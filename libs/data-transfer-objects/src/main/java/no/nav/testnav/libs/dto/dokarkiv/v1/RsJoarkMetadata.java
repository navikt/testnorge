package no.nav.testnav.libs.dto.dokarkiv.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
