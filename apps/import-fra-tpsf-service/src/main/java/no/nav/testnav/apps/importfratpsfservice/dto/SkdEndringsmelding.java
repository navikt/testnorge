package no.nav.testnav.apps.importfratpsfservice.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "meldingstype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SkdEndringsmeldingTrans1.class, name = "t1"),
        @JsonSubTypes.Type(value = SkdEndringsmeldingTrans2.class, name = "t2")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class SkdEndringsmelding {
    
    private Long id;

    private String beskrivelse;
    @Size(max = 1)
    private String transtype;
    @Size(max = 8)
    private String maskindato;
    @Size(max = 6)
    private String maskintid;
    @Size(max = 2)
    private String aarsakskode;
    @Size(max = 6)
    private String sekvensnr;
}