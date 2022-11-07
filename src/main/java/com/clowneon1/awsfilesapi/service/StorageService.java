package com.clowneon1.awsfilesapi.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.clowneon1.awsfilesapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    private AmazonS3 s3Client;

    public StorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, User user) throws AmazonServiceException {

        Map<String,String> metaList = new HashMap<>();
        metaList.put("name", user.getName());
        metaList.put("email",user.getEmail());
        metaList.put("contact-info", user.getContactInfo());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(metaList);

        File fileObj = convertMultipartFileToFile(file);
        String filename =  System.currentTimeMillis() + "_" + file.getOriginalFilename();

        s3Client.putObject(new PutObjectRequest(bucketName,filename,fileObj).withMetadata(metadata));
        fileObj.delete();
        return "File uploaded : " + filename;
    }

    public byte[] downloadFile(String filename){
        S3Object s3Object =  s3Client.getObject(bucketName,filename);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try{
            byte [] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteFile(String filename){
        s3Client.deleteObject(bucketName,filename);
        return filename + " removed...";
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("error converting multipart file to file", e);
        }
        return convertedFile;
    }
}
