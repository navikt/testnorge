package no.nav.testnav.libs.data.dollysearchservice.v1;

import lombok.Getter;

@Getter
public enum ElasticTyper {

    ARBEIDSFORHOLD("Arbeidsforhold", true),
    ARBEIDSFORHOLD_FORENKLET("Arbeidsforhold - Forenklet", true),
    ARBEIDSFORHOLD_FRILANS("Arbeidsforhold - Frilans", true),
    ARBEIDSFORHOLD_MARITIMT("Arbeidsforhold - Maritimt", true),
    ARBEIDSFORHOLD_ORDINAERT("Arbeidsforhold - Ordinært", true),
    ARBEIDSPLASSENCV("Arbeidsplassen CV", false),
    ARBEIDSSOEKERREGISTERET("Arbeidssøkerregisteret",false),
    ARENA_AAP("Arena AAP ytelse", true),
    ARENA_AAP115("Arena AAP115 rettighet", true),
    ARENA_DAGP("Arena dagpenger", true),
    BANKKONTO("Bankkontoregister", false),
    BANKKONTO_NORGE("Bankkonto i Norge", false),
    BANKKONTO_UTLAND("Bankkonto i utlandet", false),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)", false),
    DOKARKIV("Dokumentarkiv (JOARK)", true),
    ETTERLATTE("Etterlatte ytelse", false),
    FULLMAKT("Fullmakt (Representasjon)", false),
    HISTARK("Historisk arkiv (HISTARK)", false),
    INNTK("Inntektskomponenten/stub (INNTK)", false),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)", true),
    INST("Institusjonsopphold (INST2)", true),
    KRRSTUB("Kontakt- og reservasjonsregister-stub", false),
    MEDL("Medlemskap (MEDL)", false),
    NOM("NAV-ansatt (NOM)", false),
    PEN_AFP_OFFENTLIG("Pensjon - AFP offentlig", true),
    PEN_AP("Pensjon - Alderspensjon (AP)", true),
    PEN_INNTEKT("Pensjon - Pensjonsinntekt/opptjening", true),
    PEN_PENSJONSAVTALE("Pensjon - Pensjonsavtaler", true),
    PEN_TP("Pensjon - Tjenestepensjon (TP)", true),
    PEN_UT("Pensjon - Uføretrygd (UT)", true),
    SIGRUN_PENSJONSGIVENDE("Sigrunstub - Pensjonsgivende inntekt", false),
    SIGRUN_SUMMERT("Sigrunstub - Summert skattegrunnlag", false),
    SKATTEKORT("Skattekort (SOKOS)", false),
    SKJERMING("Skjermingsregisteret", false),
    SYKEMELDING("Sykemelding", true),
    UDISTUB("Udistub - Utlendingsdirektoratet", false),
    YRKESSKADE("Yrkesskade", false);

    private final String beskrivelse;
    private final boolean miljoAvhengig;

    ElasticTyper(String beskrivelse, boolean miljoAvhengig) {
        this.beskrivelse = beskrivelse;
        this.miljoAvhengig = miljoAvhengig;
    }
}
