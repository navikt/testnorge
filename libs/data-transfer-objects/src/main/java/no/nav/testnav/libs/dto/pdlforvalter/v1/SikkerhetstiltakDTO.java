package no.nav.testnav.libs.dto.pdlforvalter.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SikkerhetstiltakDTO extends DbVersjonDTO {

    @Schema
    private Tiltakstype tiltakstype;
    @Schema
    private String beskrivelse;
    private Kontaktperson kontaktperson;
    @Schema
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime gyldigFraOgMed;
    @Schema
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime gyldigTilOgMed;

    public enum Tiltakstype {FYUS, TFUS, FTUS, DIUS, TOAN}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kontaktperson implements Serializable {

        @Schema(description = "NAV ident, (bokstav + 6 sifre", requiredMode = Schema.RequiredMode.REQUIRED, example = "Z9999999")
        private String personident;

        @Schema(description = "NAV enhet (4 sifre)", requiredMode = Schema.RequiredMode.REQUIRED)
        private String enhet;
    }
}