package no.nav.dolly.synt.dagpenger.bucket;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@UtilityClass
@Slf4j
public class BucketUtils {

    /**
     * Downloads ONNX model files from a Google Cloud Storage bucket to a temporary directory.
     * <p>
     * This method connects to GCS using Application Default Credentials, filters files by the provided
     * model prefixes, and downloads matching {@code .onnx} files to a new temporary directory.
     * The temp directory is marked for automatic deletion on JVM exit.
     * <p>
     * A blob is downloaded if its filename (trailing path segment) starts with one of the provided model prefixes.
     * For example, with prefix {@code "model_PERM"}, the file
     * {@code "model_PERM_bean_model_distributions_col_32.onnx"} will match.
     *
     * @param bucket        the GCS bucket name; must exist and be readable with current credentials
     * @param models        list of model filename prefixes to match; at least one must result in a matching file
     * @param tempDirPrefix prefix for the temporary directory name (e.g., {@code "myapp-models-"});
     *                      the OS will append random characters to ensure uniqueness
     * @return a {@link Path} pointing to the temporary directory containing downloaded models;
     * guaranteed to be non-empty
     * @throws IOException           if the GCS bucket cannot be accessed, no files match the prefixes,
     *                               or file I/O operations fail
     * @throws IllegalStateException if no ONNX model files are found in the bucket matching
     *                               any of the provided prefixes
     * @see StorageOptions#getDefaultInstance()
     */
    public Path download(String bucket, List<String> models, String tempDirPrefix)
            throws IOException {

        var targetDir = Files.createTempDirectory(tempDirPrefix);
        targetDir.toFile().deleteOnExit();

        try (var storage = StorageOptions.getDefaultInstance().getService()) {
            var downloaded = downloadModelsToDirectory(bucket, models, storage, targetDir);
            if (downloaded == 0) {
                throw new IllegalStateException("No ONNX model files found in GCS bucket: " + bucket);
            }
            log.info("Downloaded {} model(s) from GCS bucket {} to {}", downloaded, bucket, targetDir);
        } catch (Exception e) {
            throw new IOException("Failed to download from GCS bucket %s".formatted(bucket), e);
        }

        return targetDir;

    }

    private int downloadModelsToDirectory(String bucket, List<String> models, Storage storage, Path targetDir)
            throws IOException {

        var matchedFiles = 0;
        for (var blob : storage.list(bucket).iterateAll()) {
            if (shouldDownload(models, blob.getName())) {
                downloadBlob(blob, targetDir);
                matchedFiles++;
            }
        }
        return matchedFiles;

    }

    private boolean shouldDownload(List<String> models, String blobName) {

        if (!blobName.endsWith(".onnx")) {
            return false;
        }
        var filename = blobName.substring(blobName.lastIndexOf('/') + 1);
        return models.stream().anyMatch(filename::startsWith);

    }

    private void downloadBlob(Blob blob, Path targetDir)
            throws IOException {

        var filename = blob.getName().substring(blob.getName().lastIndexOf('/') + 1);
        var targetPath = targetDir.resolve(filename);
        try (var in = new ByteArrayInputStream(blob.getContent())) {
            Files.copy(in, targetPath);
            log.debug("Downloaded model: {}", filename);
        }

    }

}
