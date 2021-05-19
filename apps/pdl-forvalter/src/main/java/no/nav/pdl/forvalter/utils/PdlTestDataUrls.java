package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import java.util.EnumMap;
import java.util.Map;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_ADRESSEBESKYTTELSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_BOSTEDADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_DELTBOSTED;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_DOEDSFALL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FALSK_IDENTITET;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FAMILIERELASJON;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FOEDSEL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FOLKEREGISTER_PERSONSTATUS;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FORELDREANSVAR;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FULLMAKT;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_INNFLYTTING;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KJOENN;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KONTAKTADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KONTAKTINFORMASJON_FOR_DODESDBO;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_NAVN;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_OPPHOLD;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_OPPHOLDSADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_STATSBORGERSKAP;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_TELEFONUMMER;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_TILRETTELAGT_KOMMUNIKASJON;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_UTFLYTTING;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_VERGEMAAL;

@UtilityClass
public class PdlTestDataUrls {

    public static final String PDL_BESTILLING_SLETTING_URL = "/pdl-testdata/api/v1/personident";
    private static final String PDL_BESTILLING_URL = "/pdl-testdata/api/v1/bestilling";
    public static final String PDL_BESTILLING_OPPRETT_PERSON_URL = PDL_BESTILLING_URL + "/opprettperson";

    private static final String PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL = PDL_BESTILLING_URL + "/kontaktinformasjonfordoedsbo";
    private static final String PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL = PDL_BESTILLING_URL + "/utenlandsidentifikasjonsnummer";
    private static final String PDL_BESTILLING_FALSK_IDENTITET_URL = PDL_BESTILLING_URL + "/falskidentitet";
    private static final String PDL_BESTILLING_FOEDSEL_URL = PDL_BESTILLING_URL + "/foedsel";
    private static final String PDL_BESTILLING_FAMILIERELASJON_URL = PDL_BESTILLING_URL + "/familierelasjon";
    private static final String PDL_BESTILLING_DOEDSFALL_URL = PDL_BESTILLING_URL + "/doedsfall";
    private static final String PDL_BESTILLING_ADRESSEBESKYTTELSE_URL = PDL_BESTILLING_URL + "/adressebeskyttelse";
    private static final String PDL_BESTILLING_NAVN_URL = PDL_BESTILLING_URL + "/navn";
    private static final String PDL_BESTILLING_KJOENN_URL = PDL_BESTILLING_URL + "/kjoenn";
    private static final String PDL_BESTILLING_STATSBORGERSKAP_URL = PDL_BESTILLING_URL + "/statsborgerskap";
    private static final String PDL_BESTILLING_TELEFONUMMER_URL = PDL_BESTILLING_URL + "/telefonnummer";
    private static final String PDL_BESTILLING_SIVILSTAND_URL = PDL_BESTILLING_URL + "/sivilstand";
    private static final String PDL_BESTILLING_OPPHOLDSADRESSE_URL = PDL_BESTILLING_URL + "/oppholdsadresse";
    private static final String PDL_BESTILLING_KONTAKTADRESSE_URL = PDL_BESTILLING_URL + "/kontaktadresse";
    private static final String PDL_BESTILLING_BOSTEDADRESSE_URL = PDL_BESTILLING_URL + "/bostedsadresse";
    private static final String PDL_BESTILLING_DELTBOSTED_URL = PDL_BESTILLING_URL + "/deltbosted";
    private static final String PDL_BESTILLING_INNFLYTTING_URL = PDL_BESTILLING_URL + "/innflytting";
    private static final String PDL_BESTILLING_UTFLYTTING_URL = PDL_BESTILLING_URL + "/utflytting";
    private static final String PDL_BESTILLING_FORELDREANSVAR_URL = PDL_BESTILLING_URL + "/foreldreansvar";
    private static final String PDL_BESTILLING_OPPHOLD_URL = PDL_BESTILLING_URL + "/opphold";
    private static final String PDL_BESTILLING_VERGEMAAL_URL = PDL_BESTILLING_URL + "/vergemaal";
    private static final String PDL_BESTILLING_FULLMAKT_URL = PDL_BESTILLING_URL + "/fullmakt";
    private static final String PDL_BESTILLING_TILRETTELAGT_KOMMUNIKASJON_URL = PDL_BESTILLING_URL + "/tilrettelagtkommunikasjon";
    private static final String PDL_IDENTHISTORIKK_PARAMS = "?historiskePersonidenter=";
    private static final String PDL_IDENTHISTORIKK_PARAMS_2 = "&historiskePersonidenter=";
    private static final String PDL_BESTILLING_FOLKEREGISTER_PERSONSTATUS_URL = PDL_BESTILLING_URL + "/folkeregisterpersonstatus";

