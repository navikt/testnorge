package no.nav.dolly.synt.aap.bucket;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@UtilityClass
@Slf4j
public class BucketModels {

    public static Path get(String bucket, List<String> models, String tempDirPrefix)
            throws Exception {

        var started = Instant.now();
        var targetDir = Files.createTempDirectory(tempDirPrefix);
        targetDir.toFile().deleteOnExit();

        try (var storage = StorageOptions.getDefaultInstance().getService()) {
            var downloaded = downloadModelsToDirectory(bucket, models, storage, targetDir);
            if (downloaded == 0) {
                throw new IllegalStateException("No ONNX model files found in GCS bucket: " + bucket);
            }
            var durationInMillis = Duration.between(started, Instant.now()).toMillis();
            log.info("Downloaded {} model(s) from bucket {} to {} in {}ms", downloaded, bucket, targetDir, durationInMillis);
        }

        return targetDir;

    }

    private int downloadModelsToDirectory(String bucket, List<String> models, Storage storage, Path targetDir)
            throws IOException {

        var downloadedFiles = 0;
        for (var modelPrefix : models) {
            var prefixedBlobs = storage.list(bucket, Storage.BlobListOption.prefix(modelPrefix));
            for (var blob : prefixedBlobs.iterateAll()) {
                if (blob.getName().endsWith(".onnx")) {
                    downloadBlob(blob, targetDir);
                    downloadedFiles++;
                }
            }
        }
        return downloadedFiles;

    }

    private void downloadBlob(Blob blob, Path targetDir)
            throws IOException {

        var filename = blob.getName().substring(blob.getName().lastIndexOf('/') + 1);
        var targetPath = targetDir.resolve(filename);
        try (var readChannel = blob.reader()) {
            var in = Channels.newInputStream(readChannel);
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        log.debug("Downloaded model {}", filename);

    }
}

