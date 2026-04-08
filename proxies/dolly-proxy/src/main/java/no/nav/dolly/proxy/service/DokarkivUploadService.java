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

    private final Cache<String, StringBuilder> dokarkivUploadCache;

    public String initUpload() {
        var uploadId = UUID.randomUUID().toString();
        dokarkivUploadCache.put(uploadId, new StringBuilder());
        log.info("Dokarkiv upload startet med id {}", uploadId);
        return uploadId;
    }

    public void appendChunk(String uploadId, String chunk) {
        var builder = dokarkivUploadCache.getIfPresent(uploadId);
        if (isNull(builder)) {
            throw new IllegalArgumentException("Ukjent upload id: " + uploadId);
        }
        if (builder.isEmpty() && chunk.length() >= 20) {
            log.info("Dokarkiv upload {} foerste chunk starter med: '{}'", uploadId, chunk.substring(0, 20));
        }
        builder.append(chunk);
        log.info("Dokarkiv upload {} mottok chunk paa {} tegn, totalt {} tegn", uploadId, chunk.length(), builder.length());
    }

    public String resolveUpload(String uploadId) {
        var builder = dokarkivUploadCache.getIfPresent(uploadId);
        if (isNull(builder)) {
            throw new IllegalArgumentException("Ukjent upload id: " + uploadId);
        }
        var content = builder.toString();
        dokarkivUploadCache.invalidate(uploadId);
        log.info("Dokarkiv upload {} resolved med {} tegn (~{} MB), starter med: '{}'",
                uploadId, content.length(),
                String.format("%.1f", content.length() / (1024.0 * 1024.0)),
                content.length() >= 20 ? content.substring(0, 20) : content);
        return content;
    }

}
