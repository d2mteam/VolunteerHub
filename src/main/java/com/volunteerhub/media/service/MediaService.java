package com.volunteerhub.media.service;

import com.volunteerhub.media.dto.request.ConfirmUploadRequest;
import com.volunteerhub.media.dto.request.UploadTicketRequest;
import com.volunteerhub.media.dto.response.ConfirmUploadResponse;
import com.volunteerhub.media.dto.response.DownloadUrlResponse;
import com.volunteerhub.media.dto.response.UploadTicketResponse;
import com.volunteerhub.media.model.MediaResource;
import com.volunteerhub.media.model.MediaStatus;
import com.volunteerhub.media.repository.MediaResourceRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final MinioClient minioClient;
    private final MediaResourceRepository mediaResourceRepository;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.presigned-expiry-seconds:300}")
    private int presignedExpirySeconds;

    public UploadTicketResponse createUploadTicket(UUID userId, UploadTicketRequest request) {
        if (request.getSizeBytes() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid media size");
        }

        UUID resourceId = UUID.randomUUID();
        String objectKey = "media/%s".formatted(resourceId);
        String uploadUrl = getPresignedUrl(objectKey, Method.PUT);

        return UploadTicketResponse.builder()
                .resourceId(resourceId)
                .uploadUrl(uploadUrl)
                .method(Method.PUT.name())
                .expiresInSeconds(presignedExpirySeconds)
                .contentType(request.getContentType())
                .build();
    }

    public ConfirmUploadResponse confirmUpload(UUID userId, ConfirmUploadRequest request) {
        UUID resourceId = request.getResourceId();
        String objectKey = "media/%s".formatted(resourceId);

        MediaResource existing = mediaResourceRepository.findById(resourceId).orElse(null);
        if (existing != null && !existing.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Media owned by another user");
        }

        StatObjectResponse stat = statObject(objectKey);

        MediaResource resource = MediaResource.builder()
                .id(resourceId)
                .bucket(bucket)
                .objectKey(objectKey)
                .contentType(stat.contentType())
                .sizeBytes(stat.size())
                .checksum(stat.etag())
                .ownerId(userId)
                .status(MediaStatus.TEMP)
                .build();

        mediaResourceRepository.save(resource);

        return ConfirmUploadResponse.builder()
                .resourceId(resourceId)
                .status(resource.getStatus())
                .contentType(resource.getContentType())
                .sizeBytes(resource.getSizeBytes())
                .checksum(resource.getChecksum())
                .build();
    }

    public DownloadUrlResponse getDownloadUrl(UUID userId, UUID resourceId) {
        MediaResource resource = mediaResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));
        if (resource.getStatus() == MediaStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.GONE, "Media deleted");
        }
        if (!resource.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        String url = getPresignedUrl(resource.getObjectKey(), Method.GET);
        return DownloadUrlResponse.builder()
                .downloadUrl(url)
                .method(Method.GET.name())
                .expiresInSeconds(presignedExpirySeconds)
                .build();
    }

    private String getPresignedUrl(String objectKey, Method method) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(presignedExpirySeconds)
                            .method(method)
                            .build()
            );
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate URL", ex);
        }
    }

    private StatObjectResponse statObject(String objectKey) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uploaded media not found", ex);
        }
    }
}
