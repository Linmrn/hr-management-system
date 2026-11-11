package net.xju.controller;

import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileController {

    private static final String UPLOAD_DIRECTORY = "uploads";

    @RequestMapping(value="/file/upload.do", methods={"POST", "PUT", "DELETE"})
    public String upload(HttpServletRequest request) {
        try {
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                return "json:" + JSONUtil.toJson(Result.fail("请选择要上传的图片"));
            }

            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return "json:" + JSONUtil.toJson(Result.fail("只能上传图片文件"));
            }

            String extension = getExtension(filePart.getSubmittedFileName());
            if (!".jpg".equals(extension) && !".jpeg".equals(extension) && !".png".equals(extension)) {
                return "json:" + JSONUtil.toJson(Result.fail("图片必须是 JPG 或 PNG 格式"));
            }

            File uploadDirectory = getUploadDirectory(request);
            if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
                throw new IOException("无法创建上传目录");
            }

            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            File targetFile = new File(uploadDirectory, fileName);
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            return "json:" + JSONUtil.toJson(Result.success("/file/image.do?name=" + fileName));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("图片上传失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/file/image.do")
    public void image(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getParameter("name");
        if (fileName == null || !fileName.matches("[a-fA-F0-9]{32}\\.(jpg|jpeg|png)")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File imageFile = new File(getUploadDirectory(request), fileName);
        if (!imageFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(Files.probeContentType(imageFile.toPath()));
        response.setContentLengthLong(imageFile.length());
        try (InputStream input = Files.newInputStream(imageFile.toPath());
             OutputStream output = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        }
    }

    private File getUploadDirectory(HttpServletRequest request) {
        return new File(request.getServletContext().getRealPath("/"), UPLOAD_DIRECTORY);
    }

    private String getExtension(String originalFileName) {
        if (originalFileName == null) return "";
        int dotIndex = originalFileName.lastIndexOf('.');
        return dotIndex < 0 ? "" : originalFileName.substring(dotIndex).toLowerCase();
    }
}
