package no.nav.dolly.domain.resultset.entity.testgruppe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
    private Integer antallIBruk;

    private boolean erEierAvGruppe;
    private boolean favorittIGruppen;
    private Boolean erLaast;
    private String laastBeskrivelse;

    public List<Tags> getTags() {
        if (isNull(tags)) {
            tags = new ArrayList<>();
        }
        return tags;
    }
}