package no.nav.tpsidenter.vedlikehold.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {

    private String filename;
    private Byte[] filecontents;
}