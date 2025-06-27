package com.zbkj.common.request;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class Base64ToMultipartFile implements MultipartFile {

    private final byte[] content;
    private final String fileName;
    private final String contentType;

    public Base64ToMultipartFile(String base64String, String fileName) throws IOException {
        // 去除Base64编码数据中的前缀（如果有的话，例如"data:image/png;base64,"等格式标识部分）
        String base64Data = base64String.split(",")[1];

        // 解码Base64数据为字节数组
        this.content = Base64.getDecoder().decode(base64Data);
        this.fileName = fileName;

        // 获取文件名后缀，用于设置MultipartFile的内容类型（简单示例，可根据实际完善）
        String fileExtension = getFileExtension(fileName);
        this.contentType = getContentTypeFromExtension(fileExtension);
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            IOUtils.copy(inputStream, new java.io.FileOutputStream(dest));
        }
    }

    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {
            throw new IllegalArgumentException("文件名缺少后缀");
        }
        return fileName.substring(lastIndex);
    }

    private static String getContentTypeFromExtension(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".png":
                return "image/png";
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }

    public static void main(String[] args) {
        String base64String = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
        String fileName = "example.png";
        try {
            MultipartFile multipartFile = new Base64ToMultipartFile(base64String, fileName);
            System.out.println("转换成功，MultipartFile对象创建完成，文件名: " + multipartFile.getOriginalFilename() +
                    ", 内容类型: " + multipartFile.getContentType());
        } catch (IOException e) {
            System.err.println("转换失败: " + e.getMessage());
        }
    }
}