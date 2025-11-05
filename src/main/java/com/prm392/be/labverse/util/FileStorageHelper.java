package com.prm392.be.labverse.util;

import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.CommonErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileStorageHelper {
    S3Client s3Client;
    S3Presigner s3Presigner;

    @NonFinal
    @Value("${aws.s3.buckets.name}")
    String bucketName;

    private static final String PREFIX_PAPER = "/paper/";
    private static final String PREFIX_ANNOTATION= "/annotation/";
    private static final String PREFIX_AVATAR = "/avatar/";
    private Random random = new Random();


    public String generateUploadPaperUrl(String key){
        //tạm thời liều, để client tự định nghĩa key
        // String key = userId + PREFIX_PAPER + fileName;
        return generateUploadFileUrl(key, MediaType.APPLICATION_PDF_VALUE);
    }

    public String generateUploadAnnotationUrl(String key){
        return generateUploadFileUrl(key, MediaType.APPLICATION_JSON_VALUE);
    }
    /**
     *
     * @param file the file which would be uploaded
     * @param userId the userId of the user who upload the file
     * @return a String which is uri to save to the database
     */
    public String uploadAvatar(MultipartFile file, String userId) {
        String key = userId + PREFIX_AVATAR + getFileName(file);
        uploadFile(file, key);
        return key;
    }

    /**
     * Uploads a file to the specified S3 bucket with the given key.
     *
     * @param file   the file to be uploaded (as MultipartFile)
     * @param s3Key    the  key (path/filename) under which the file will be stored in the S3 bucket
     * @return true if successfully upload file
     * @throws AppException if there is an error during the file upload process
     */
    private void uploadFile(MultipartFile file, String s3Key) {
        //todo choox nafy vaan synchronous, cos the lau
        //upload object to s3
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("Upload file {} to S3 successful", s3Key);
        } catch (IOException e) {
            log.info("Upload file {} to S3 failed", s3Key);
            throw new AppException(CommonErrorCode.UPLOAD_OBJECT_TO_S3_FAIL);
        }
    }

    /**
     * Generates a presigned URL for accessing a file stored in the S3 bucket.
     * This URL is temporary and valid for 30 minutes.
     *
     * @param s3Key the key (path/filename) of the file stored in the S3 bucket
     * @return the presigned URL as a String
     */
    public String generateDownloadFileUrl(String s3Key) {
        String fileName = extractFileName(s3Key);
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        //chuẩn bị thông tin cần presign
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                //ép trình duyệt tải file với tên gốc
                .responseContentDisposition("attachment; filename=\"" + encodedName + "\"")
                .build();

        //gói thông tin cần presign vào presign request
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(30)) //allow this url to be access in 30
                //todo
                .signatureDuration(Duration.ofMinutes(60)) //allow this url to be access in 30
                .getObjectRequest(getObjectRequest)
                .build();
        log.info("Get url of the file with the key={} successful", s3Key);
        //tạo link đã kí và lấy url thực tế
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    /**
     * Extract the file name from url path
     */
    private String extractFileName(String key) {
        if (key == null || key.isEmpty()) return "file";
        return key.substring(key.lastIndexOf('/') + 1);
    }

    /**
     * get the valid file name to upload to S3
     * @param file the file which would be uploaded
     * @return a String as the file name
     */
    private String getFileName(MultipartFile file){
        String originalFileName = file.getOriginalFilename();
        // Nếu không có tên → fallback mặc định
        if (originalFileName == null || originalFileName.isBlank()) return "file.bin";

        //Sanitize toàn bộ tên trước (tránh ký tự lạ ảnh hưởng đến substring)
        // \p{L}   tất cả ký tự chữ cái Unicode (Latin có dấu, Nhật, Trung, Hàn, v.v.)
        // \p{N}   tất cả chữ số Unicode
        // ._-   vẫn giữ lại dấu chấm, gạch dưới, gạch ngang như trước
        // [^...]   nghĩa là bất kỳ ký tự nào KHÔNG thuộc nhóm này sẽ bị thay bằng _
        originalFileName = originalFileName.replaceAll("[^\\p{L}\\p{N}._-]", "_");

        //Kiểm tra có extension hay không
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == originalFileName.length() - 1) {
            // không có hoặc "." ở cuối → thêm fallback .bin
            return originalFileName + ".bin";
        }

        //Có extension hợp lệ → return nguyên tên
        return originalFileName;
    }

    private String generateUploadFileUrl(String s3Key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10)) //mo link cho upload trong 10 phuts
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }
}
