package no.nav.dolly.synt.dagpenger.models;

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

    /**
     * Downloads ONNX model files from a Google Cloud Storage bucket to a temporary directory.
     * <p>
     * This method connects to GCS using Application Default Credentials and, for each configured
     * model prefix, performs prefix-based blob listing in the bucket.
     * Matching {@code .onnx} files are downloaded to a new temporary directory.
     * The temp directory is marked for automatic deletion on JVM exit.
     * <p>
     * A blob is considered for download when its full blob name starts with one of the provided model prefixes
     * and the blob name ends with {@code .onnx}.
     * For example, with prefix {@code "model_PERM"}, the file
     * {@code "model_PERM_bean_model_distributions_col_32.onnx"} will match.
     *
     * @param bucket        The GCS bucket name; must exist and be readable with current credentials.
     * @param models        A list of blob-name prefixes to list and match; at least one must result in a matching file.
     * @param tempDirPrefix Prefix for the temporary directory name, e.g. {@code "myapp-models-"}.
     * @return A {@link Path} pointing to the temporary directory containing downloaded models..
     * @throws IOException           If the GCS bucket cannot be accessed, no files match the prefixes,
     *                               or file I/O operations fail.
     * @throws IllegalStateException If no ONNX model files are found in the bucket matching
     *                               any of the provided prefixes.
     * @see StorageOptions#getDefaultInstance()
     */
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
            log.info("Downloaded {} model(s) from GCS bucket {} to {} in {}ms", downloaded, bucket, targetDir, durationInMillis);
        }

        return targetDir;

    }

    private int downloadModelsToDirectory(String bucket, List<String> models, Storage storage, Path targetDir)
            throws IOException {

        var matchedFiles = 0;
        for (var model : models) {
            var prefixedBlobs = storage.list(bucket, Storage.BlobListOption.prefix(model));
            for (var blob : prefixedBlobs.iterateAll()) {
                if (blob.getName().endsWith(".onnx")) {
                    downloadBlob(blob, targetDir);
                    matchedFiles++;
                }
            }
        }
        return matchedFiles;

    }

    private void downloadBlob(Blob blob, Path targetDir)
            throws IOException {

        var filename = blob
                .getName()
                .substring(blob.getName().lastIndexOf('/') + 1);
        var targetPath = targetDir.resolve(filename);
        try (var readChannel = blob.reader()) {
            var in = Channels.newInputStream(readChannel);
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        log.debug("Downloaded model {}", filename);

    }

}
