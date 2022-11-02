package com.clowneon1.awsfilesapi.controller;

import com.clowneon1.awsfilesapi.service.StorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/files")
public class storageController {
    public storageController(StorageService service) {
        this.service = service;
    }

    private StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file){
        return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String filename){
        byte[] data = service.downloadFile(filename);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok().contentLength(data.length)
                .header("content-type", "application/octet-stream")
                .header("content-disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename){
        return new ResponseEntity<>(service.deleteFile(filename),HttpStatus.OK);
    }
}
