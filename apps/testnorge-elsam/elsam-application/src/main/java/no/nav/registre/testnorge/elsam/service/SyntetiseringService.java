package no.nav.registre.testnorge.elsam.service;

import static no.nav.registre.testnorge.elsam.utils.TssUtil.buildLegeFromTssResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import no.nav.registre.elsam.domain.Adresse;
import no.nav.registre.elsam.domain.Arbeidsgiver;
import no.nav.registre.elsam.domain.Detaljer;
import no.nav.registre.elsam.domain.ElsamSaveInHodejegerenRequest;
import no.nav.registre.elsam.domain.Ident;
import no.nav.registre.elsam.domain.IdentMedAdresseinfo;
import no.nav.registre.elsam.domain.IdentMedData;
import no.nav.registre.elsam.domain.Lege;
import no.nav.registre.elsam.domain.Legekontor;
import no.nav.registre.elsam.domain.Organisasjon;
import no.nav.registre.elsam.domain.SykmeldingPeriode;
import no.nav.registre.elsam.domain.SykmeldingRequest;
import no.nav.registre.elsam.domain.SykmeldingsType;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.PersondataResponse;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.elsam.consumer.rs.AaregstubConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.ElsamSyntConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.EregConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.TSSConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.response.aareg.Arbeidsforhold;
import no.nav.registre.testnorge.elsam.consumer.rs.response.ereg.EregResponse;
import no.nav.registre.testnorge.elsam.consumer.rs.response.ereg.Forretningsadresse;
import no.nav.registre.testnorge.elsam.consumer.rs.response.synt.ElsamSyntResponse;
import no.nav.registre.testnorge.elsam.consumer.rs.response.synt.SyntSykmeldingResponse;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.TssResponse;
import no.nav.registre.testnorge.elsam.domain.Sykemelding;
import no.nav.registre.testnorge.elsam.utils.DatoUtil;

@Service
@Slf4j
@RequiredArgsConstructor
@DependencyOn("testnorge-hodejegeren")
public class SyntetiseringService {

    private static final String ELSAM_NAVN = "elsam";
    private static final String MININORGE_LEGEKONTOR_ORGNUMMER = "880756053";
    private static final int MINIMUM_ALDER = 18;

    private final HodejegerenConsumer hodejegerenConsumer;

    private final ElsamSyntConsumer elsamSyntConsumer;

    private final TSSConsumer tssConsumer;

    private final EregConsumer eregConsumer;

    private final AaregstubConsumer aaregstubConsumer;

    private final Random rand;

    private final DatoUtil datoUtil;

    private final SykmeldingService sykmeldingService;

    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Value("#{${lege.miljoe.avspillergruppeId}}")
    private Map<String, Long> miljoerMedLegeavspillergruppe;

    public List<Sykemelding> syntetiserSykemeldinger(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter
    ) {
        var identerMedAdresseinfo = finnIdenterMedArbeidsforholdOgAdresseinfo(avspillergruppeId, miljoe, antallIdenter);
        var leger = hentLegerFraTssogEreg(miljoe);

        Map<String, Arbeidsforhold> identerMedArbeidsforhold = new HashMap<>(identerMedAdresseinfo.size());
        List<Map<String, String>> syntRequest = new ArrayList<>(identerMedAdresseinfo.size());
        for (IdentMedAdresseinfo ident : identerMedAdresseinfo.values()) {
            identerMedArbeidsforhold.put(ident.getIdent(), finnSenesteArbeidsforhold(aaregstubConsumer.hentArbeidsforholdTilIdent(ident.getIdent()).getArbeidsforhold()));

            Map<String, String> identMedStartdato = new HashMap<>();
            identMedStartdato.put(ident.getIdent(), datoUtil.lagTilfeldigDatoIAnsettelsesperiode(identerMedArbeidsforhold.get(ident.getIdent()).getAnsettelsesPeriode()).toString());
            syntRequest.add(identMedStartdato);
        }

        var elsamSyntResponse = elsamSyntConsumer.syntetiserSykemeldinger(syntRequest);
        List<Sykemelding> sykmeldinger = new ArrayList<>();
        List<IdentMedData> elsamData = new ArrayList<>();

        for (Map.Entry<String, ElsamSyntResponse> entry : elsamSyntResponse.entrySet()) {
            var lege = leger.get(rand.nextInt(leger.size()));
            var arbeidsforhold = identerMedArbeidsforhold.get(entry.getKey());
            var eregData = eregConsumer.hentEregdataFraOrgnummer(arbeidsforhold.getArbeidsgiver().getOrgnummer());

            if (!identerMedAdresseinfo.containsKey(entry.getKey())) {
                break;
            }
            var ident = identerMedAdresseinfo.get(entry.getKey());
            var persondata = hodejegerenConsumer.getPersondata(entry.getKey(), miljoe);
            var identMedData = IdentMedData.builder()
                    .id(entry.getKey())
                    .data(new ArrayList<>())
                    .build();

            for (SyntSykmeldingResponse syntSykmeldingResponse : entry.getValue().getSykmeldinger()) {
                SykmeldingRequest sykmeldingRequest = SykmeldingRequest.builder()
                        .ident(opprettIdent(entry.getKey(), persondata, ident, arbeidsforhold, eregData))
                        .syketilfelleStartDato(syntSykmeldingResponse.getStartPeriode())
                        .utstedelsesdato(syntSykmeldingResponse.getKontaktMedPasient())
                        .hovedDiagnose(syntSykmeldingResponse.getHovedDiagnose())
                        .biDiagnoser(syntSykmeldingResponse.getBiDiagnoser())
                        .lege(opprettLege(lege))
                        .manglendeTilretteleggingPaaArbeidsplassen(false)
                        .detaljer(opprettDetaljer(syntSykmeldingResponse))
                        .sykmeldingPerioder(Collections.singletonList(opprettSykmeldingPeriode(syntSykmeldingResponse)))
                        .senderOrganisasjon(opprettSender(lege))
                        .mottakerOrganisasjon(opprettMottaker(eregData))
                        .build();
                try {
                    sykmeldinger.add(sykmeldingService.opprettSykmelding(sykmeldingRequest));
                    identMedData.getData().add(sykmeldingRequest);
                } catch (IOException e) {
                    log.error("IOException under opprettelse av sykmelding", e);
                }
            }

            elsamData.add(identMedData);
        }

        List<String> lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(ElsamSaveInHodejegerenRequest.builder()
                .kilde(ELSAM_NAVN)
                .identMedData(elsamData)
                .build());

        for (IdentMedData identMedData : elsamData) {
            if (!lagredeIdenter.contains(identMedData.getId())) {
                log.error("Ident med id {} kunne ikke lagres i hodejegeren", identMedData.getId());
            }
        }

        return sykmeldinger;
    }

