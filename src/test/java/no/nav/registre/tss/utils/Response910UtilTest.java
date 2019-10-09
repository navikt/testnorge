package no.nav.registre.tss.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

import no.nav.registre.tss.consumer.rs.response.Response910;

public class Response910UtilTest {

    @Test
    public void shouldParseStringToObject() throws IOException {
        URL resource = Resources.getResource("response_910_rutine.json");
        new ObjectMapper().readValue(resource, Response910.class);

        Response910 response = Response910Util
                .parseResponse("COB                00                                                                                                                                            "
                        + "                                                                      910           24107626502FNR                                                     "
                        + "                                                                                                                                                       "
                        + "                                                                    11024107626502FNR LE  Lege                          20190919        ETAT VRIEN     "
                        + "                         NO                                      GYLDGyldig                     SYNTORK     20190919121811124107626502FNR Norsk fødselsnummer"
                        + "                     20190919        JSYNTORK     201909191218                                                                                         "
                        + "               111587977648  HPR HPR-nummer                              20190919        JSYNTORK     201909191218                                     "
                        + "                                                                   12501                                                                               "
                        + "     20190919        J80900100006           SYNTORK     201909191218");

        check110Routine(response);
        check111Routine(response);
        check125Routine(response);
    }

    private void check110Routine(Response910 response) {
        assertThat(response.getResponse110().get(0).getIdKode(), equalTo("110"));
        assertThat(response.getResponse110().get(0).getIdOff(), equalTo("24107626502"));
        assertThat(response.getResponse110().get(0).getKodeIdenttype(), equalTo("FNR"));
        assertThat(response.getResponse110().get(0).getKodeSamhType(), equalTo("LE"));
        assertThat(response.getResponse110().get(0).getBeskrSamhType(), equalTo("Lege"));
        assertThat(response.getResponse110().get(0).getDatoSamhType(), equalTo("20190919"));
        assertThat(response.getResponse110().get(0).getNavn(), equalTo("ETAT VRIEN"));
        assertThat(response.getResponse110().get(0).getKodeSpraak(), equalTo("NO"));
        assertThat(response.getResponse110().get(0).getKodeStatus(), equalTo("GYLD"));
        assertThat(response.getResponse110().get(0).getBeskrStatus(), equalTo("Gyldig"));
        assertThat(response.getResponse110().get(0).getKilde(), equalTo("SYNT"));
        assertThat(response.getResponse110().get(0).getBrukerId(), equalTo("ORK"));
        assertThat(response.getResponse110().get(0).getTidReg(), equalTo("201909191218"));
    }

    private void check111Routine(Response910 response) {
        assertThat(response.getResponse111().get(0).getIdKode(), equalTo("111"));
        assertThat(response.getResponse111().get(0).getIdAlternativ(), equalTo("24107626502"));
        assertThat(response.getResponse111().get(0).getKodeAltIdenttype(), equalTo("FNR"));
        assertThat(response.getResponse111().get(0).getBeskrAltIdenttype(), equalTo("Norsk fødselsnummer"));
        assertThat(response.getResponse111().get(0).getDatoIdentFom(), equalTo("20190919"));
        assertThat(response.getResponse111().get(0).getGyldigIdent(), equalTo("J"));
        assertThat(response.getResponse111().get(0).getKilde(), equalTo("SYNT"));
        assertThat(response.getResponse111().get(0).getBrukerid(), equalTo("ORK"));
        assertThat(response.getResponse111().get(0).getTidReg(), equalTo("201909191218"));

        assertThat(response.getResponse111().get(1).getIdKode(), equalTo("111"));
        assertThat(response.getResponse111().get(1).getIdAlternativ(), equalTo("587977648"));
        assertThat(response.getResponse111().get(1).getKodeAltIdenttype(), equalTo("HPR"));
        assertThat(response.getResponse111().get(1).getBeskrAltIdenttype(), equalTo("HPR-nummer"));
        assertThat(response.getResponse111().get(1).getDatoIdentFom(), equalTo("20190919"));
        assertThat(response.getResponse111().get(1).getGyldigIdent(), equalTo("J"));
        assertThat(response.getResponse111().get(1).getKilde(), equalTo("SYNT"));
        assertThat(response.getResponse111().get(1).getBrukerid(), equalTo("ORK"));
        assertThat(response.getResponse111().get(1).getTidReg(), equalTo("201909191218"));
    }

    private void check125Routine(Response910 response) {
        assertThat(response.getResponse125().get(0).getIdKode(), equalTo("125"));
        assertThat(response.getResponse125().get(0).getAvdelingsnr(), equalTo("01"));
        assertThat(response.getResponse125().get(0).getDatoAvdelingFrom(), equalTo("20190919"));
        assertThat(response.getResponse125().get(0).getGyldigAvdeling(), equalTo("J"));
        assertThat(response.getResponse125().get(0).getIdTSSEkstern(), equalTo("80900100006"));
        assertThat(response.getResponse125().get(0).getKilde(), equalTo("SYNT"));
        assertThat(response.getResponse125().get(0).getBrukerid(), equalTo("ORK"));
        assertThat(response.getResponse125().get(0).getTidReg(), equalTo("201909191218"));
    }
}