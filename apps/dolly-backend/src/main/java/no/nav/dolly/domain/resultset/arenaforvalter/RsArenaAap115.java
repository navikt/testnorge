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
@Schema(description = "AAP-§115-rettigheter for brukeren")
public class RsArenaAap115 {

    public enum VedtaksType {O, S}

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime fraDato;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime tilDato;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime datoMottatt;

    @Schema(description = "Gyldige verdier: O (ny), S (stans)")
    private VedtaksType vedtaktype;
    private List<MedisinskOpplysning> medisinskOpplysning;
    private List<Vilkaar> vilkaar;
    private String utfall;
    private String begrunnelse;

    public List<MedisinskOpplysning> getMedisinskOpplysning() {
        if (isNull(medisinskOpplysning)) {
            medisinskOpplysning = new ArrayList<>();
        }
        return medisinskOpplysning;
    }

    public List<Vilkaar> getVilkaar() {
        if (isNull(vilkaar)) {
            vilkaar = new ArrayList<>();
        }
        return vilkaar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedisinskOpplysning {

        private String type;
        private String klassifisering;
        private String diagnose;
        private String kilde;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime kildeDato;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vilkaar {

        private String kode;
        private String status;
    }
}