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
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.AvvikDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.FartoeyDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.InntektDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.PermisjonDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.ArbeidsforholdModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.AvvikModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.FartoeyModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.InntektModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.PermisjonModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.PersonModel;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.VirksomhetModel;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Alvorlighetsgrad;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Arbeidsforhold;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Avvik;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.EDAGM;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Fartoey;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Inntekt;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Inntektsmottaker;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.JuridiskEntitet;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Kilde;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Leveranseinformasjon;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Loennsinntekt;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.ObjectFactory;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Permisjon;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Spesifikasjon;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Virksomhet;

@Slf4j
public class Oppsummeringsdokument {
    private final OppsummeringsdokumentDTO dto;
    private final Populasjon populasjon;
    private String id;

    public Oppsummeringsdokument(OppsummeringsdokumentDTO dto, Populasjon populasjon) {
        this.dto = dto;
        this.populasjon = populasjon;
    }

    public Oppsummeringsdokument(OppsummeringsdokumentModel model) {
        this.id = model.getId();
        this.populasjon = model.getPopulasjon();
        this.dto = OppsummeringsdokumentDTO
                .builder()
                .kalendermaaned(model.getKalendermaaned())
                .opplysningspliktigOrganisajonsnummer(model.getOpplysningspliktigOrganisajonsnummer())
                .version(model.getVersion())
                .virksomheter(model.getVirksomheter().stream().map(mapVirksomhetDTO()).collect(Collectors.toList()))
                .build();
    }

    public String getId() {
        return id;
    }

    public String getOpplysningspliktigOrganisajonsnummer() {
        return dto.getOpplysningspliktigOrganisajonsnummer();
    }

    public Long getVersion() {
        return dto.getVersion();
    }

    public Set<String> getIdenter() {
        return dto.getVirksomheter()
                .stream()
                .flatMap(virksomhet -> virksomhet.getPersoner().stream().map(PersonDTO::getIdent))
                .collect(Collectors.toSet());
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
                .historikk(value.getHistorikk())
                .fartoey(value.getFartoey() != null ? FartoeyDTO
                        .builder()
                        .skipstype(value.getFartoey().getSkipstype())
                        .fartsomraade(value.getFartoey().getFartsomraade())
                        .skipsregister(value.getFartoey().getSkipsregister())
                        .build() : null
                )
                .inntekter(value.getInntekter().stream().map(mapInntektDTO()).collect(Collectors.toList()))
                .build();
    }

    private Function<InntektModel, InntektDTO> mapInntektDTO() {
        return value -> InntektDTO
                .builder()
                .antall(value.getAntall())
                .opptjeningsland(value.getOpptjeningsland())
                .sluttdatoOpptjeningsperiode(value.getSluttdatoOpptjeningsperiode())
                .startdatoOpptjeningsperiode(value.getStartdatoOpptjeningsperiode())
                .build();
    }


    private Function<PermisjonModel, PermisjonDTO> mapPermisjonDTO() {
        return value -> PermisjonDTO
                .builder()
                .permisjonId(value.getPermisjonId())
                .beskrivelse(value.getBeskrivelse())
                .permisjonsprosent(value.getPermisjonsprosent())
                .sluttdato(value.getSluttdato())
                .startdato(value.getStartdato())
                .build();
    }


