package no.nav.registre.hodejegeren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.Resources;

import no.nav.registre.testnorge.domain.dto.namespacetps.TpsPersonDokumentType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import no.nav.registre.hodejegeren.mongodb.Data;
import no.nav.registre.hodejegeren.mongodb.Kilde;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikkRepository;
import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;

@RunWith(MockitoJUnitRunner.class)
public class HistorikkServiceTest {

    @Mock
    private SyntHistorikkRepository syntHistorikkRepository;

    @InjectMocks
    private HistorikkService historikkService;

    private String id1 = "01010101010";
    private String id2 = "02020202020";
    private SyntHistorikk syntHistorikk1;
    private SyntHistorikk syntHistorikk2;
    private List<SyntHistorikk> lagretHistorikk;
    private HistorikkRequest historikkRequest1;
    private HistorikkRequest historikkRequest2;

    @Before
    public void setUp() throws IOException {
        var objectMapper = new ObjectMapper();
        syntHistorikk1 = objectMapper.treeToValue(objectMapper.readTree(Resources.getResource("historikk/historikk1.json")), SyntHistorikk.class);
        syntHistorikk2 = objectMapper.treeToValue(objectMapper.readTree(Resources.getResource("historikk/historikk2.json")), SyntHistorikk.class);

        lagretHistorikk = new ArrayList<>(Arrays.asList(syntHistorikk1, syntHistorikk2));

        when(syntHistorikkRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(lagretHistorikk));
        when(syntHistorikkRepository.findById(id1)).thenReturn(Optional.ofNullable(lagretHistorikk.get(0)));
        when(syntHistorikkRepository.findById(id2)).thenReturn(Optional.ofNullable(lagretHistorikk.get(1)));

        when(syntHistorikkRepository.findAllIdsByKildenavn("aareg")).thenReturn(lagretHistorikk);

        when(syntHistorikkRepository.save(syntHistorikk1)).thenReturn(syntHistorikk1);
        when(syntHistorikkRepository.save(syntHistorikk2)).thenReturn(syntHistorikk2);

        historikkRequest1 = objectMapper.treeToValue(objectMapper.readTree(Resources.getResource("historikk/historikk-request1.json")), HistorikkRequest.class);
        historikkRequest2 = objectMapper.treeToValue(objectMapper.readTree(Resources.getResource("historikk/historikk-request2.json")), HistorikkRequest.class);
    }

    @Test
    public void shouldHenteAllHistorikk() {
        var historikk = historikkService.hentAllHistorikk(0, 10);

        assertThat(historikk.get(0).getId(), equalTo(id1));
        assertThat(historikk.get(1).getId(), equalTo(id2));
    }

    @Test
    public void shouldHenteHistorikkMedId() {
        var historikk1 = historikkService.hentHistorikkMedId(id1);
        var historikk2 = historikkService.hentHistorikkMedId(id2);

        assertThat(historikk1.getId(), equalTo(id1));
        assertThat(historikk2.getId(), equalTo(id2));
    }

    @Test
    public void shouldHenteHistorikkMedKilde() {
        when(syntHistorikkRepository.findAllByIdIn(anyList(), any(Pageable.class))).thenReturn(new PageImpl<>(lagretHistorikk));
        var historikkMedKilde = historikkService.hentHistorikkMedKilder(Collections.singletonList("aareg"), 0, 10);

        var historikkIds = new ArrayList<>(Arrays.asList(historikkMedKilde.get(0).getId(), historikkMedKilde.get(1).getId()));
        assertThat(historikkIds, containsInAnyOrder(id1, id2));
    }

    @Test
    public void shouldHenteIdsMedKilde() {
        var idsMedKilde = historikkService.hentIdsMedKilder(Collections.singletonList("aareg"));

        assertThat(idsMedKilde, containsInAnyOrder(id1, id2));
    }

    @Test
    public void shouldOppretteHistorikk() {
        var historikk1 = historikkService.opprettHistorikk(syntHistorikk1);
        var historikk2 = historikkService.opprettHistorikk(syntHistorikk2);

        assertThat(historikk1.getId(), equalTo(syntHistorikk1.getId()));
        assertThat(historikk1.getKilder().get(0).getNavn(), is(equalTo("aareg")));
        assertThat(historikk1.getKilder().get(0).getData().get(0).getDatoOpprettet(), is(notNullValue()));
        assertThat(historikk1.getKilder().get(0).getData().get(0).getDatoEndret(), is(notNullValue()));
        assertThat(historikk2.getId(), equalTo(syntHistorikk2.getId()));
        assertThat(historikk2.getKilder().get(0).getNavn(), is(equalTo("aareg")));
        assertThat(historikk2.getKilder().get(0).getData().get(0).getDatoOpprettet(), is(notNullValue()));
        assertThat(historikk2.getKilder().get(0).getData().get(0).getDatoEndret(), is(notNullValue()));
    }

