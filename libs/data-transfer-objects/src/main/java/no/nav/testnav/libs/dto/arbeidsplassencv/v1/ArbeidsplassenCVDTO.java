package no.nav.testnav.libs.dto.arbeidsplassencv.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsplassenCVDTO {

    @Schema(description = "harHjemmel er ikke en del av CV, men er lagt her for bakover-kompabilitet. " +
            "Den styrer lesetilgang ved \"rekrutteringsbistand\" i target system.")
    private Boolean harHjemmel;
    private Boolean harBil;
    private List<Arbeidserfaring> arbeidserfaring;
    private List<Utdanning> utdanning;
    private List<AnnenGodkjenning> andreGodkjenninger;
    private List<Foererkort> foererkort;
    private List<AnnenErfaring> annenErfaring;
    private List<Kurs> kurs;
    private List<Spraak> spraak;
    private List<OffentligeGodkjenning> offentligeGodkjenninger;
    private List<Fagbrev> fagbrev;
    private List<Kompetanse> kompetanser;
    private String sammendrag;
    private Jobboensker jobboensker;
    private Boolean sistEndretAvNav;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime sistEndret;

    public List<Arbeidserfaring> getArbeidserfaring() {
        if (isNull(arbeidserfaring)) {
            arbeidserfaring = new ArrayList<>();
        }
        return arbeidserfaring;
    }

    public List<Utdanning> getUtdanning() {
        if (isNull(utdanning)) {
            utdanning = new ArrayList<>();
        }
        return utdanning;
    }

    public List<AnnenGodkjenning> getAndreGodkjenninger() {
        if (isNull(andreGodkjenninger)) {
            andreGodkjenninger = new ArrayList<>();
        }
        return andreGodkjenninger;
    }

    public List<Foererkort> getFoererkort() {
        if (isNull(foererkort)) {
            foererkort = new ArrayList<>();
        }
        return foererkort;
    }

    public List<AnnenErfaring> getAnnenErfaring() {
        if (isNull(annenErfaring)) {
            annenErfaring = new ArrayList<>();
        }
        return annenErfaring;
    }

    public List<Kurs> getKurs() {
        if (isNull(kurs)) {
            kurs = new ArrayList<>();
        }
        return kurs;
    }

    public List<Spraak> getSpraak() {
        if (isNull(spraak)) {
            spraak = new ArrayList<>();
        }
        return spraak;
    }

    public List<OffentligeGodkjenning> getOffentligeGodkjenninger() {
        if (isNull(offentligeGodkjenninger)) {
            offentligeGodkjenninger = new ArrayList<>();
        }
        return offentligeGodkjenninger;
    }

    public List<Fagbrev> getFagbrev() {
        if (isNull(fagbrev)) {
            fagbrev = new ArrayList<>();
        }
        return fagbrev;
    }

    public List<Kompetanse> getKompetanser() {
        if (isNull(kompetanser)) {
            kompetanser = new ArrayList<>();
        }
        return kompetanser;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidserfaring extends CVFelles {

        private String employer;
        private String jobTitle;
        private String alternativeJobTitle;
        private String conceptId;
        private String location;
        private String description;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime fromDate;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime toDate;
        private Boolean ongoing;
        private String styrkkode;
        private Boolean ikkeAktueltForFremtiden;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Utdanning extends CVFelles {

        private String institution;
        private String field;
        private String nuskode;
        private Boolean hasAuthorization;
        private Yrkesskole vocationalCollege;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime startDate;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime endDate;
        private String description;
        private Boolean ongoing;
    }

    public enum VocationalCertification {
        SVENNEBREV_FAGBREV, MESTERBREV, AUTORISASJON
    }

    public enum Yrkesskole {
        SVENNEBREV_FAGBREV, MESTERBREV, INGEN
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenGodkjenning extends CVFelles {

        private String certificateName;
        private String alternativeName;
        private String conceptId;
        private String issuer;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime fromDate;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime toDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foererkort extends CVFelles {

        private String type;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime acquiredDate;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime expiryDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenErfaring extends CVFelles {

        private String description;
        private String role;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime fromDate;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime toDate;
        private Boolean ongoing;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kurs extends CVFelles {

        private String title;
        private String issuer;
        private Long duration;
        private Tidsenhet durationUnit;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime date;
    }

    public enum Tidsenhet {
        UKJENT,
        TIME,
        DAG,
        UKE,
        MND
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Spraak extends CVFelles {

        private String language;
        private Sprakferdighetsniva oralProficiency;
        private Sprakferdighetsniva writtenProficiency;
    }

    public enum Sprakferdighetsniva {
        IKKE_OPPGITT,
        NYBEGYNNER,
        GODT,
        VELDIG_GODT,
        FOERSTESPRAAK
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OffentligeGodkjenning extends CVFelles {

        private String title;
        private String conceptId;
        private String issuer;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime fromDate;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime toDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fagbrev extends CVFelles {

        private String title;
        private String conceptId;
        private VocationalCertification type;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kompetanse extends CVFelles {

        private String title;
        private Long conceptId;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Jobboensker {
        private Boolean active;
        private StartOption startOption;
        private List<Occupation> occupations;
        private List<OccupationDraft> occupationDrafts;
        private List<Location> locations;
        private List<OccupationType> occupationTypes;
        private List<Omfang> workLoadTypes;
        private List<Arbeidstid> workScheduleTypes;

        public List<Occupation> getOccupations() {
            if (isNull(occupations)) {
                occupations = new ArrayList<>();
            }
            return occupations;
        }

        public List<OccupationDraft> getOccupationDrafts() {
            if (isNull(occupationDrafts)) {
                occupationDrafts = new ArrayList<>();
            }
            return occupationDrafts;
        }

        public List<Location> getLocations() {
            if (isNull(locations)) {
                locations = new ArrayList<>();
            }
            return locations;
        }
    }

    public enum StartOption {
        LEDIG_NAA,
        ETTER_TRE_MND,
        ETTER_AVTALE
    }

    public enum OccupationType {
        FAST,
        VIKARIAT,
        ENGASJEMENT,
        PROSJEKT,
        SESONG,
        TRAINEE,
        LAERLING,
        SELVSTENDIG_NAERINGSDRIVENDE,
        FERIEJOBB,
        ANNET
    }

    public enum Omfang {
        HELTID, DELTID;
    }

    public enum Arbeidstid {
        DAGTID,
        KVELD,
        NATT,
        UKEDAGER,
        LOERDAG,
        SOENDAG,
        SKIFT,
        VAKT,
        TURNUS;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Occupation {
        private String id;
        private String title;
        private Long conceptId;
        private String styrk08;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OccupationDraft {

        private String id;
        private String title;
        private Long conceptId;
        private String styrk08;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @SuppressWarnings("java:S1700")
    public static class Location {
        private String id;
        private String location;
        private String code;
        private Long conceptId;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    public abstract static class CVFelles {
        private String uuid;
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedAt;
    }
}
