package com.kidami.security.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    String saveFile(MultipartFile file) throws IOException;
    Resource loadFile(String fileName) throws IOException;
    List<String> listFiles() throws IOException;
    boolean deleteFile(String fileName) throws IOException;
}