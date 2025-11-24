package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@SuppressWarnings("java:S115")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Lookups {

    AOrdningBeskrivelse(TenorRequest.AOrdningBeskrivelse.class),
    AdresseGradering(TenorRequest.AdresseGradering.class),
    Adressebeskyttelse(TenorRequest.Adressebeskyttelse.class),
    Arbeidsforholdstype(TenorRequest.Arbeidsforholdstype.class),
    BostedsadresseType(TenorRequest.BostedsadresseType.class),
    CoAdressenavnType(TenorRequest.CoAdressenavnType.class),
    Forskuddstrekk(TenorRequest.Forskuddstrekk.class),
    Hendelse(no.nav.testnav.apps.tenorsearchservice.domain.Hendelse.class),
    IdentifikatorType(TenorRequest.IdentifikatorType.class),
    IdentitetsgrunnlagStatus(TenorRequest.IdentitetsgrunnlagStatus.class),
    Inntektstype(TenorRequest.Inntektstype.class),
    Kjoenn(TenorRequest.Kjoenn.class),
    Oppgjoerstype(TenorRequest.Oppgjoerstype.class),
    Personstatus(TenorRequest.Personstatus.class),
    Relasjon(TenorRequest.Relasjon.class),
    Roller(TenorRequest.Rolle.class),
    SaerskiltSkatteplikt(TenorRequest.SaerskiltSkatteplikt.class),
    Sivilstatus(TenorRequest.Sivilstand.class),
    Skattemeldingstype(TenorRequest.Skattemeldingstype.class),
    Skattepliktstype(TenorRequest.Skattepliktstype.class),
    Spesifiseringstype(no.nav.testnav.apps.tenorsearchservice.domain.Spesifiseringstype.class),
    Stadietype(TenorRequest.Stadietype.class),
    TekniskNavn(no.nav.testnav.apps.tenorsearchservice.domain.TekniskNavn.class),
    TilleggsskattType(TenorRequest.TilleggsskattType.class),
    UtenlandskPersonIdentifikasjon(TenorRequest.UtenlandskPersonIdentifikasjon.class),
    VergeTjenestevirksomhet(TenorRequest.VergeTjenestevirksomhet.class),
    VergemaalType(TenorRequest.VergemaalType.class);

    private final Class<?> value;
}