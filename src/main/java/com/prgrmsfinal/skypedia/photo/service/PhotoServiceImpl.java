package com.prgrmsfinal.skypedia.photo.service;

import com.prgrmsfinal.skypedia.photo.dto.PhotoDTO;
import com.prgrmsfinal.skypedia.photo.entity.Photo;
import com.prgrmsfinal.skypedia.photo.exception.PhotoException;
import com.prgrmsfinal.skypedia.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final S3Service s3Service;


    // ---------------------------------- CREATE ----------------------------------

    //하나의 URL create요청 <- 솔직히 필요없는듯
    @Override
    public String createPhotoURL(PhotoDTO photoDTO) {
        String uuid = UUID.randomUUID().toString();
        if(checkContentType(photoDTO)){
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
        if(photoId == null){
            throw PhotoException.NOT_FOUND.get();
        }
        return s3Service.getPresignedUrl(photoRepository.findS3FileKeyById(photoId));
    }

    // 여러개의 read요청 List로 id받기
    @Override
    public List<String> readPhotoUrlList(List<Long> photoIds) {
        List<String> photoUrls = new ArrayList<>();
        for(Long photoId : photoIds){
            if(photoId == null){
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

            if(id == null){
                throw PhotoException.NOT_FOUND.get();
            }
            photoRepository.updatePhotoDetails(id, originalFileName, cont);
            photoUrls.add(s3Service.modifyPresignedURL(photoRepository.findS3FileKeyById(id)));

        }

        return photoUrls;
    }


    // ----------------------------------DELETE----------------------------------------

    @Override
    public void deletePhoto(String photoId) {
        //soft-del로 수정하기 위해, 로직을 비워두었습니다.
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
        if(!allowedTypes.contains(cont)){
            throw PhotoException.CONTENT_ERROR.get();
        }
        return allowedTypes.contains(cont);
    }
}




















