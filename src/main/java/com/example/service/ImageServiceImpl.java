package com.example.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageServiceImpl implements ImageService{

    private final S3Config s3Config;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String localLocation = "C:\\Users\\tlsgh\\Desktop\\s3image\\";

    @Override
    public String imageUpload(MultipartFile imageFile) throws IOException {


        // 추출한 이미지에서 파일 이름과 확장자를 추출
        String fileName = imageFile.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));

        //이미지 파일 이름 유일성을 위한 UUID 생성
        String uuidFileName = UUID.randomUUID() + extension;

        //서버환경에 저장할 로컬 경로
        String localPath = localLocation + uuidFileName;


        //서버환경에서 이미지를 로컬 경로에 저장
        File localFile = new File(localPath);
        imageFile.transferTo(localFile);

        //로컬 경로에 저장된 이미지를 S3에 업로드
        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuidFileName, localFile).withCannedAcl(CannedAccessControlList.PublicRead));
        String s3Url = s3Config.amazonS3Client().getUrl(bucket, uuidFileName).toString();

        //로컬 경로에 저장된 이미지 삭제
        localFile.delete();

        return s3Url;
    }
}
