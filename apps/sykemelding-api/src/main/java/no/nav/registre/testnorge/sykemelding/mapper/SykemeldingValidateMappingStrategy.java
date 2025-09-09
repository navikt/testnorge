package no.nav.registre.testnorge.sykemelding.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.dto.ReceivedSykemeldingDTO;
import no.nav.registre.testnorge.sykemelding.external.eiFellesformat.XMLMottakenhetBlokk;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLMsgHead;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.XMLCS;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLDynaSvarType;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet;
import no.nav.registre.testnorge.sykemelding.external.xmlstds.helseopplysningerarbeidsuforhet._2013_10_01.XMLHelseOpplysningerArbeidsuforhet.UtdypendeOpplysninger.SpmGruppe;
import no.nav.testnav.libs.dto.sykemelding.v1.UtdypendeOpplysningerDTO.Restriksjon;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SykemeldingValidateMappingStrategy implements MappingStrategy {

    private static final String DUMMY_FNR = "12508407724";
    private static final String DUMMY_AKTOER_ID = "123456789";
    private static final String DUMMY_LEGEKONTOR_ORG_NAME = "Testkontoret";
    private static final String DUMMY_GATE = "Testveien 1";
    private static final Integer DUMMY_POSTNUMMER = 1234;
    private static final Map<String, Map<String, ReceivedSykemeldingDTO.SporsmalSvar>> DUMMY_UTDYPENDE_OPPLYSNINGER =
            Collections.singletonMap("1", Collections.singletonMap("2",
                    ReceivedSykemeldingDTO.SporsmalSvar.builder()
                            .sporsmal("Hva er din hovedplage?")
                            .svar("Tester")
                            .restriksjoner(List.of(Restriksjon.SKJERMET_FOR_ARBEIDSGIVER))
                            .build()));

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Sykemelding.class, ReceivedSykemeldingDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Sykemelding source, ReceivedSykemeldingDTO target, MappingContext context) {

                        var sykemeldingBuilder = ReceivedSykemeldingDTO.Sykemelding.builder();


                        source.getFellesformat().getAny()
                                .forEach(any -> {

                                    target.setMsgId(source.getMsgId());
                                    target.setNavLogId(source.getMsgId());
                                    target.setId(source.getMsgId());
                                    target.setPersonNrPasient(DUMMY_FNR);
                                    target.setLegekontorOrgName(DUMMY_LEGEKONTOR_ORG_NAME);
                                    target.setFellesformat(String.valueOf(source.getFellesformat()));

                                    if (any instanceof XMLMsgHead xmlMsgHead) {
                                        xmlMsgHead.getDocument().forEach(document ->
                                                document.getRefDoc().getContent().getAny().forEach(refDoc -> {

                                                    if (refDoc instanceof XMLHelseOpplysningerArbeidsuforhet xmlHelseOpplysningerArbeidsuforhet) {
                                                        sykemeldingBuilder.syketilfelleStartDato(xmlHelseOpplysningerArbeidsuforhet.getSyketilfelleStartDato())
                                                                .id(source.getMsgId())
                                                                .msgId(source.getMsgId())
                                                                .pasientAktoerId(DUMMY_AKTOER_ID)
                                                                .navnFastlege(xmlHelseOpplysningerArbeidsuforhet.getPasient().getNavnFastlege())
                                                                .arbeidsgiver(ReceivedSykemeldingDTO.Arbeidsgiver.builder()
                                                                        .harArbeidsgiver(mapHarArbeidsgiver(xmlHelseOpplysningerArbeidsuforhet.getArbeidsgiver().getHarArbeidsgiver()))
                                                                        .yrkesbetegnelse(xmlHelseOpplysningerArbeidsuforhet.getArbeidsgiver().getYrkesbetegnelse())
                                                                        .stillingsprosent(xmlHelseOpplysningerArbeidsuforhet.getArbeidsgiver().getStillingsprosent())
                                                                        .navn(xmlHelseOpplysningerArbeidsuforhet.getArbeidsgiver().getNavnArbeidsgiver())
                                                                        .build())
                                                                .medisinskVurdering(mapMedisinskVurdering(xmlHelseOpplysningerArbeidsuforhet.getMedisinskVurdering()))
                                                                .behandler(ReceivedSykemeldingDTO.Behandler.builder()
                                                                        .fornavn(xmlHelseOpplysningerArbeidsuforhet.getBehandler().getNavn().getFornavn())
                                                                        .mellomnavn(xmlHelseOpplysningerArbeidsuforhet.getBehandler().getNavn().getMellomnavn())
                                                                        .etternavn(xmlHelseOpplysningerArbeidsuforhet.getBehandler().getNavn().getEtternavn())
                                                                        .fnr(xmlHelseOpplysningerArbeidsuforhet.getBehandler().getId().getFirst().getId())
                                                                        .aktoerId(DUMMY_AKTOER_ID)
                                                                        .adresse(ReceivedSykemeldingDTO.Adresse.builder()
                                                                                .gate(DUMMY_GATE)
                                                                                .postnummer(DUMMY_POSTNUMMER)
                                                                                .build())
                                                                        .build())
                                                                .avsenderSystem(ReceivedSykemeldingDTO.AvsenderSystem.builder()
                                                                        .navn(xmlHelseOpplysningerArbeidsuforhet.getAvsenderSystem().getSystemNavn())
                                                                        .versjon(xmlHelseOpplysningerArbeidsuforhet.getAvsenderSystem().getSystemVersjon())
                                                                        .build())
                                                                .kontaktMedPasient(mapKontaktMedPasient(xmlHelseOpplysningerArbeidsuforhet.getKontaktMedPasient()))
                                                                .tiltakArbeidsplassen(nonNull(xmlHelseOpplysningerArbeidsuforhet.getTiltak()) ?
                                                                        xmlHelseOpplysningerArbeidsuforhet.getTiltak().getTiltakArbeidsplassen() : null)
                                                                .tiltakNAV(nonNull(xmlHelseOpplysningerArbeidsuforhet.getTiltak()) ?
                                                                        xmlHelseOpplysningerArbeidsuforhet.getTiltak().getTiltakNAV() : null)
                                                                .andreTiltak(nonNull(xmlHelseOpplysningerArbeidsuforhet.getTiltak()) ?
                                                                        xmlHelseOpplysningerArbeidsuforhet.getTiltak().getAndreTiltak() : null)
                                                                .utdypendeOpplysninger(mapUtdypendeOpplysninger(xmlHelseOpplysningerArbeidsuforhet.getUtdypendeOpplysninger()))
                                                                .perioder(mapPerioder(xmlHelseOpplysningerArbeidsuforhet.getAktivitet().getPeriode()))
                                                                .prognose(mapPrognose(xmlHelseOpplysningerArbeidsuforhet.getPrognose()))
                                                                .behandletTidspunkt(LocalDateTime.now())
                                                                .signaturDato(LocalDateTime.now());
                                                    }
                                                }));
                                    }
                                    if (any instanceof XMLMottakenhetBlokk xmlMottakenhetBlokk) {

                                        target.setMottattDato(convertDateNTime(xmlMottakenhetBlokk.getMottattDatotid()));

                                    }
                                    var sykemelding = sykemeldingBuilder.build();

                                    target.setPersonNrLege(sykemelding.getBehandler().getFnr());
                                    target.setSykmelding(sykemelding);
                                });
                    }
                })
                .register();
    }

    private ReceivedSykemeldingDTO.KontaktMedpasient mapKontaktMedPasient(XMLHelseOpplysningerArbeidsuforhet.KontaktMedPasient kontaktMedPasient) {

        if (isNull(kontaktMedPasient)) {
            return null;
        }

        return ReceivedSykemeldingDTO.KontaktMedpasient.builder()
                .begrunnelseIkkeKontakt(kontaktMedPasient.getBegrunnIkkeKontakt())
                .kontaktDato(kontaktMedPasient.getKontaktDato())
                .build();
    }

    private ReceivedSykemeldingDTO.ArbeidsgiverType mapHarArbeidsgiver(XMLCS harArbeidsgiver) {

        if (isNull(harArbeidsgiver)) {
            return ReceivedSykemeldingDTO.ArbeidsgiverType.INGEN_ARBEIDSGIVER;
        }
        return switch (harArbeidsgiver.getV()) {
            case "1" ->  ReceivedSykemeldingDTO.ArbeidsgiverType.EN_ARBEIDSGIVER;
            case "2" ->  ReceivedSykemeldingDTO.ArbeidsgiverType.FLERE_ARBEIDSGIVERE;
            default -> ReceivedSykemeldingDTO.ArbeidsgiverType.INGEN_ARBEIDSGIVER;
        };
    }

    private List<ReceivedSykemeldingDTO.Periode> mapPerioder(List<XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode> perioder) {

        return perioder.stream()
                .map(periode -> ReceivedSykemeldingDTO.Periode.builder()
                        .fom(periode.getPeriodeFOMDato())
                        .tom(periode.getPeriodeTOMDato())
                        .avventendeInnspillTilArbeidsgiver(nonNull(periode.getAvventendeSykmelding()) ?
                                periode.getAvventendeSykmelding().getInnspillTilArbeidsgiver() : null)
                        .gradert(ReceivedSykemeldingDTO.Gradert.builder()
                                .grad(nonNull(periode.getGradertSykmelding()) ?
                                        periode.getGradertSykmelding().getSykmeldingsgrad() : null)
                                .reisetilskudd(nonNull(periode.getGradertSykmelding()) ?
                                        periode.getGradertSykmelding().isReisetilskudd() : null)
                                .build())
                        .aktivitetIkkeMulig(mapAktivitetIkkeMulig(periode.getAktivitetIkkeMulig()))
                        .behandlingsdager(nonNull(periode.getBehandlingsdager()) ?
                                periode.getBehandlingsdager().getAntallBehandlingsdagerUke() : null)
                        .reisetilskudd(periode.isReisetilskudd())
                        .build())
                .toList();
    }

    private ReceivedSykemeldingDTO.Prognose mapPrognose(XMLHelseOpplysningerArbeidsuforhet.Prognose prognose) {

        if (isNull(prognose)) {
            return null;
        }

        return ReceivedSykemeldingDTO.Prognose.builder()
                .arbeidsforEtterPeriode(prognose.isArbeidsforEtterEndtPeriode())
                .hensynArbeidsplassen(prognose.getBeskrivHensynArbeidsplassen())
                .erIArbeid(nonNull(prognose.getErIArbeid()) ?
                        ReceivedSykemeldingDTO.ErIArbeid.builder()
                                .annetArbeidPaSikt(prognose.getErIArbeid().isAnnetArbeidPaSikt())
                                .arbeidFOM(prognose.getErIArbeid().getArbeidFraDato())
                                .vurderingsdato(prognose.getErIArbeid().getVurderingDato())
                                .egetArbeidPaSikt(prognose.getErIArbeid().isEgetArbeidPaSikt())
                                .build() : null)
                .erIkkeIArbeid(nonNull(prognose.getErIkkeIArbeid()) ?
                        ReceivedSykemeldingDTO.ErIkkeIArbeid.builder()
                                .arbeidsforFOM(prognose.getErIkkeIArbeid().getArbeidsforFraDato())
                                .vurderingsdato(prognose.getErIkkeIArbeid().getVurderingDato())
                                .arbeidsforPaSikt(prognose.getErIkkeIArbeid().isArbeidsforPaSikt())
                                .build() : null)
                .build();
    }

    private Map<String, Map<String, ReceivedSykemeldingDTO.SporsmalSvar>> mapUtdypendeOpplysninger(XMLHelseOpplysningerArbeidsuforhet.UtdypendeOpplysninger utdypendeOpplysninger) {

        return isNull(utdypendeOpplysninger) || utdypendeOpplysninger.getSpmGruppe().isEmpty() ? DUMMY_UTDYPENDE_OPPLYSNINGER :
                utdypendeOpplysninger.getSpmGruppe().stream()
                        .collect(Collectors.toMap(SpmGruppe::getSpmGruppeId,
                                gruppe -> gruppe.getSpmSvar().stream()
                                        .collect(Collectors.toMap(XMLDynaSvarType::getSpmId, spmsvar -> ReceivedSykemeldingDTO.SporsmalSvar.builder()
                                                .sporsmal(spmsvar.getSpmTekst())
                                                .svar(spmsvar.getSvarTekst())
                                                .restriksjoner(spmsvar.getRestriksjon().getRestriksjonskode().stream()
                                                        .map(XMLCS::getDN)
                                                        .map(Restriksjon::valueOf)
                                                        .toList())
                                                .build()))));
    }

    private ReceivedSykemeldingDTO.AktivitetIkkeMulig mapAktivitetIkkeMulig(XMLHelseOpplysningerArbeidsuforhet.Aktivitet.Periode.AktivitetIkkeMulig aktivitetIkkeMulig) {

        if (isNull(aktivitetIkkeMulig)) {
            return null;
        }

        var medisinskeArsakTyper = !aktivitetIkkeMulig.getMedisinskeArsaker().getArsakskode().isEmpty() ?
                aktivitetIkkeMulig.getMedisinskeArsaker().getArsakskode().stream()
                        .map(XMLCS::getV)
                        .map(ReceivedSykemeldingDTO.MedisinskArsakType::valueOf)
                        .toList() : null;

        var arbeidsforholdArsakTyper = !aktivitetIkkeMulig.getArbeidsplassen().getArsakskode().isEmpty() ?
                aktivitetIkkeMulig.getArbeidsplassen().getArsakskode().stream()
                        .map(XMLCS::getV)
                        .map(ReceivedSykemeldingDTO.ArbeidsrelatertArsakType::valueOf)
                        .toList() : null;

        return ReceivedSykemeldingDTO.AktivitetIkkeMulig.builder()
                .medisinskArsak(nonNull(aktivitetIkkeMulig.getMedisinskeArsaker()) ?
                        ReceivedSykemeldingDTO.MedisinskArsak.builder()
                                .arsak(medisinskeArsakTyper)
                                .beskrivelse(aktivitetIkkeMulig.getMedisinskeArsaker().getBeskriv())
                                .build() : null)
                .arbeidsrelatertArsak(nonNull(aktivitetIkkeMulig.getArbeidsplassen()) ?
                        ReceivedSykemeldingDTO.ArbeidsrelatertArsak.builder()
                                .arsak(arbeidsforholdArsakTyper)
                                .beskrivelse(aktivitetIkkeMulig.getArbeidsplassen().getBeskriv())
                                .build() : null)
                .build();
    }

    private ReceivedSykemeldingDTO.MedisinskVurdering mapMedisinskVurdering(XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering xmlMedisinskVurdering) {

        return ReceivedSykemeldingDTO.MedisinskVurdering.builder()
                .hovedDiagnose(ReceivedSykemeldingDTO.Diagnose.builder()
                        .kode(xmlMedisinskVurdering.getHovedDiagnose().getDiagnosekode().getV())
                        .system(xmlMedisinskVurdering.getHovedDiagnose().getDiagnosekode().getS())
                        .tekst(xmlMedisinskVurdering.getHovedDiagnose().getDiagnosekode().getDN())
                        .build())
                .biDiagnoser(mapBidiagnoser(xmlMedisinskVurdering.getBiDiagnoser()))
                .annenFraversArsak(ReceivedSykemeldingDTO.AnnenFraversArsak.builder()
                        .beskrivelse(xmlMedisinskVurdering.getAnnenFraversArsak().getBeskriv())
                        .build())
                .svangerskap(xmlMedisinskVurdering.isSvangerskap())
                .yrkesskade(xmlMedisinskVurdering.isYrkesskade())
                .yrkesskadeDato(xmlMedisinskVurdering.getYrkesskadeDato())
                .build();
    }

    private List<ReceivedSykemeldingDTO.Diagnose> mapBidiagnoser(XMLHelseOpplysningerArbeidsuforhet.MedisinskVurdering.BiDiagnoser biDiagnoser) {

        if (isNull(biDiagnoser)) {
            return emptyList();
        }

        return biDiagnoser.getDiagnosekode().stream()
                .map(diagnose -> ReceivedSykemeldingDTO.Diagnose.builder()
                        .kode(diagnose.getV())
                        .system(diagnose.getS())
                        .tekst(diagnose.getDN())
                        .build())
                .toList();
    }

    private LocalDateTime convertDateNTime(XMLGregorianCalendar xmlGregorianCalendar) {

        return LocalDateTime.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(),
                xmlGregorianCalendar.getDay(), xmlGregorianCalendar.getHour(),
                xmlGregorianCalendar.getMinute(), xmlGregorianCalendar.getSecond(),
                xmlGregorianCalendar.getMillisecond());
    }
}
