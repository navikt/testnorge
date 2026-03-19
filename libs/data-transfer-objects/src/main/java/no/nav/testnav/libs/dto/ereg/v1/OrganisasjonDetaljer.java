package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om organisasjonsdetaljer")
@JsonPropertyOrder({
        "registreringsdato",
        "stiftelsesdato",
        "opphoersdato",
        "enhetstyper",
        "navn",
        "naeringer",
        "statuser",
        "forretningsadresser",
        "postadresser",
        "epostadresser",
        "internettadresser",
        "telefonnummer",
        "mobiltelefonnummer",
        "telefaksnummer",
        "formaal",
        "registrertMVA",
        "underlagtHjemlandLovgivningForetaksform",
        "hjemlandregistre",
        "ansatte",
        "navSpesifikkInformasjon",
        "maalform",
        "dublettAv",
        "dubletter",
        "sistEndret"
})
@SuppressWarnings({"pmd:TooManyFields", "fb-contrib:CC_CYCLOMATIC_COMPLEXITY"})
public class OrganisasjonDetaljer {

    private LocalDateTime registreringsdato;

    private LocalDate sistEndret;

    @Schema(description = "M&aring;lform (kodeverk: M_c3_a5lformer)", example = "NB")
    private String maalform;

    private LocalDate opphoersdato;

    @Schema(description = "Dublettorganisajon")
    private Organisasjon dublettAv;

    @Schema(description = "Liste av organisasjonsdubletter")
    private List<Organisasjon> dubletter;

    @Schema(description = "Liste av merverdiavgiftinformasjon")
    private List<MVA> registrertMVA;

    @Schema(description = "Liste av telefaksnummer")
    private List<Telefonnummer> telefaksnummer;

    @Schema(description = "Liste av telefonnummer")
    private List<Telefonnummer> telefonnummer;

    @Schema(description = "Liste av organisasjonsstatuser")
    private List<Status> statuser;

    @Schema(description = "Liste av forretningsadresser")
    private List<Forretningsadresse> forretningsadresser;

    @Schema(description = "Liste av postadresser")
    private List<Postadresse> postadresser;

    @Schema(description = "Informasjon som er spesifikk for NAV")
    private NAVSpesifikkInformasjon navSpesifikkInformasjon;

    @Schema(description = "Liste av internettadresser")
    private List<Internettadresse> internettadresser;

    @Schema(description = "Liste av epostadresser")
    private List<Epostadresse> epostadresser;

    @Schema(description = "Liste av n&aelig;ringsinformasjon")
    private List<Naering> naeringer;

    @Schema(description = "Liste av informasjon om foretak i utland")
    private List<UnderlagtHjemlandLovgivningForetaksform> underlagtHjemlandLovgivningForetaksform;

    @Schema(description = "Liste av organisasjonsnavn")
    private List<Navn> navn = new ArrayList<>();

    @Schema(description = "Liste av form&aring;l")
    private List<Formaal> formaal;

    @Schema(description = "Liste av mobiltelefonnummer")
    private List<Telefonnummer> mobiltelefonnummer;

    private LocalDate stiftelsesdato;

    @Schema(description = "Liste av hjemlandregister")
    private List<Hjemlandregister> hjemlandregistre;

    @Schema(description = "Liste av organisasjonsenhetstyper")
    private List<Enhetstype> enhetstyper;

    @Schema(description = "Liste av ansattinformasjon")
    private List<Ansatte> ansatte;

    @JsonIgnore
    public LocalDateTime getRegistreringsdato() {
        return registreringsdato;
    }

    @JsonIgnore
    public LocalDate getSistEndret() {
        return sistEndret;
    }

    @JsonProperty("registreringsdato")
    @Schema(description = "Dato for registrering, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2014-07-15T12:10:58.059")
    public String getRegistreringsdatoAsString() {
        return JavaTimeUtil.toString(registreringsdato);
    }

    @JsonProperty("sistEndret")
    @Schema(description = "Dato sist endret, format (ISO-8601): yyyy-MM-dd", example = "2015-01-13")
    public String getSistEndretDatoAsString() {
        return JavaTimeUtil.toString(sistEndret);
    }

    @JsonProperty("opphoersdato")
    @Schema(description = "Dato for opph&oslash;r, format (ISO-8601): yyyy-MM-dd", example = "2016-12-31")
    public String getOpphoersdatoAsString() {
        return JavaTimeUtil.toString(opphoersdato);
    }

    @JsonProperty("stiftelsesdato")
    @Schema(description = "Dato for stiftelse, format (ISO-8601): yyyy-MM-dd", example = "2014-07-15")
    public String getStiftelsesdatoAsString() {
        return JavaTimeUtil.toString(stiftelsesdato);
    }

    public List<Organisasjon> getDubletter() {

        if (isNull(dubletter)) {
            dubletter = new ArrayList<>();
        }
        return dubletter;
    }

    public List<MVA> getRegistrertMVA() {

        if (isNull(registrertMVA)) {
            registrertMVA = new ArrayList<>();
        }
        return registrertMVA;
    }

    public List<Telefonnummer> getTelefaksnummer() {
        if (isNull(telefaksnummer)) {
            telefaksnummer = new ArrayList<>();
        }
        return telefaksnummer;
    }

    public List<Telefonnummer> getTelefonnummer() {

        if (isNull(telefonnummer)) {
            telefonnummer = new ArrayList<>();
        }
        return telefonnummer;
    }

    public List<Status> getStatuser() {

        if (isNull(statuser)) {
            statuser = new ArrayList<>();
        }
        return statuser;
    }

    public List<Forretningsadresse> getForretningsadresser() {

        if (isNull(forretningsadresser)) {
            forretningsadresser = new ArrayList<>();
        }
        return forretningsadresser;
    }

    public List<Postadresse> getPostadresser() {
        if (isNull(postadresser)) {
            postadresser = new ArrayList<>();
        }
        return postadresser;
    }

    public List<Internettadresse> getInternettadresser() {
        if (isNull(internettadresser)) {
            internettadresser = new ArrayList<>();
        }
        return internettadresser;
    }

    public List<Epostadresse> getEpostadresser() {
        if (isNull(epostadresser)) {
            epostadresser = new ArrayList<>();
        }
        return epostadresser;
    }

    public List<Naering> getNaeringer() {
        if (isNull(naeringer)) {
            naeringer = new ArrayList<>();
        }
        return naeringer;
    }

    public List<Navn> getNavn() {
        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }

    public List<Formaal> getFormaal() {
        if (isNull(formaal)) {
            formaal = new ArrayList<>();
        }
        return formaal;
    }

    public List<Telefonnummer> getMobiltelefonnummer() {
        if (isNull(mobiltelefonnummer)) {
            mobiltelefonnummer = new ArrayList<>();
        }
        return mobiltelefonnummer;
    }

    public List<Hjemlandregister> getHjemlandregistre() {
        if (isNull(hjemlandregistre)) {
            hjemlandregistre = new ArrayList<>();
        }
        return hjemlandregistre;
    }

    public List<Enhetstype> getEnhetstyper() {
        if (isNull(enhetstyper)) {
            enhetstyper = new ArrayList<>();
        }
        return enhetstyper;
    }

    public List<Ansatte> getAnsatte() {
        if (isNull(ansatte)) {
            ansatte = new ArrayList<>();
        }
        return ansatte;
    }
}
