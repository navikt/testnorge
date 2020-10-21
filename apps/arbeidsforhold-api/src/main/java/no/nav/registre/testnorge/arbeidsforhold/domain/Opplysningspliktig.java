package no.nav.registre.testnorge.arbeidsforhold.domain;

import lombok.SneakyThrows;
import org.springframework.lang.Nullable;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Arbeidsforhold;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.EDAGM;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Inntektsmottaker;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.JuridiskEntitet;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Virksomhet;

public class Opplysningspliktig {
    private final OpplysningspliktigDTO dto;

    public Opplysningspliktig(OpplysningspliktigDTO dto) {
        this.dto = dto;
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


    public OpplysningspliktigDTO toDTO(){
        return dto;
    }

    @SneakyThrows
    public EDAGM toEDAGM() {
        List<Virksomhet> virksomheter = dto
                .getVirksomheter()
                .stream()
                .map(Opplysningspliktig::create)
                .collect(Collectors.toList());

        Leveranse leveranse = Leveranse
                .builder()
                .withKalendermaaned(toXMLGregorianCalendar(dto.getKalendermaaned()))
                .withOpplysningspliktig(no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Opplysningspliktig
                        .builder()
                        .withNorskIdentifikator(dto.getOpplysningspliktigOrganisajonsnummer())
                        .build()
                )
                .withOppgave(JuridiskEntitet.builder().withVirksomhet(virksomheter).build())
                .build();

        return EDAGM
                .builder()
                .withLeveranse(leveranse)
                .build();
    }


    private static Virksomhet create(VirksomhetDTO dto) {
        return Virksomhet
                .builder()
                .withNorskIdentifikator(dto.getOrganisajonsnummer())
                .withInntektsmottaker(dto
                        .getPersoner()
                        .stream()
                        .map(personDTO -> Inntektsmottaker
                                .builder()
                                .withNorskIdentifikator(personDTO.getIdent())
                                .withArbeidsforhold(personDTO
                                        .getArbeidsforhold()
                                        .stream()
                                        .map(Opplysningspliktig::create)
                                        .collect(Collectors.toList())
                                )
                                .build()
                        ).collect(Collectors.toList())
                )
                .build();
    }

    private static Arbeidsforhold create(ArbeidsforholdDTO dto) {
        return Arbeidsforhold
                .builder()
                .withStartdato(toXMLGregorianCalendar(dto.getStartdato()))
                .withSluttdato(toXMLGregorianCalendar(dto.getSluttdato()))
                .withTypeArbeidsforhold(dto.getTypeArbeidsforhold())
                .withArbeidsforholdId(dto.getArbeidsforholdId())
                .withAntallTimerPerUkeSomEnFullStillingTilsvarer(dto.getAntallTimerPerUke() != null
                        ? BigDecimal.valueOf(dto.getAntallTimerPerUke())
                        : null
                )
                .withYrke(dto.getYrke())
                .withArbeidstidsordning(dto.getArbeidstidsordning())
                .withStillingsprosent(
                        dto.getStillingsprosent() != null
                                ? BigDecimal.valueOf(dto.getStillingsprosent())
                                : null
                )
                .withSisteLoennsendringsdato(toXMLGregorianCalendar(dto.getSisteLoennsendringsdato()))
                .build();
    }


    @SneakyThrows
    private static XMLGregorianCalendar toXMLGregorianCalendar(@Nullable LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
    }
}
