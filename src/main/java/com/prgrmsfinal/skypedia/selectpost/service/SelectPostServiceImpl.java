package com.prgrmsfinal.skypedia.selectpost.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.photo.entity.Photo;
import com.prgrmsfinal.skypedia.photo.entity.SelectPostPhoto;
import com.prgrmsfinal.skypedia.photo.entity.SelectPostPhotoId;
import com.prgrmsfinal.skypedia.photo.repository.PhotoRepository;
import com.prgrmsfinal.skypedia.photo.repository.SelectPostPhotoRepository;
import com.prgrmsfinal.skypedia.photo.service.PhotoService;
import com.prgrmsfinal.skypedia.photo.service.S3Service;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostRequestDto;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostResponseDto;
import com.prgrmsfinal.skypedia.selectpost.entity.SelectPost;
import com.prgrmsfinal.skypedia.selectpost.repository.SelectPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SelectPostServiceImpl implements SelectPostService {

	private final SelectPostRepository selectPostRepository;
	private final PhotoRepository photoRepository;
	private final SelectPostPhotoRepository selectPostPhotoRepository;
	private final MemberRepository memberRepository;
	private final S3Service s3Service;
	private final PhotoService photoService;

	@Override
	@Transactional(readOnly = true)
	public SelectPostResponseDto.ListResponse readAllSelectPosts(int size) {
		Pageable pageable = PageRequest.of(0, size + 1);  // 다음 페이지 확인을 위해 size + 1
		List<SelectPost> selectPosts = selectPostRepository.findAllByOrderByCreatedAtDesc(
			LocalDateTime.now(),
			pageable
		);

		boolean hasNext = selectPosts.size() > size;
		if (hasNext) {
			selectPosts.remove(selectPosts.size() - 1);
		}

		List<SelectPostResponseDto> selectPostDtos = selectPosts.stream()
			.map(selectPost -> SelectPostResponseDto.builder()
				.selectPostId(selectPost.getId())
				.content(selectPost.getContent())
				.presignedUrls(photoRepository.findS3FileKeysBySelectPostId(selectPost.getId())
					.stream()
					.map(s3Service::getPresignedUrl)
					.collect(Collectors.toList()))
				.createdAt(selectPost.getCreatedAt())
				.author(MemberResponseDTO.Info.builder()
					.username(selectPost.getMember().getUsername())
					.profileUrl(selectPost.getMember().getProfileImage())
					.build())
				.build())
			.collect(Collectors.toList());

		return SelectPostResponseDto.ListResponse.builder()
			.selectPosts(selectPostDtos)
			.hasNext(hasNext)
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public SelectPostResponseDto readSelectPost(Long selectPostId) {
		SelectPost selectPost = selectPostRepository.findById(selectPostId)
			.orElseThrow(() -> new RuntimeException("선택 게시글을 찾을 수 없습니다."));

		List<String> presignedUrls = photoRepository.findS3FileKeysBySelectPostId(selectPostId)
			.stream()
			.map(s3Service::getPresignedUrl)
			.collect(Collectors.toList());

		return SelectPostResponseDto.builder()
			.selectPostId(selectPost.getId())
			.content(selectPost.getContent())
			.presignedUrls(presignedUrls)
			.build();
	}

	@Override
	@Transactional
	public List<PhotoResponseDTO.Info> createSelectPost(Long memberId, SelectPostRequestDto selectPostRequestDto) {
		// 회원 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

		// 선택 게시글 생성
		SelectPost selectPost = SelectPost.builder()
			.member(member)
			.content(selectPostRequestDto.getContent())
			.build();

		// 게시글 저장
		selectPost = selectPostRepository.save(selectPost);

		// PhotoService를 통해 사진 업로드 및 URL 받기
		List<PhotoResponseDTO.Info> photoInfos = photoService.createPhotoUrlList(selectPostRequestDto.getUploads());

		//photo 생성된거에서 id 끄집어오기
		List<Long> photoIds = photoInfos.stream()
			.map(PhotoResponseDTO.Info::getId)
			.collect(Collectors.toList());

		Map<Long, Photo> photoMap = photoRepository.findAllById(photoIds).stream()
			.collect(Collectors.toMap(Photo::getId, photo -> photo));

		for (Long photoId : photoIds) {
			Photo photo = photoMap.get(photoId);
			if (photo == null) {
				throw new RuntimeException("해당 ID의 사진을 찾을 수 없습니다: " + photoId);
			}

			// 복합 키 생성
			SelectPostPhotoId selectPostPhotoId = new SelectPostPhotoId();
			selectPostPhotoId.setPostId(selectPost.getId());
			selectPostPhotoId.setPhotoId(photoId);

			SelectPostPhoto selectPostPhoto = SelectPostPhoto.builder()
				.id(selectPostPhotoId)
				.selectPost(selectPost)
				.photo(photo)
				.category(determinePhotoCategory(photo))  // 카테고리 설정
				.likes(0L)  // 초기 좋아요 수 설정
				.build();

			selectPostPhotoRepository.save(selectPostPhoto);
		}

		// 응답 DTO 생성
		return photoInfos;
	}

	@Override
	@Transactional
	public SelectPostResponseDto updateSelectPost(Long selectPostId, Long memberId, SelectPostRequestDto requestDto) {
		// 게시글 조회
		SelectPost selectPost = selectPostRepository.findById(selectPostId)
			.orElseThrow(() -> new RuntimeException("선택 게시글을 찾을 수 없습니다."));
		// 작성자 확인
		if (!selectPost.getMember().getId().equals(memberId)) {
			throw new RuntimeException("게시글 수정 권한이 없습니다.");
		}

		// 내용 업데이트
		selectPost.setContent(requestDto.getContent());

		// 기존 사진 연결 삭제
		selectPostPhotoRepository.deleteBySelectPostId(selectPostId);

		// 새로운 사진 업로드
		List<PhotoResponseDTO.Info> photoInfos = photoService.uploadPhotosForPost(
			selectPost.getId(),
			requestDto.getUploads()
		);

		// presigned URL 목록 생성
		List<String> presignedUrls = photoInfos.stream()
			.map(PhotoResponseDTO.Info::getPhotoUrl)
			.collect(Collectors.toList());

		return SelectPostResponseDto.builder()
			.selectPostId(selectPost.getId())
			.content(selectPost.getContent())
			.presignedUrls(presignedUrls)
			.build();
	}

	@Override
	@Transactional
	public void deleteSelectPost(Long selectPostId, Long memberId) {
		SelectPost selectPost = selectPostRepository.findById(selectPostId)
			.orElseThrow(() -> new RuntimeException("선택 게시글을 찾을 수 없습니다."));

		// 작성자 확인
		if (!selectPost.getMember().getId().equals(memberId)) {
			throw new RuntimeException("게시글 삭제 권한이 없습니다.");
		}

		// 연관된 사진 연결 먼저 삭제
		selectPostPhotoRepository.deleteBySelectPostId(selectPostId);

		// 게시글 삭제
		selectPostRepository.delete(selectPost);
	}

	public String determinePhotoCategory(Photo photo) {
		// 사진 카테고리 결정 로직 (예: 인물/배경)
		// 실제 구현은 요구사항에 맞게 수정 필요
		return photo.getId() % 2 == 0 ? "인물" : "배경";
	}

}