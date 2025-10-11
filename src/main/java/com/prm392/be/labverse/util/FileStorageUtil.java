package com.prm392.be.labverse.util;

import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.CommonErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileStorageUtil {
    S3Client s3Client;
    S3Presigner s3Presigner;

    @NonFinal
    @Value("${aws.s3.buckets.name}")
    String bucketName;

    private static final String PREFIX_PAPER = "/paper/";
    private static final String PREFIX_AVATAR = "/avatar/";
    private Random random = new Random();

    /**
     *
     * @param file the file which would be uploaded
     * @param userId the userId of the user who upload the file
     * @return a String which is uri to save to the database
     */
    public String uploadPaper(MultipartFile file, String userId) {
        //timestamp hiện tại (theo millis)
        long timestamp = System.currentTimeMillis();
        //3 ký tự random để tránh trùng khi upload cùng millis
        int randomInt = random.nextInt(50);  // returns pseudo-random value between 0 and 50
        String key = userId + PREFIX_PAPER + timestamp+ "_" + randomInt + "_" + getFileName(file);
        uploadFile(file, key);
        //todo choox nafy vaan synchronous, cos the lau
        return key;
    }


    /**
     * Uploads a file to the specified S3 bucket with the given key.
     *
     * @param file   the file to be uploaded (as MultipartFile)
     * @param key    the  key (path/filename) under which the file will be stored in the S3 bucket
     * @return true if successfully upload file
     * @throws AppException if there is an error during the file upload process
     */
    private void uploadFile(MultipartFile file, String key) {
        //upload object to s3
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("Upload file {} to S3 successful", key);
        } catch (IOException e) {
            log.info("Upload file {} to S3 failed", key);
            throw new AppException(CommonErrorCode.UPLOAD_OBJECT_TO_S3_FAIL);
        }
    }

    /**
     * Generates a presigned URL for accessing a file stored in the S3 bucket.
     * This URL is temporary and valid for 30 minutes.
     *
     * @param uri the key (path/filename) of the file stored in the S3 bucket
     * @return the presigned URL as a String
     */
    public String getFileUrl(String uri) {
        String fileName = extractFileName(uri);
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(uri)
                //ép trình duyệt tải file với tên gốc
                .responseContentDisposition("attachment; filename=\"" + encodedName + "\"")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30)) //allow this url to be access in 30
                .getObjectRequest(getObjectRequest)
                .build();
        log.info("Get url of the file with the key={} successful", uri);
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    /**
     * Extract the file name from key path
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

}
