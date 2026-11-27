package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@SuppressWarnings("java:S115")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Lookups {

    AOrdningBeskrivelse(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.AOrdningBeskrivelse.class),
    AdresseGradering(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.AdresseGradering.class),
    Adressebeskyttelse(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Adressebeskyttelse.class),
    Arbeidsforholdstype(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Arbeidsforholdstype.class),
    BostedsadresseType(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.BostedsadresseType.class),
    CoAdressenavnType(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.CoAdressenavnType.class),
    Forskuddstrekk(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Forskuddstrekk.class),
    Hendelse(no.nav.testnav.libs.dto.tenorsearchservice.v1.Hendelse.class),
    IdentifikatorType(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.IdentifikatorType.class),
    IdentitetsgrunnlagStatus(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.IdentitetsgrunnlagStatus.class),
    Inntektstype(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Inntektstype.class),
    Kjoenn(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Kjoenn.class),
    Oppgjoerstype(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Oppgjoerstype.class),
    Personstatus(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Personstatus.class),
    Relasjon(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Relasjon.class),
    Roller(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Rolle.class),
    SaerskiltSkatteplikt(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.SaerskiltSkatteplikt.class),
    Sivilstatus(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Sivilstand.class),
    Skattemeldingstype(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Skattemeldingstype.class),
    Skattepliktstype(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Skattepliktstype.class),
    Spesifiseringstype(no.nav.testnav.libs.dto.tenorsearchservice.v1.Spesifiseringstype.class),
    Stadietype(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.Stadietype.class),
    TekniskNavn(no.nav.testnav.libs.dto.tenorsearchservice.v1.TekniskNavn.class),
    TilleggsskattType(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.TilleggsskattType.class),
    UtenlandskPersonIdentifikasjon(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.UtenlandskPersonIdentifikasjon.class),
    VergeTjenestevirksomhet(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.VergeTjenestevirksomhet.class),
    VergemaalType(no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest.VergemaalType.class);

    private final Class<?> value;
}