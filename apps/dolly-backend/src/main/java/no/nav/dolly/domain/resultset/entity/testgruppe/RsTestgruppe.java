package no.nav.dolly.domain.resultset.entity.testgruppe;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTestgruppe {
    private Long id;
    private String navn;
    private String hensikt;
    private RsBrukerUtenFavoritter opprettetAv;
    private RsBrukerUtenFavoritter sistEndretAv;
    private List<Tags> tags;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate datoEndret;

    private Integer antallIdenter;
    private Integer antallBestillinger;
    private Integer antallIBruk;

    private boolean erEierAvGruppe;
    private Boolean erLaast;
    private String laastBeskrivelse;
}
