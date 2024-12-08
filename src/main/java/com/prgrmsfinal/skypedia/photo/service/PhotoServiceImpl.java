package com.prgrmsfinal.skypedia.photo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.prgrmsfinal.skypedia.photo.dto.PhotoDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.photo.entity.Photo;
import com.prgrmsfinal.skypedia.photo.entity.PostPhoto;
import com.prgrmsfinal.skypedia.photo.entity.PostPhotoId;
import com.prgrmsfinal.skypedia.photo.exception.PhotoException;
import com.prgrmsfinal.skypedia.photo.repository.PhotoRepository;
import com.prgrmsfinal.skypedia.photo.repository.PostPhotoRepository;
import com.prgrmsfinal.skypedia.post.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PhotoServiceImpl implements PhotoService {

	private final PhotoRepository photoRepository;
	private final PostRepository postRepository;
	private final PostPhotoRepository postPhotoRepository;
	private final S3Service s3Service;

	// ---------------------------------- CREATE ----------------------------------

	//하나의 URL create요청 <- 솔직히 필요없는듯
	@Override
	public String createPhotoURL(PhotoDTO photoDTO) {
		String uuid = UUID.randomUUID().toString();
		if (checkContentType(photoDTO)) {
			List<Photo> photo = new ArrayList<>();
			photo.add(Photo.builder()
				.contentType(photoDTO.getContentType().toString().toLowerCase())
				.originalFileName(photoDTO.getOriginalFileName())
				.uuid(uuid)
				.s3FileKey("test/" + uuid)
				.build());

			photoRepository.saveAll(photo);
		}

		return s3Service.createPresignedURL("test/" + uuid);
	}

	// 여러개의 URL create요청
	@Override
	public List<String> createPhotoUrlList(List<PhotoDTO> photoDTOs) {
		List<String> photoUrls = new ArrayList<>();

		for (PhotoDTO photoDTO : photoDTOs) {
			String uuid = UUID.randomUUID().toString();

			if (checkContentType(photoDTO)) {
				// Photo 객체 생성
				Photo photo = Photo.builder()
					.contentType(photoDTO.getContentType().toString().toLowerCase())
					.originalFileName(photoDTO.getOriginalFileName())
					.uuid(uuid)
					.s3FileKey("test/" + uuid)
					.build();

				// DB 저장
				photoRepository.save(photo);

				// S3 URL 생성
				photoUrls.add(s3Service.createPresignedURL("test/" + uuid));
			}
		}

		return photoUrls;
	}

	// ------------------------------------ READ --------------------------------------

	// 한개의 read요청 <- 솔직히 필요없는듯
	@Override
	public String readPhotoURL(Long photoId) {
		if (photoId == null) {
			throw PhotoException.NOT_FOUND.get();
		}
		return s3Service.getPresignedUrl(photoRepository.findS3FileKeyById(photoId));
	}

	// 여러개의 read요청 List로 id받기
	@Override
	public List<String> readPhotoUrlList(List<Long> photoIds) {
		List<String> photoUrls = new ArrayList<>();
		for (Long photoId : photoIds) {
			if (photoId == null) {
				throw PhotoException.NOT_FOUND.get();
			}
			String s3FileKey = photoRepository.findS3FileKeyById(photoId);
			photoUrls.add(s3Service.getPresignedUrl(s3FileKey));
		}
		return photoUrls;
	}

	// ------------------------------------- MODIFY -----------------------------------------

	//id값에 해당하는 contentType을 바뀐 변수로 수정
	//id값에 해당하는 originalFileName을 바뀐 변수로 수정

	@Override
	public List<String> modifyPhotoUrlList(List<PhotoDTO> photoDTOs) {
		List<String> photoUrls = new ArrayList<>();

		for (PhotoDTO photoDTO : photoDTOs) {
			Long id = photoDTO.getId();
			String cont = photoDTO.getContentType().toString().toLowerCase();
			String originalFileName = photoDTO.getOriginalFileName();

			if (id == null) {
				throw PhotoException.NOT_FOUND.get();
			}
			photoRepository.updatePhotoDetails(id, originalFileName, cont);
			photoUrls.add(s3Service.modifyPresignedURL(photoRepository.findS3FileKeyById(id)));

		}

		return photoUrls;
	}

	// ------ POST | READ ------
	// Post ID로 해당 게시글의 모든 사진 정보 조회
	public List<PhotoResponseDTO.Info> readPhotosByPostId(Long postId) {
		List<Photo> photos = photoRepository.findPhotosByPostId(postId);
		return photos.stream()
			.map(photo -> PhotoResponseDTO.Info.builder()
				.photoUrl(s3Service.getPresignedUrl(photo.getS3FileKey()))
				.build())
			.collect(Collectors.toList());
	}

	// ----- POST | POST ------
	@Transactional
	public List<PhotoResponseDTO.Info> uploadPhotosForPost(Long postId, List<PhotoRequestDTO.Upload> uploads) {
		if (uploads == null || uploads.isEmpty()) {
			return new ArrayList<>();
		}

		return uploads.stream()
			.map(upload -> uploadSinglePhotoForPost(postId, upload))
			.collect(Collectors.toList());
	}

	@Transactional
	public PhotoResponseDTO.Info uploadSinglePhotoForPost(Long postId, PhotoRequestDTO.Upload upload) {
		// Photo 엔티티 생성 및 저장
		Photo photo = Photo.builder()
			.uuid(UUID.randomUUID().toString())
			.originalFileName(upload.getOriginalFileName())
			.contentType(upload.getContentType())
			.s3FileKey("posts/" + postId + "/" + UUID.randomUUID().toString())
			.build();
		photo = photoRepository.save(photo);

		// PostPhoto 연결 엔티티 생성 및 저장
		PostPhoto postPhoto = new PostPhoto();
		postPhoto.setId(new PostPhotoId(postId, photo.getId()));
		postPhoto.setPost(postRepository.getReferenceById(postId));
		postPhoto.setPhoto(photo);
		postPhotoRepository.save(postPhoto);

		// Presigned URL 생성 및 응답
		return PhotoResponseDTO.Info.builder()
			.photoUrl(s3Service.getPresignedUrl(photo.getS3FileKey()))
			.build();
	}

	// ---- POST | DEL -----

	// Post에서 특정 사진들 삭제
	@Transactional
	public void deletePhotosFromPost(Long postId, List<Long> photoIds) {
		if (photoIds != null && !photoIds.isEmpty()) {
			postPhotoRepository.deleteByPostIdAndPhotoIdIn(postId, photoIds);
		}
	}

	// --------------------------- CHECKCONTENTTYPE -------------------------------

	@Override
	public boolean checkContentType(PhotoDTO photoDTO) {
		var cont = photoDTO.getContentType().toString().toLowerCase();

		List<String> allowedTypes = Arrays.asList(
			"image/jpeg",
			"image/jpg",
			"image/png",
			"image/webp",
			"image/avif",
			"image/heif"
		);
		if (!allowedTypes.contains(cont)) {
			throw PhotoException.CONTENT_ERROR.get();
		}
		return allowedTypes.contains(cont);
	}
}
