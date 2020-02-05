package no.nav.dolly.domain.resultset.entity.testgruppe;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTestgruppe {
    private Long id;
    private String navn;
    private String hensikt;
    private String opprettetAvNavIdent;
    private String sistEndretAvNavIdent;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate datoEndret;

    private Integer antallIdenter;
    private Integer antallIBruk;

    private boolean erEierAvGruppe;
    private boolean favorittIGruppen;
}