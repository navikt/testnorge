package no.nav.registre.testnorge.arbeidsforhold.domain;

import lombok.SneakyThrows;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Arbeidsforhold;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.EDAGM;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Inntektsmottaker;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.JuridiskEntitet;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Kilde;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Leveranseinformasjon;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Virksomhet;

public class Opplysningspliktig {
    private final OpplysningspliktigDTO dto;

    public Opplysningspliktig(OpplysningspliktigDTO dto) {
        this.dto = dto;
    }

    public String getOrgnummer() {
        return dto.getOpplysningspliktigOrganisajonsnummer();
    }


    public Opplysningspliktig(String opplysningspliktig, Map<String, List<no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold>> map) {
        dto = OpplysningspliktigDTO
                .builder()
                .kalendermaaned(LocalDate.now())
                .opplysningspliktigOrganisajonsnummer(opplysningspliktig)
                .virksomheter(map.keySet().stream().map(orgnummer -> VirksomhetDTO
                        .builder()
                        .organisajonsnummer(orgnummer)
                        .personer(getIdenter(map.get(orgnummer)).stream().map(ident -> PersonDTO
                                .builder()
                                .ident(ident)
                                .arbeidsforhold(getArbeidsforholdFraIdent(ident, map.get(orgnummer)).stream().map(arbeidsforhold -> ArbeidsforholdDTO
                                        .builder()
                                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                                        .arbeidstidsordning(arbeidsforhold.getArbeidstidsordning())
                                        .antallTimerPerUke(arbeidsforhold.getAntallTimerPrUke())
                                        .sisteLoennsendringsdato(arbeidsforhold.getSistLoennsendring())
                                        .sluttdato(arbeidsforhold.getTom())
                                        .startdato(arbeidsforhold.getFom())
                                        .yrke(arbeidsforhold.getYrke())
                                        .stillingsprosent(arbeidsforhold.getStillingsprosent())
                                        .typeArbeidsforhold(arbeidsforhold.getType())
                                        .build()
                                ).collect(Collectors.toList()))
                                .build()
                        ).collect(Collectors.toList()))
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }

    private static Set<String> getIdenter(List<no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold> arbeidsforholds) {
        return arbeidsforholds.stream().map(no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold::getIdent).collect(Collectors.toSet());
    }

    private static List<no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold> getArbeidsforholdFraIdent(String ident, List<no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold> arbeidsforholds) {
        return arbeidsforholds.stream().filter(arbeidsforhold -> arbeidsforhold.getIdent().equals(ident)).collect(Collectors.toList());
    }


    public OpplysningspliktigDTO toDTO() {
        return dto;
    }

    @SneakyThrows
    public EDAGM toEDAGM() {
        List<Virksomhet> virksomheter = dto
                .getVirksomheter()
                .stream()
                .map(Opplysningspliktig::create)
                .collect(Collectors.toList());


        var opplysningspliktig = new no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Opplysningspliktig();

        opplysningspliktig.setNorskIdentifikator(dto.getOpplysningspliktigOrganisajonsnummer());

        JuridiskEntitet juridiskEntitet = new JuridiskEntitet();
        juridiskEntitet.getVirksomhet().addAll(virksomheter);

        Leveranse leveranse = new Leveranse();
        leveranse.setKalendermaaned(toXMLGregorianCalendar(dto.getKalendermaaned()));
        leveranse.setOpplysningspliktig(opplysningspliktig);

        Leveranseinformasjon leveranseinformasjon = new Leveranseinformasjon();
        leveranseinformasjon.setAltinnreferanse("Dummy");
        leveranseinformasjon.setInnleveringstidspunkt(toXMLGregorianCalendar(LocalDate.now()));
        leveranseinformasjon.setMeldingsId(UUID.randomUUID().toString());
        leveranseinformasjon.setKildesystem("testnorge-arbeidsforhold-api");
        leveranseinformasjon.setMeldingsId(UUID.randomUUID().toString());

        leveranse.getLeveranseinformasjon().add(leveranseinformasjon);
        Kilde value = new Kilde();
        value.setKildenavn("Team Dolly");
        value.setKildereferanse(UUID.randomUUID().toString());
        value.setKildeversjon(BigInteger.valueOf(1));
        leveranse.setKilde(value);
        leveranse.setOppgave(juridiskEntitet);

        EDAGM edagm = new EDAGM();
        edagm.setLeveranse(leveranse);

        return edagm;
    }


    private static Virksomhet create(VirksomhetDTO dto) {
        Virksomhet virksomhet = new Virksomhet();
        virksomhet.setNorskIdentifikator(dto.getOrganisajonsnummer());


        virksomhet.getInntektsmottaker().addAll(
                dto.getPersoner().stream().map(personDTO -> {

                    Inntektsmottaker inntektsmottaker = new Inntektsmottaker();
                    Kilde value = new Kilde();
                    value.setKildenavn("Team Dolly");
                    value.setKildereferanse(UUID.randomUUID().toString());
                    value.setKildeversjon(BigInteger.valueOf(1));
                    inntektsmottaker.setKilde(value);
                    inntektsmottaker.setNorskIdentifikator(personDTO.getIdent());
                    inntektsmottaker.getArbeidsforhold().addAll(
                            personDTO.getArbeidsforhold()
                                    .stream()
                                    .map(Opplysningspliktig::create)
                                    .collect(Collectors.toList())
                    );
                    return inntektsmottaker;
                }).collect(Collectors.toList())
        );

        return virksomhet;
    }

    private static Arbeidsforhold create(ArbeidsforholdDTO dto) {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.setStartdato(toXMLGregorianCalendar(dto.getStartdato()));
        arbeidsforhold.setSluttdato(toXMLGregorianCalendar(dto.getSluttdato()));
        arbeidsforhold.setTypeArbeidsforhold(dto.getTypeArbeidsforhold());
        arbeidsforhold.setArbeidsforholdId(dto.getArbeidsforholdId());
        arbeidsforhold.setAntallTimerPerUkeSomEnFullStillingTilsvarer(dto.getAntallTimerPerUke() != null ? BigDecimal.valueOf(dto.getAntallTimerPerUke()) : null);
        arbeidsforhold.setYrke(dto.getYrke());
        arbeidsforhold.setArbeidstidsordning(dto.getArbeidstidsordning());
        arbeidsforhold.setStillingsprosent(dto.getStillingsprosent() != null ? BigDecimal.valueOf(dto.getStillingsprosent()) : null);
        arbeidsforhold.setSisteLoennsendringsdato(toXMLGregorianCalendar(dto.getSisteLoennsendringsdato()));
        return arbeidsforhold;
    }


    @SneakyThrows
    private static XMLGregorianCalendar toXMLGregorianCalendar(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
    }
}