    private static final Map<PdlArtifact, String> bestillingUrl = new EnumMap<>(PdlArtifact.class);

    static {
        bestillingUrl.put(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL);
        bestillingUrl.put(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL);
        bestillingUrl.put(PDL_FALSK_IDENTITET, PDL_BESTILLING_FALSK_IDENTITET_URL);
        bestillingUrl.put(PDL_FOEDSEL, PDL_BESTILLING_FOEDSEL_URL);
        bestillingUrl.put(PDL_FAMILIERELASJON, PDL_BESTILLING_FAMILIERELASJON_URL);
        bestillingUrl.put(PDL_DOEDSFALL, PDL_BESTILLING_DOEDSFALL_URL);
        bestillingUrl.put(PDL_ADRESSEBESKYTTELSE, PDL_BESTILLING_ADRESSEBESKYTTELSE_URL);
        bestillingUrl.put(PDL_NAVN, PDL_BESTILLING_NAVN_URL);
        bestillingUrl.put(PDL_KJOENN, PDL_BESTILLING_KJOENN_URL);
        bestillingUrl.put(PDL_KONTAKTADRESSE, PDL_BESTILLING_KONTAKTADRESSE_URL);
        bestillingUrl.put(PDL_STATSBORGERSKAP, PDL_BESTILLING_STATSBORGERSKAP_URL);
        bestillingUrl.put(PDL_TELEFONUMMER, PDL_BESTILLING_TELEFONUMMER_URL);
        bestillingUrl.put(PDL_SIVILSTAND, PDL_BESTILLING_SIVILSTAND_URL);
        bestillingUrl.put(PDL_OPPHOLDSADRESSE, PDL_BESTILLING_OPPHOLDSADRESSE_URL);
        bestillingUrl.put(PDL_BOSTEDADRESSE, PDL_BESTILLING_BOSTEDADRESSE_URL);
        bestillingUrl.put(PDL_DELTBOSTED, PDL_BESTILLING_DELTBOSTED_URL);
        bestillingUrl.put(PDL_INNFLYTTING, PDL_BESTILLING_INNFLYTTING_URL);
        bestillingUrl.put(PDL_UTFLYTTING, PDL_BESTILLING_UTFLYTTING_URL);
        bestillingUrl.put(PDL_FORELDREANSVAR, PDL_BESTILLING_FORELDREANSVAR_URL);
        bestillingUrl.put(PDL_OPPHOLD, PDL_BESTILLING_OPPHOLD_URL);
        bestillingUrl.put(PDL_VERGEMAAL, PDL_BESTILLING_VERGEMAAL_URL);
        bestillingUrl.put(PDL_FULLMAKT, PDL_BESTILLING_FULLMAKT_URL);
        bestillingUrl.put(PDL_FOLKEREGISTER_PERSONSTATUS, PDL_BESTILLING_FOLKEREGISTER_PERSONSTATUS_URL);
        bestillingUrl.put(PDL_TILRETTELAGT_KOMMUNIKASJON, PDL_BESTILLING_TILRETTELAGT_KOMMUNIKASJON_URL);
    }

    public enum PdlArtifact {
        PDL_KONTAKTINFORMASJON_FOR_DODESDBO,
        PDL_UTENLANDS_IDENTIFIKASJON_NUMMER,
        PDL_FOLKEREGISTER_PERSONSTATUS,
        PDL_FALSK_IDENTITET,
        PDL_FOEDSEL,
        PDL_FAMILIERELASJON,
        PDL_DOEDSFALL,
        PDL_ADRESSEBESKYTTELSE,
        PDL_NAVN,
        PDL_KJOENN,
        PDL_STATSBORGERSKAP,
        PDL_TELEFONUMMER,
        PDL_SIVILSTAND,
        PDL_OPPHOLDSADRESSE,
        PDL_KONTAKTADRESSE,
        PDL_BOSTEDADRESSE,
        PDL_DELTBOSTED,
        PDL_INNFLYTTING,
        PDL_UTFLYTTING,
        PDL_FORELDREANSVAR,
        PDL_OPPHOLD,
        PDL_VERGEMAAL,
        PDL_FULLMAKT,
        PDL_TILRETTELAGT_KOMMUNIKASJON
    }

    public enum TemaGrunnlag {GEN, PEN}

    public enum PdlStatus {OK, FEIL}

    public static Map<PdlArtifact, String> getBestillingUrl() {
        return bestillingUrl;
    }
}