    private Ident opprettIdent(
            String fnr,
            PersondataResponse persondata,
            IdentMedAdresseinfo ident,
            Arbeidsforhold arbeidsforhold,
            EregResponse eregData
    ) {
        return Ident.builder()
                .fnr(fnr)
                .fornavn(persondata.getFornavn())
                .mellomnavn(persondata.getMellomnavn())
                .etternavn(persondata.getEtternavn())
                .adresse(Adresse.builder()
                        .gate(ident.getGateadresse())
                        .postnummer(ident.getPostnummer())
                        .by(ident.getBy())
                        .build())
                .navKontor(ident.getNavKontorNavn())
                .telefon("")
                .arbeidsgiver(Arbeidsgiver.builder()
                        .yrkesbetegnelse(arbeidsforhold.getArbeidsavtale().getYrke())
                        .stillingsprosent(arbeidsforhold.getArbeidsavtale().getStillingsprosent())
                        .navn(eregData.getNavn().hentFulltNavn())
                        .build())
                .build();
    }

    private Lege opprettLege(
            Lege lege
    ) {
        return Lege.builder()
                .fnr(lege.getFnr())
                .hprId(lege.getHprId())
                .herId(lege.getHerId())
                .fornavn(lege.getFornavn())
                .mellomnavn(lege.getMellomnavn())
                .etternavn(lege.getEtternavn())
                .adresse(opprettAdresseLege(lege))
                .telefon(lege.getTlf())
                .build();
    }

    private Adresse opprettAdresseLege(
            Lege lege
    ) {
        return Adresse.builder()
                .gate(lege.getLegekontor().getAdresse().getGate())
                .postnummer(lege.getLegekontor().getAdresse().getPostnummer())
                .by(lege.getLegekontor().getAdresse().getBy())
                .build();
    }

    private Adresse opprettAdresseEreg(
            EregResponse eregData
    ) {
        return Adresse.builder()
                .gate(eregData.getOrganisasjonDetaljer().getForretningsadresser().get(0).hentFullAdresse())
                .postnummer(eregData.getOrganisasjonDetaljer().getForretningsadresser().get(0).getPostnummer())
                .by(eregData.getOrganisasjonDetaljer().getForretningsadresser().get(0).getPoststed())
                .build();
    }

    private Detaljer opprettDetaljer(
            SyntSykmeldingResponse syntSykmeldingResponse
    ) {
        return Detaljer.builder()
                .tiltakArbeidsplass("")
                .tiltakNav("")
                .arbeidsforEtterEndtPeriode(syntSykmeldingResponse.getArbeidsforEtterEndtPeriode())
                .build();
    }

    private SykmeldingPeriode opprettSykmeldingPeriode(
            SyntSykmeldingResponse syntSykmeldingResponse
    ) {
        return SykmeldingPeriode.builder()
                .fom(syntSykmeldingResponse.getStartPeriode())
                .tom(syntSykmeldingResponse.getSluttPeriode())
                .type(SykmeldingsType.findSykmeldingsType(syntSykmeldingResponse.getSykmeldingsprosent()))
                .build();
    }

