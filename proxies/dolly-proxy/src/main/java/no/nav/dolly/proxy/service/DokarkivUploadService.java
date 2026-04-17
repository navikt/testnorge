package no.nav.dolly.proxy.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivUploadService {

    private final Cache<String, StringBuffer> dokarkivUploadCache;

    public String initUpload() {
        var uploadId = UUID.randomUUID().toString();
        dokarkivUploadCache.put(uploadId, new StringBuffer());
        log.info("Dokarkiv upload startet med id {}", uploadId);
        return uploadId;
    }

    public void appendChunk(String uploadId, String chunk) {
        var builder = dokarkivUploadCache.getIfPresent(uploadId);
        if (isNull(builder)) {
            throw new IllegalArgumentException("Ukjent upload id: " + uploadId);
        }
        builder.append(chunk);
    }

    public String resolveUpload(String uploadId) {
        var builder = dokarkivUploadCache.getIfPresent(uploadId);
        if (isNull(builder)) {
            throw new IllegalArgumentException("Ukjent upload id: " + uploadId);
        }
        var content = builder.toString();
        dokarkivUploadCache.invalidate(uploadId);
        log.info("Dokarkiv upload {} resolved med {} tegn (~{} MB)",
                uploadId, content.length(),
                String.format("%.1f", content.length() / (1024.0 * 1024.0)));
        return content;
    }

}