    public OppsummeringsdokumentModel toModel(String miljo, String origin) {
        var model = new OppsummeringsdokumentModel();
        model.setKalendermaaned(dto.getKalendermaaned());
        model.setMiljo(miljo);
        model.setOpplysningspliktigOrganisajonsnummer(dto.getOpplysningspliktigOrganisajonsnummer());
        model.setVersion(dto.getVersion());
        model.setOrigin(origin);
        model.setPopulasjon(populasjon);
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
            model.setHistorikk(value.getHistorikk());
            if (value.getFartoey() != null) {
                FartoeyModel fartoey = new FartoeyModel();
                fartoey.setFartsomraade(value.getFartoey().getFartsomraade());
                fartoey.setSkipsregister(value.getFartoey().getSkipsregister());
                fartoey.setSkipstype(value.getFartoey().getSkipstype());
                model.setFartoey(fartoey);
            }
            model.setInntekter(value.getInntekter().stream().map(mapInntektModel()).collect(Collectors.toList()));
            model.setPermisjoner(value.getPermisjoner().stream().map(mapPermisjonModel()).collect(Collectors.toList()));
            model.setAvvik(value.getAvvik().stream().map(mapAvvikDTOToModel()).collect(Collectors.toList()));
            return model;
        };
    }

    private Function<InntektDTO, InntektModel> mapInntektModel() {
        return value -> {
            var model = new InntektModel();
            model.setAntall(value.getAntall());
            model.setOpptjeningsland(value.getOpptjeningsland());
            model.setSluttdatoOpptjeningsperiode(value.getSluttdatoOpptjeningsperiode());
            model.setStartdatoOpptjeningsperiode(value.getStartdatoOpptjeningsperiode());
            model.setAvvik(value.getAvvik().stream().map(mapAvvikDTOToModel()).collect(Collectors.toList()));
            return model;
        };
    }

    private Function<PermisjonDTO, PermisjonModel> mapPermisjonModel() {
        return value -> {
            var model = new PermisjonModel();
            model.setPermisjonId(value.getPermisjonId());
            model.setPermisjonsprosent(value.getPermisjonsprosent());
            model.setBeskrivelse(value.getBeskrivelse());
            model.setStartdato(value.getStartdato());
            model.setSluttdato(value.getSluttdato());
            model.setAvvik(value.getAvvik().stream().map(mapAvvikDTOToModel()).collect(Collectors.toList()));
            return model;
        };
    }

    private Function<AvvikDTO, AvvikModel> mapAvvikDTOToModel() {
        return dto -> {
            var model = new AvvikModel();
            model.setNavn(dto.getNavn());
            model.setId(dto.getId());
            model.setAlvorlighetsgrad(dto.getAlvorlighetsgrad());
            return model;
        };
    }

    public OppsummeringsdokumentDTO toDTO() {
        return dto;
    }

    @SneakyThrows
    public String toXml() {
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
                    inntektsmottaker.getInntekt().addAll(
                            personDTO.getArbeidsforhold()
                                    .stream()
                                    .map(Oppsummeringsdokument::createInntekter)
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList())
                    );
                    return inntektsmottaker;
                }).collect(Collectors.toList())
        );

        return virksomhet;
    }

    private static List<Inntekt> createInntekter(ArbeidsforholdDTO dto) {
        if (dto.getInntekter() == null) {
            return Collections.emptyList();
        }

        return dto.getInntekter().stream().map(value -> {
            var inntekt = new Inntekt();
            inntekt.setArbeidsforholdId(dto.getArbeidsforholdId());
            var loennsinntekt = new Loennsinntekt();
            Optional.ofNullable(value.getAntall()).ifPresent(antall -> loennsinntekt.setAntall(BigDecimal.valueOf(antall)));
            Optional.ofNullable(value.getOpptjeningsland()).ifPresent(opptjeningsland -> {
                var spesifikasjon = new Spesifikasjon();
                spesifikasjon.setOpptjeningsland(opptjeningsland);
                loennsinntekt.setSpesifikasjon(spesifikasjon);
            });
            inntekt.setLoennsinntekt(loennsinntekt);
            inntekt.setSluttdatoOpptjeningsperiode(toXMLGregorianCalendar(value.getSluttdatoOpptjeningsperiode()));
            inntekt.setStartdatoOpptjeningsperiode(toXMLGregorianCalendar(value.getStartdatoOpptjeningsperiode()));
            inntekt.getAvvik().addAll(value.getAvvik().stream().map(mapDTOToAvvik()).collect(Collectors.toList()));
            return inntekt;
        }).collect(Collectors.toList());
    }

    private static Function<AvvikDTO, Avvik> mapDTOToAvvik() {
        return value -> {
            var avvik = new Avvik();
            avvik.setAlvorlighetsgrad(Alvorlighetsgrad.fromValue(value.getAlvorlighetsgrad()));
            avvik.setId(value.getId());
            avvik.setNavn(value.getNavn());
            return avvik;
        };
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
        arbeidsforhold.getAvvik().addAll(dto.getAvvik().stream().map(mapDTOToAvvik()).collect(Collectors.toList()));
        if (dto.getPermisjoner() != null) {
            var permisjoner = dto.getPermisjoner().stream().map(permisjonDTO -> {
                Permisjon permisjon = new Permisjon();
                permisjon.setBeskrivelse(permisjonDTO.getBeskrivelse());
                permisjon.setPermisjonId(permisjonDTO.getPermisjonId());
                permisjon.setPermisjonsprosent(permisjonDTO.getPermisjonsprosent() != null ? BigDecimal.valueOf(permisjonDTO.getPermisjonsprosent()) : null);
                permisjon.setSluttdato(toXMLGregorianCalendar(permisjonDTO.getSluttdato()));
                permisjon.setStartdato(toXMLGregorianCalendar(permisjonDTO.getStartdato()));
                permisjon.getAvvik().addAll(permisjonDTO.getAvvik().stream().map(mapDTOToAvvik()).collect(Collectors.toList()));
                return permisjon;
            }).collect(Collectors.toList());
            arbeidsforhold.getPermisjon().addAll(permisjoner);
        }

        if (dto.getFartoey() != null) {
            Fartoey fartoey = new Fartoey();
            fartoey.setFartsomraade(dto.getFartoey().getFartsomraade());
            fartoey.setSkipsregister(dto.getFartoey().getSkipsregister());
            fartoey.setSkipstype(dto.getFartoey().getSkipstype());
            arbeidsforhold.setFartoey(fartoey);
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
