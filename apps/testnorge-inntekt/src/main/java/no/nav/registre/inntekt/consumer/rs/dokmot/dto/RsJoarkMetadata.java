package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Value;

@ApiModel
@Value
@Builder
public class RsJoarkMetadata {

    private String journalpostType;
    private String avsenderMottakerIdType;
    private String brukerIdType;
    private String tema;
    private String tittel;
    private String kanal;
    private String eksternReferanseId;
    private String filtypeOriginal;
    private String filtypeArkiv;
    private String variantformatOriginal;
    private String variantformatArkiv;
    private String brevkode;
    private String brevkategori;

}
