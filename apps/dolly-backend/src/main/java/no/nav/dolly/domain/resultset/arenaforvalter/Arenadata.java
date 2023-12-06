package no.nav.dolly.domain.resultset.arenaforvalter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arenadata {

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime aktiveringDato;

    @Schema
    private ArenaBrukertype arenaBrukertype;

    @Schema
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;

    @Schema(description = "Automatisk innsending av meldekort")
    private Boolean automatiskInnsendingAvMeldekort;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime inaktiveringDato;

    @Schema(description = "Om rettigheten aap115 skal aktiveres på personen")
    private List<RsArenaAap115> aap115;

    @Schema(description = "Om rettigheten aap skal aktiveres på personen")
    private List<RsArenaAap> aap;

    @Schema(description = "Om rettigheten aap skal aktiveres på personen")
    private List<RsArenaDagpenger> dagpenger;

    public List<RsArenaAap115> getAap115() {
        if (isNull(aap115)) {
            aap115 = new ArrayList<>();
        }
        return aap115;
    }

    public List<RsArenaAap> getAap() {
        if (isNull(aap)) {
            aap = new ArrayList<>();
        }
        return aap;
    }

    public List<RsArenaDagpenger> getDagpenger() {
        if (isNull(dagpenger)) {
            dagpenger = new ArrayList<>();
        }
        return dagpenger;
    }
}