    @Test
    public void shouldLeggeTilHistorikkPaaIdent() {
        var identerLagtTil = historikkService.leggTilHistorikkPaaIdent(historikkRequest1);
        assertThat(identerLagtTil, contains(id1));

        identerLagtTil = historikkService.leggTilHistorikkPaaIdent(historikkRequest2);
        assertThat(identerLagtTil, contains(id2));

        verify(syntHistorikkRepository, times(2)).save(any());
    }

    @Test
    public void shouldOppdatereTpsPersonDokument() throws IOException {
        var datoOpprettet = LocalDateTime.of(2000, 1, 1, 1, 1);
        var skdFnr = "19040979827";
        List<Data> data = new ArrayList<>();
        data.add(Data.builder().datoOpprettet(datoOpprettet).datoEndret(datoOpprettet).build());
        List<Kilde> kilder = new ArrayList<>();
        kilder.add(Kilde.builder()
                .navn("skd")
                .data(data)
                .build());
        var skdHistorikk = SyntHistorikk.builder()
                .id(skdFnr)
                .kilder(kilder)
                .build();

        when(syntHistorikkRepository.findById(skdFnr)).thenReturn(Optional.ofNullable(skdHistorikk));
        when(syntHistorikkRepository.save(any())).thenReturn(skdHistorikk);

        var resource = Resources.getResource("historikk/TpsPersonDokumentType.xml");
        var xmlMapper = new XmlMapper();
        var tpsPersonDokumentType = xmlMapper.readValue(resource, TpsPersonDokumentType.class);

        var identerOppdatert = historikkService.oppdaterTpsPersonDokument(skdFnr, tpsPersonDokumentType);

        assertThat(identerOppdatert, contains(skdFnr));

        verify(syntHistorikkRepository).findById(skdFnr);
        verify(syntHistorikkRepository).save(any());
    }

    @Test
    public void shouldOppretteNyttSkdDokument() throws IOException {
        var skdFnr = "03030303030";
        List<Kilde> kilder = new ArrayList<>();
        kilder.add(Kilde.builder()
                .navn("skd")
                .build());
        var skdHistorikk = SyntHistorikk.builder()
                .id(skdFnr)
                .kilder(kilder)
                .build();

        when(syntHistorikkRepository.findById(skdFnr)).thenReturn(Optional.empty());
        when(syntHistorikkRepository.save(any())).thenReturn(skdHistorikk);

        var resource = Resources.getResource("historikk/TpsPersonDokumentType.xml");
        var xmlMapper = new XmlMapper();
        var tpsPersonDokumentType = xmlMapper.readValue(resource, TpsPersonDokumentType.class);

        var identerLagtTil = historikkService.oppdaterTpsPersonDokument(skdFnr, tpsPersonDokumentType);

        assertThat(identerLagtTil, contains(skdFnr));

        verify(syntHistorikkRepository, times(2)).findById(skdFnr);
        verify(syntHistorikkRepository).save(any());
    }

    @Test
    public void shouldSletteHistorikk() {
        var response = historikkService.slettHistorikk(id1);
        verify(syntHistorikkRepository).deleteById(id1);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().toString(), containsString("Historikk tilhørende id '" + id1 + "' ble slettet"));
    }

    @Test
    public void shouldReturnErrorOnUnsuccessfulDeleteOfHistorikk() {
        var response = historikkService.slettHistorikk("03030303030");
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody().toString(), containsString("Fant ingen historikk tilhørende id '03030303030'"));
    }

    @Test
    public void shouldSletteKilde() {
        var response = historikkService.slettKilde(id1, "aareg");
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().toString(), containsString("Kilde 'aareg' tilhørende id '" + id1 + "' ble slettet"));
    }

    @Test
    public void shouldReturnErrorOnUnsuccessfulDeleteOfKilde() {
        var response = historikkService.slettKilde(id1, "bisys");
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody().toString(), containsString("Fant ingen identMedData med navn 'bisys' tilhørende id '" + id1 + "'"));
    }
}