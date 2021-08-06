package no.nav.testnav.libs.slack.command;

import org.springframework.core.io.ByteArrayResource;

public class FileNameByteArrayResource extends ByteArrayResource {

    private String fileName;

    public FileNameByteArrayResource(String fileName, byte[] byteArray) {
        super(byteArray);
        this.fileName = fileName;
    }

    @Override
    public String getFilename() {
        return fileName;
    }
}