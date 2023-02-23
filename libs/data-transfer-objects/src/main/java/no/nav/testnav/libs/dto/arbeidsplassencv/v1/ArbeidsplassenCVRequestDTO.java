package no.nav.testnav.libs.dto.arbeidsplassencv.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsplassenCVRequestDTO {

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

    @Data
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
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private Boolean ongoing;
        private String styrkkode;
        private Boolean ikkeAktueltForFremtiden;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Utdanning extends CVFelles {

        private String institution;
        private String field;
        private String nuskode;
        private Boolean hasAuthorization;
        private String vocationalCollege; //TBD "SVENNEBREV_FAGBREV"
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String description;
        private Boolean ongoing;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenGodkjenning extends CVFelles {

        private String certificateName;
        private String alternativeName;
        private String conceptId;
        private String issuer;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foererkort extends CVFelles {

        private String type;
        private LocalDateTime acquiredDate;
        private LocalDateTime expiryDate;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenErfaring extends CVFelles {

        private String description;
        private String role;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private Boolean ongoing;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kurs extends CVFelles {

        private String title;
        private String issuer;
        private Long duration;
        private String durationUnit; //"UKJENT",
        private LocalDateTime date;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Spraak extends CVFelles {

        private String language;
        private String oralProficiency; //TBD "IKKE_OPPGITT",
        private String writtenProficiency; //TBD "IKKE_OPPGITT",
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OffentligeGodkjenning extends CVFelles {

        private String title;
        private String conceptId;
        private String issuer;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fagbrev extends CVFelles {

        private String title;
        private String conceptId;
        private String type; //TBD  "SVENNEBREV_FAGBREV",
    }

    @Data
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
        private String startOption; //TBD "LEDIG_NAA"
        private List<Occupation> occupations;
        private List<OccupationDraft> occupationDrafts;
        private List<Location> locations;
        private List<String> occupationTypes; // TBD "FAST"
        private List<String> workLoadTypes;   // TBD  "HELTID"
        private List<String> workScheduleTypes; // TBD "DAGTID"

        private Boolean sistEndretAvNav;
        private LocalDateTime sistEndret;
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
        private LocalDateTime updatedAt;
    }
}