    private Organisasjon opprettSender(
            Lege lege
    ) {
        return Organisasjon.builder()
                .navn(lege.getLegekontor().getNavn())
                .herId(lege.getLegekontor().getHerId())
                .orgNr(MININORGE_LEGEKONTOR_ORGNUMMER)
                .adresse(opprettAdresseLege(lege))
                .build();
    }

    private Organisasjon opprettMottaker(
            EregResponse eregData
    ) {
        return Organisasjon.builder()
                .navn(eregData.getNavn().hentFulltNavn())
                .herId("")
                .orgNr(eregData.getOrganisasjonsnummer())
                .adresse(opprettAdresseEreg(eregData))
                .build();
    }

    private List<Lege> hentLegerFraTssogEreg(
            String miljoe
    ) {
        var response = tssConsumer.hentLeger(miljoerMedLegeavspillergruppe.get(miljoe), miljoe);

        List<Lege> utvalgteLeger = new ArrayList<>();

        if (response.getBody() != null) {
            Legekontor legekontor = opprettLegekontorFraEregdata(eregConsumer.hentEregdataFraOrgnummer(MININORGE_LEGEKONTOR_ORGNUMMER));

            for (TssResponse tssResponse : response.getBody().values()) {
                Lege lege = buildLegeFromTssResponse(tssResponse);
                lege.setLegekontor(legekontor);
                utvalgteLeger.add(lege);
            }
        }

        return utvalgteLeger;
    }

    private Map<String, IdentMedAdresseinfo> finnIdenterMedArbeidsforholdOgAdresseinfo(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter
    ) {
        Set<String> levendeIdenterOverAlder = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER));
        Set<String> identerIAaregstub = new HashSet<>(aaregstubConsumer.hentAlleIdenterIStub());

        identerIAaregstub.retainAll(levendeIdenterOverAlder);

        var identerIAaregstubListe = new ArrayList<>(identerIAaregstub);
        Collections.shuffle(identerIAaregstubListe);

        if (identerIAaregstubListe.size() < antallIdenter) {
            antallIdenter = identerIAaregstub.size();
            log.info("Ikke nok identer med arbeidsforhold i avspillergruppe. Oppretter {} sykmeldinger.", antallIdenter);
        }
        List<String> utvalgteIdenter = identerIAaregstubListe.subList(0, antallIdenter);

        var adresseInfo = hodejegerenConsumer.getAdresse(miljoe, new ArrayList<>(utvalgteIdenter));

        Map<String, IdentMedAdresseinfo> identerMedNavKontor = new HashMap<>(adresseInfo.size());
        var objectMapper = new ObjectMapper();

        for (Map.Entry<String, JsonNode> entry : adresseInfo.entrySet()) {
            IdentMedAdresseinfo identMedAdresseinfo = objectMapper.convertValue(entry.getValue().findValue("NAVenhetDetalj"), IdentMedAdresseinfo.class);
            identMedAdresseinfo.setGateadresse(entry.getValue().findValue("adresse1").asText());
            identMedAdresseinfo.setPostnummer(entry.getValue().findValue("postnr").asText());
            identMedAdresseinfo.setBy(entry.getValue().findValue("poststed").asText());
            identMedAdresseinfo.setLand(entry.getValue().findValue("land").asText());
            identMedAdresseinfo.setIdent(entry.getKey());
            identerMedNavKontor.put(entry.getKey(), identMedAdresseinfo);
        }

        return identerMedNavKontor;
    }

    private Arbeidsforhold finnSenesteArbeidsforhold(
            List<Arbeidsforhold> alleArbeidsforhold
    ) {
        Arbeidsforhold senesteArbeidsforhold = alleArbeidsforhold.get(0);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][ ]HH:mm[:ss]");

        for (Arbeidsforhold arbeidsforhold : alleArbeidsforhold) {
            LocalDateTime ansettelsesPeriodeStart = LocalDateTime.parse(arbeidsforhold.getAnsettelsesPeriode().getFom(), dateTimeFormatter);

            LocalDateTime senesteArbeidsforholdPeriodeStart = LocalDateTime.parse(senesteArbeidsforhold.getAnsettelsesPeriode().getFom(), dateTimeFormatter);

            if (ansettelsesPeriodeStart.isAfter(senesteArbeidsforholdPeriodeStart)) {
                senesteArbeidsforhold = arbeidsforhold;
            }
        }

        return senesteArbeidsforhold;
    }

    private Legekontor opprettLegekontorFraEregdata(
            EregResponse eregData
    ) {
        Forretningsadresse forretningsadresse = eregData.getOrganisasjonDetaljer().getForretningsadresser().get(0);

        return Legekontor.builder()
                .navn(eregData.getNavn().hentFulltNavn())
                .eregId(eregData.getOrganisasjonsnummer())
                .adresse(Adresse.builder()
                        .gate(forretningsadresse.getAdresselinje1())
                        .postnummer(forretningsadresse.getPostnummer())
                        .by(forretningsadresse.getPoststed())
                        .build())
                .build();
    }
}
