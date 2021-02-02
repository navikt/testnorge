package no.nav.registre.testnorge.oppsummeringsdokumentservice.domain;

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
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PermisjonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.ArbeidsforholdModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.PermisjonModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.PersonModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.VirksomhetModel;
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
public class Oppsummeringsdokument {
    private final OppsummeringsdokumentDTO dto;
    private String id;

    public Oppsummeringsdokument(OppsummeringsdokumentDTO dto) {
        this.dto = dto;
    }

    public Oppsummeringsdokument(OppsummeringsdokumentModel model){
        this.id = model.getId();
        this.dto = OppsummeringsdokumentDTO
                .builder()
                .kalendermaaned(model.getKalendermaaned())
                .opplysningspliktigOrganisajonsnummer(model.getOpplysningspliktigOrganisajonsnummer())
                .version(model.getVersion())
                .virksomheter(model.getVirksomheter().stream().map(mapVirksomhetDTO()).collect(Collectors.toList()))
                .build();
    }

    public String getId(){
        return id;
    }

    public Long getVersion(){
        return dto.getVersion();
    }


    private Function<VirksomhetModel, VirksomhetDTO> mapVirksomhetDTO() {
        return value -> VirksomhetDTO
                .builder()
                .organisajonsnummer(value.getOrganisajonsnummer())
                .personer(value.getPersoner().stream().map(mapPersonDTO()).collect(Collectors.toList()))
                .build();
    }

    private Function<PersonModel, PersonDTO> mapPersonDTO() {
        return value -> PersonDTO
                .builder()
                .ident(value.getIdent())
                .arbeidsforhold(value.getArbeidsforhold().stream().map(mapArbeidsforholdDTO()).collect(Collectors.toList()))
                .build();
    }

    private Function<ArbeidsforholdModel, ArbeidsforholdDTO> mapArbeidsforholdDTO() {
        return value -> ArbeidsforholdDTO
                .builder()
                .arbeidsforholdId(value.getArbeidsforholdId())
                .typeArbeidsforhold(value.getTypeArbeidsforhold())
                .arbeidstidsordning(value.getArbeidstidsordning())
                .antallTimerPerUke(value.getAntallTimerPerUke())
                .sluttdato(value.getSluttdato())
                .startdato(value.getStartdato())
                .sisteLoennsendringsdato(value.getSisteLoennsendringsdato())
                .stillingsprosent(value.getStillingsprosent())
                .yrke(value.getYrke())
                .permisjoner(value.getPermisjoner().stream().map(mapPermisjonDTO()).collect(Collectors.toList()))
                .build();
    }


    private Function<PermisjonModel, PermisjonDTO> mapPermisjonDTO() {
        return value -> PermisjonDTO
                .builder()
                .beskrivelse(value.getBeskrivelse())
                .permisjonsprosent(value.getPermisjonsprosent())
                .sluttdato(value.getSluttdato())
                .startdato(value.getStartdato())
                .build();
    }


    public OppsummeringsdokumentModel toModel(String miljo, String origin){
        var model = new OppsummeringsdokumentModel();
        model.setKalendermaaned(dto.getKalendermaaned());
        model.setMiljo(miljo);
        model.setOpplysningspliktigOrganisajonsnummer(dto.getOpplysningspliktigOrganisajonsnummer());
        model.setVersion(dto.getVersion());
        model.setOrigin(origin);
        model.setVirksomheter(dto.getVirksomheter().stream().map(mapVirksomhetModel()).collect(Collectors.toList()));
        return model;
    }

    private Function<VirksomhetDTO, VirksomhetModel> mapVirksomhetModel() {
        return value -> {
            var model = new VirksomhetModel();
            model.setOrganisajonsnummer(value.getOrganisajonsnummer());
            model.setPersoner(value.getPersoner().stream().map(mapPersonModel()).collect(Collectors.toList()));
            return model;
        };
    }

    private Function<PersonDTO, PersonModel> mapPersonModel() {
        return value -> {
            var model = new PersonModel();
            model.setIdent(value.getIdent());
            model.setArbeidsforhold(value.getArbeidsforhold().stream().map(mapArbeidsforholdModel()).collect(Collectors.toList()));
            return model;
        };
    }

    private Function<ArbeidsforholdDTO, ArbeidsforholdModel> mapArbeidsforholdModel() {
        return value -> {
            var model = new ArbeidsforholdModel();
            model.setArbeidsforholdId(value.getArbeidsforholdId());
            model.setArbeidstidsordning(value.getArbeidstidsordning());
            model.setTypeArbeidsforhold(value.getTypeArbeidsforhold());
            model.setSluttdato(value.getSluttdato());
            model.setStartdato(value.getStartdato());
            model.setAntallTimerPerUke(value.getAntallTimerPerUke());
            model.setSisteLoennsendringsdato(value.getSisteLoennsendringsdato());
            model.setStillingsprosent(value.getStillingsprosent());
            model.setYrke(value.getYrke());
            model.setPermisjoner(value.getPermisjoner().stream().map(mapPermisjonModel()).collect(Collectors.toList()));
            return model;
        };
    }

    private Function<PermisjonDTO, PermisjonModel> mapPermisjonModel() {
        return value -> {
            var model = new PermisjonModel();
            model.setPermisjonsprosent(model.getPermisjonsprosent());
            model.setBeskrivelse(model.getBeskrivelse());
            model.setStartdato(model.getStartdato());
            model.setSluttdato(model.getSluttdato());
            return model;
        };
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
        leveranseinformasjon.setKildesystem("oppsummeringsdokument-service");
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
                                    .map(Oppsummeringsdokument::create)
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
                permisjon.setPermisjonsprosent(permisjonDTO.getPermisjonsprosent() != null ? BigDecimal.valueOf(permisjonDTO.getPermisjonsprosent()) : null);
                permisjon.setSluttdato(toXMLGregorianCalendar(permisjonDTO.getSluttdato()));
                permisjon.setStartdato(toXMLGregorianCalendar(permisjonDTO.getStartdato()));
                return permisjon;
            }).collect(Collectors.toList()));
        }
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
