package no.nav.dolly.bestilling.arbeidsplassencv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PAMCVDTO {

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
    private ZonedDateTime sistEndret;

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
        private ZonedDateTime fromDate;
        private ZonedDateTime toDate;
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
        private ArbeidsplassenCVDTO.Yrkesskole vocationalCollege;
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private String description;
        private Boolean ongoing;
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
        private ZonedDateTime fromDate;
        private ZonedDateTime toDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foererkort extends CVFelles {

        private String type;
        private ZonedDateTime acquiredDate;
        private ZonedDateTime expiryDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenErfaring extends CVFelles {

        private String description;
        private String role;
        private ZonedDateTime fromDate;
        private ZonedDateTime toDate;
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
        private ArbeidsplassenCVDTO.Tidsenhet durationUnit;
        private ZonedDateTime date;
    }
    
    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Spraak extends CVFelles {

        private String language;
        private ArbeidsplassenCVDTO.Sprakferdighetsniva oralProficiency;
        private ArbeidsplassenCVDTO.Sprakferdighetsniva writtenProficiency;
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
        private ZonedDateTime fromDate;
        private ZonedDateTime toDate;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fagbrev extends CVFelles {

        private String title;
        private String conceptId;
        private ArbeidsplassenCVDTO.VocationalCertification type;
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
        private ArbeidsplassenCVDTO.StartOption startOption;
        private List<Occupation> occupations;
        private List<OccupationDraft> occupationDrafts;
        private List<Location> locations;
        private List<ArbeidsplassenCVDTO.OccupationType> occupationTypes;
        private List<ArbeidsplassenCVDTO.Omfang> workLoadTypes;
        private List<ArbeidsplassenCVDTO.Arbeidstid> workScheduleTypes;

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
        private ZonedDateTime updatedAt;
    }
}
