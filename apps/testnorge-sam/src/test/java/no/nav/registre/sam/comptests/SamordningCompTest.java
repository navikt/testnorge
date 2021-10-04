package no.nav.registre.sam.comptests;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static no.nav.registre.sam.service.SyntetiseringService.ENDRET_OPPRETTET_AV;
import static no.nav.registre.sam.testutils.DateUtils.formatDate;
import static no.nav.registre.sam.testutils.DateUtils.formatTimestamp;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;
import no.nav.registre.sam.database.TPersonRepository;
import no.nav.registre.sam.database.TSamHendelseRepository;
import no.nav.registre.sam.database.TSamMeldingRepository;
import no.nav.registre.sam.database.TSamVedtakRepository;
import no.nav.registre.sam.service.SyntetiseringService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SamordningCompTest {

    @Autowired
    private TPersonRepository tPersonRepository;

    @Autowired
    private TSamHendelseRepository tSamHendelseRepository;

    @Autowired
    private TSamMeldingRepository tSamMeldingRepository;

    @Autowired
    private TSamVedtakRepository tSamVedtakRepository;

    @Autowired
    private SyntetiseringService syntetiseringService;

    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private List<String> identer;
    private List<SyntetisertSamordningsmelding> syntetiserteSamordningsmeldinger;

    @Before
    public void setUp() throws IOException {
        identer = new ArrayList<>();
        identer.add(fnr1);
        identer.add(fnr2);

        syntetiserteSamordningsmeldinger = new ArrayList<>();

        var samMeldingerFraJson = new ObjectMapper().treeToValue(new ObjectMapper().readTree(Resources.getResource("samordningsmelding.json")), List.class);
        for (var melding : samMeldingerFraJson) {
            syntetiserteSamordningsmeldinger.add(new ObjectMapper().convertValue(melding, SyntetisertSamordningsmelding.class));
        }
    }

    @Test
    public void shouldLagreSyntetiserteSamordningsmeldinger() throws ParseException {
        stubHodejegerenConsumer();

        syntetiseringService.lagreSyntetiserteMeldinger(syntetiserteSamordningsmeldinger, identer);

        var person1 = tPersonRepository.findByFnrFK(fnr1);
        var person2 = tPersonRepository.findByFnrFK(fnr2);
        assertThat(person1.getFnrFK(), equalTo(fnr1));
        assertThat(person1.getEndretAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(person1.getOpprettetAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(person2.getFnrFK(), equalTo(fnr2));

        var tSamHendelse = tSamHendelseRepository.findById(1L).orElse(null);
        assertThat(tSamHendelse, notNullValue());
        assertThat(tSamHendelse.getKTpArt(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKTPArt()));
        assertThat(tSamHendelse.getKSamHendelseT(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKSamHendelseT()));
        assertThat(tSamHendelse.getKKanalT(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKKanalT()));
        assertThat(tSamHendelse.getTssEksternIdFk(), equalTo(syntetiserteSamordningsmeldinger.get(0).getTssEksternIdFk()));
        assertThat(tSamHendelse.getOpprettetAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(tSamHendelse.getEndretAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(tSamHendelse.getDatoEndret().toString(), equalTo(formatTimestamp(syntetiserteSamordningsmeldinger.get(0).getDatoEndret()).toString()));
        assertThat(tSamHendelse.getDatoOpprettet().toString(), equalTo(formatTimestamp(syntetiserteSamordningsmeldinger.get(0).getDatoOpprettet()).toString()));

        var tSamMelding = tSamMeldingRepository.findById(1L).orElse(null);
        assertThat(tSamMelding, notNullValue());
        assertThat(tSamMelding.getEndretAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(tSamMelding.getOpprettetAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(tSamMelding.getKKanalT(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKKanalT()));
        assertThat(tSamMelding.getKMeldingStatus(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKMeldingStatus()));
        assertThat(tSamMelding.getRefusjonskrav(), equalTo(syntetiserteSamordningsmeldinger.get(0).getRefusjonskrav()));
        assertThat(tSamMelding.getTssEksternIdFk(), equalTo(syntetiserteSamordningsmeldinger.get(0).getTssEksternIdFk()));
        assertThat(tSamMelding.getDatoEndret().toString(), equalTo(formatTimestamp(syntetiserteSamordningsmeldinger.get(0).getDatoEndret()).toString()));
        assertThat(tSamMelding.getDatoOpprettet().toString(), equalTo(formatTimestamp(syntetiserteSamordningsmeldinger.get(0).getDatoOpprettet()).toString()));
        assertThat(tSamMelding.getDatoPurret().toString(), equalTo(formatDate(syntetiserteSamordningsmeldinger.get(0).getDatoPurret()).toString()));
        assertThat(tSamMelding.getDatoSendt().toString(), equalTo(formatDate(syntetiserteSamordningsmeldinger.get(0).getDatoSendt()).toString()));
        assertThat(tSamMelding.getDatoSvart().toString(), equalTo(formatDate(syntetiserteSamordningsmeldinger.get(0).getDatoSvart()).toString()));

        var tSamVedtak = tSamVedtakRepository.findById(1L).orElse(null);
        assertThat(tSamVedtak, notNullValue());
        assertThat(tSamVedtak.getEndretAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(tSamVedtak.getOpprettetAv(), equalTo(ENDRET_OPPRETTET_AV));
        assertThat(tSamVedtak.getEtterbetaling(), equalTo(syntetiserteSamordningsmeldinger.get(0).getEtterbetaling()));
        assertThat(tSamVedtak.getKArt(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKArt()));
        assertThat(tSamVedtak.getKFagomraade(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKFagomraade()));
        assertThat(tSamVedtak.getKVedtakStatus(), equalTo(syntetiserteSamordningsmeldinger.get(0).getKVedtakStatus()));
        assertThat(tSamVedtak.getPurring(), equalTo(syntetiserteSamordningsmeldinger.get(0).getPurring()));
        assertThat(tSamVedtak.getDatoEndret().toString(), equalTo(formatTimestamp(syntetiserteSamordningsmeldinger.get(0).getDatoEndret()).toString()));
        assertThat(tSamVedtak.getDatoOpprettet().toString(), equalTo(formatTimestamp(syntetiserteSamordningsmeldinger.get(0).getDatoOpprettet()).toString()));
        assertThat(tSamVedtak.getDatoFom().toString(), equalTo(formatDate(syntetiserteSamordningsmeldinger.get(0).getDatoFom()).toString()));
        assertThat(tSamVedtak.getDatoTom().toString(), equalTo(formatDate(syntetiserteSamordningsmeldinger.get(0).getDatoTom()).toString()));
    }

    private void stubHodejegerenConsumer() {
        stubFor(post("/hodejegeren/api/v1/historikk")
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr1 + "\", \"" + fnr2 + "\"]")));
    }
}
