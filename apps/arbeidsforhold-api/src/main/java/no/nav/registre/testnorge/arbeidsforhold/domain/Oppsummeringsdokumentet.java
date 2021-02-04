package no.nav.registre.testnorge.arbeidsforhold.domain;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PermisjonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Arbeidsforhold;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.EDAGM;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Inntektsmottaker;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.JuridiskEntitet;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Kilde;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Leveranseinformasjon;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.ObjectFactory;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Permisjon;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Virksomhet;

@Slf4j
public class Oppsummeringsdokumentet {
    private final OppsummeringsdokumentDTO dto;

    public Oppsummeringsdokumentet(OppsummeringsdokumentDTO dto) {
        this.dto = dto;
    }

    public String getOrgnummer() {
        return dto.getOpplysningspliktigOrganisajonsnummer();
    }

    public LocalDate getRapporteringsmaaned() {
        return dto.getKalendermaaned();
    }

    public Long getVersion() {
        return dto.getVersion();
    }

    public Oppsummeringsdokumentet(EDAGM edagm) {
        var leveranse = edagm.getLeveranse();
        var oppgave = edagm.getLeveranse().getOppgave();
        dto = OppsummeringsdokumentDTO
                .builder()
                .kalendermaaned(toLocalDate(leveranse.getKalendermaaned()))
                .opplysningspliktigOrganisajonsnummer(leveranse.getOpplysningspliktig().getNorskIdentifikator())
                .version(leveranse.getKilde().getKildeversjon().longValue())
                .virksomheter(oppgave.getVirksomhet().stream().map(virksomhet -> VirksomhetDTO
                        .builder()
                        .organisajonsnummer(virksomhet.getNorskIdentifikator())
                        .personer(virksomhet.getInntektsmottaker().stream().map(inntektsmottaker -> PersonDTO
                                .builder()
                                .ident(inntektsmottaker.getNorskIdentifikator())
                                .arbeidsforhold(inntektsmottaker.getArbeidsforhold().stream().map(arbeidsforhold -> ArbeidsforholdDTO
                                        .builder()
                                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                                        .arbeidstidsordning(arbeidsforhold.getArbeidstidsordning())
                                        .antallTimerPerUke(arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer() != null
                                                ? arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer().floatValue()
                                                : null
                                        )
                                        .sisteLoennsendringsdato(toLocalDate(arbeidsforhold.getSisteLoennsendringsdato()))
                                        .startdato(toLocalDate(arbeidsforhold.getStartdato()))
                                        .sluttdato(toLocalDate(arbeidsforhold.getSluttdato()))
                                        .yrke(arbeidsforhold.getYrke())
                                        .stillingsprosent(arbeidsforhold.getStillingsprosent() != null
                                                ? arbeidsforhold.getStillingsprosent().floatValue()
                                                : null
                                        )
                                        .permisjoner(arbeidsforhold.getPermisjon().stream().map(permisjon -> PermisjonDTO
                                                .builder()
                                                .beskrivelse(permisjon.getBeskrivelse())
                                                .permisjonsprosent(permisjon.getPermisjonsprosent().floatValue())
                                                .sluttdato(toLocalDate(permisjon.getSluttdato()))
                                                .startdato(toLocalDate(permisjon.getStartdato()))
                                                .build()
                                        ).collect(Collectors.toList()))
                                        .typeArbeidsforhold(arbeidsforhold.getTypeArbeidsforhold())
                                        .build()
                                ).collect(Collectors.toList()))
                                .build()
                        ).collect(Collectors.toList()))
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }

    public Oppsummeringsdokumentet(String opplysningspliktig, Map<String, List<no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold>> map) {
        dto = OppsummeringsdokumentDTO
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


    public OppsummeringsdokumentDTO toDTO() {
        return dto;
    }

    @SneakyThrows
    public String toXml(){
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EDAGM> melding = objectFactory.createMelding(toEDAGM());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(melding, sw);
        return sw.toString();
    }

    @SneakyThrows
    public EDAGM toEDAGM() {
        List<Virksomhet> virksomheter = dto
                .getVirksomheter()
                .stream()
                .map(value -> create(value, dto.getVersion()))
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

        ZonedDateTime zoneDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(zoneDateTime);
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory
                .newInstance()
                .newXMLGregorianCalendar(gregorianCalendar);

        leveranseinformasjon.setInnleveringstidspunkt(xmlGregorianCalendar);
        leveranseinformasjon.setMeldingsId(UUID.randomUUID().toString());
        leveranseinformasjon.setKildesystem("testnorge-arbeidsforhold-api");
        leveranseinformasjon.setMeldingsId(UUID.randomUUID().toString());

        leveranse.getLeveranseinformasjon().add(leveranseinformasjon);
        Kilde value = new Kilde();
        value.setKildenavn("Team Dolly");
        value.setKildereferanse(UUID.randomUUID().toString());
        value.setKildeversjon(BigInteger.valueOf(dto.getVersion()));
        leveranse.setKilde(value);
        leveranse.setOppgave(juridiskEntitet);

        EDAGM edagm = new EDAGM();
        edagm.setLeveranse(leveranse);

        return edagm;
    }


    private static Virksomhet create(VirksomhetDTO dto, Long version) {
        Virksomhet virksomhet = new Virksomhet();
        virksomhet.setNorskIdentifikator(dto.getOrganisajonsnummer());


        virksomhet.getInntektsmottaker().addAll(
                dto.getPersoner().stream().map(personDTO -> {

                    Inntektsmottaker inntektsmottaker = new Inntektsmottaker();
                    Kilde value = new Kilde();
                    value.setKildenavn("Team Dolly");
                    value.setKildereferanse(UUID.randomUUID().toString());
                    value.setKildeversjon(BigInteger.valueOf(version));
                    inntektsmottaker.setKilde(value);
                    inntektsmottaker.setNorskIdentifikator(personDTO.getIdent());
                    inntektsmottaker.getArbeidsforhold().addAll(
                            personDTO.getArbeidsforhold()
                                    .stream()
                                    .map(Oppsummeringsdokumentet::create)
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
        if (dto.getPermisjoner() != null) {
            arbeidsforhold.getPermisjon().addAll(dto.getPermisjoner().stream().map(permisjonDTO -> {
                Permisjon permisjon = new Permisjon();
                permisjon.setBeskrivelse(permisjonDTO.getBeskrivelse());
                permisjon.setPermisjonId(UUID.randomUUID().toString());
                permisjon.setPermisjonsprosent(BigDecimal.valueOf(permisjonDTO.getPermisjonsprosent()));
                permisjon.setSluttdato(toXMLGregorianCalendar(permisjonDTO.getSluttdato()));
                permisjon.setStartdato(toXMLGregorianCalendar(permisjonDTO.getStartdato()));
                return permisjon;
            }).collect(Collectors.toList()));
        }
        return arbeidsforhold;
    }


    private static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }

        return LocalDate.of(
                calendar.getYear(),
                calendar.getMonth(),
                1
        );
    }

    @SneakyThrows
    private static XMLGregorianCalendar toXMLGregorianCalendar(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
    }
}
