package ru.nasrulaev.cloudfilestorage.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUpload {
    private List<MultipartFile> files;

    public FileUpload(List<MultipartFile> files) {
        this.files = files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }
}
