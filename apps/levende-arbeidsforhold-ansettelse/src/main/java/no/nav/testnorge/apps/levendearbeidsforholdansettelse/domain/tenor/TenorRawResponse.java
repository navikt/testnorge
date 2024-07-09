package no.nav.testnorge.apps.levendearbeidsforholdansettelse.domain.tenor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class TenorRawResponse {

    private Integer treff;
    private Integer rader;
    private Integer offset;
    private Integer nesteSide;
    private Integer seed;
    private List<Dokument> dokumentListe;

    public List<Dokument> getDokumentListe() {

        if (isNull(dokumentListe)) {
            dokumentListe = new ArrayList<>();
        }
        return dokumentListe;
    }

    @Data
    @NoArgsConstructor
    public static class Dokument {
        private String bostedsadresse;
        private String id;
        private String arbeidsforhold;
    }

    @Data
    @NoArgsConstructor
    public static class TenorRelasjoner {

        private List<Arbeidsforhold> arbeidsforhold;
        private List<BeregnetSkatt> beregnetSkatt;
        private List<BrregErFr> brregErFr;
        private List<Freg> freg;
        private List<Inntekt> inntekt;
        private List<SamletReskontroinnsyn> samletReskontroinnsyn;
        private List<Skattemelding> skattemelding;
        private List<Skatteplikt> skatteplikt;
        private List<SpesifisertSummertSkattegrunnlag> spesifisertSummertSkattegrunnlag;
        private List<SummertSkattegrunnlag> summertSkattegrunnlag;
        private List<TestinnsendingSkattPerson> testinnsendingSkattPerson;
        private List<Tilleggsskatt> tilleggsskatt;
        private List<Tjenestepensjonsavtale> tjenestepensjonsavtale;
    }

    @Data
    @NoArgsConstructor
    public static class BrregErFr {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class Skattemelding {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class Skatteplikt {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class Inntekt {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class BeregnetSkatt {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class SummertSkattegrunnlag {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class Freg {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class SpesifisertSummertSkattegrunnlag {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class Tjenestepensjonsavtale {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class Tilleggsskatt {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class Arbeidsforhold {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class TestinnsendingSkattPerson {

        private String tenorRelasjonsnavn;
    }

    @Data
    @NoArgsConstructor
    public static class SamletReskontroinnsyn {

        private String tenorRelasjonsnavn;
    }
}