package no.nav.dolly.synt.tilleggsstonad.models;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.util.Objects.isNull;

@UtilityClass
@Slf4j
public class BucketModels {

    private static final String MODEL_SUFFIX = ".json.gz";

    public static Path get(String bucket, List<String> models, String tempDirPrefix)
            throws Exception {

        if (isNull(models) || models.isEmpty()) {
            throw new IllegalStateException("No tilleggsstonad models configured for GCS bucket: " + bucket);
        }
        var started = Instant.now();
        var targetDir = Files.createTempDirectory(tempDirPrefix);
        targetDir.toFile().deleteOnExit();

        try (var storage = StorageOptions.getDefaultInstance().getService()) {
            for (var model : models) {
                downloadModel(storage, bucket, model + MODEL_SUFFIX, targetDir);
            }
        }
        log.info("Downloaded {} model(s) from GCS bucket {} to {} in {}ms", models.size(), bucket, targetDir,
                Duration.between(started, Instant.now()).toMillis());
        return targetDir;

    }

    private static void downloadModel(Storage storage, String bucket, String blobName, Path targetDir) {

        var blob = storage.get(BlobId.of(bucket, blobName));
        if (isNull(blob)) {
            throw new IllegalStateException("Model file %s not found in GCS bucket %s".formatted(blobName, bucket));
        }
        blob.downloadTo(targetDir.resolve(blobName));
        log.debug("Downloaded model {}", blobName);

    }

}
