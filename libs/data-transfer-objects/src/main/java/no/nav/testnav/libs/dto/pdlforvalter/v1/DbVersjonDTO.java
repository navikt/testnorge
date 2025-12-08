package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

import static java.util.Objects.nonNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class DbVersjonDTO implements Serializable {

    @Schema(description = "Versjon av informasjonselement. Fravær av denne eller 0 betyr nytt element")
    private Integer id;

    @Schema(defaultValue = "Dolly",
            description = "Dataens opprinnelse")
    private String kilde;

    @Schema(defaultValue = "FREG",
            description = "Hvem er master, FREG eller PDL?")
    private Master master;

    @JsonIgnore
    private Boolean isNew;

    @Schema(description = "Angir timestamp for metadata")
    private Instant opprettet;

    @Schema(description = "Denne kan også benyttes ved behov")
    private FolkeregistermetadataDTO folkeregistermetadata;

    @Schema(description = "hendelseId formidler forrige innsendingshendelse (kvittering) fra PDL")
    private String hendelseId;

    @JsonIgnore
    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }

    public enum Master {FREG, PDL}

    @JsonIgnore
    public String getIdentForRelasjon() {
        return null;
    }

    @JsonIgnore
    public boolean isPdlMaster() {
        return master == Master.PDL;
    }
}
