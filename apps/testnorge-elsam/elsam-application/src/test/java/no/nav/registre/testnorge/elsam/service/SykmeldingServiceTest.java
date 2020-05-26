package no.nav.registre.testnorge.elsam.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import no.nav.helse.eiFellesformat.XMLEIFellesformat;
import no.nav.helse.msgHead.XMLMsgHead;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;

import no.nav.registre.elsam.domain.SykmeldingRequest;
import no.nav.registre.testnorge.elsam.utils.JAXB;

@RunWith(MockitoJUnitRunner.class)
public class SykmeldingServiceTest {

    @InjectMocks
    private SykmeldingService sykmeldingService;

    private SykmeldingRequest sykmeldingRequest;

    @Before
    public void setUp() throws IOException {
        URL resource = Resources.getResource("testdata/sykmelding_request.json");
        sykmeldingRequest = new ObjectMapper().readValue(resource, SykmeldingRequest.class);
    }

    @Test
    public void shouldOppretteSykmelding() throws IOException {
        String response = sykmeldingService.opprettSykmelding(sykmeldingRequest).toXml();

        XMLMsgHead responseXml = getXmlFromString(response);

        assertThat(responseXml.getMsgInfo().getSender().getOrganisation().getOrganisationName(), equalTo("TESTSENDER"));
        assertThat(responseXml.getMsgInfo().getReceiver().getOrganisation().getOrganisationName(), equalTo("TESTMOTTAKER"));
        assertThat(responseXml.getMsgInfo().getPatient().getGivenName(), equalTo("GUL"));
        assertThat(responseXml.getMsgInfo().getPatient().getFamilyName(), equalTo("BOLLE"));
        assertThat(responseXml.getMsgInfo().getPatient().getIdent().get(0).getId(), equalTo("01010101010"));

    }

    private XMLMsgHead getXmlFromString(String xml) {
        XMLEIFellesformat fellesformat = JAXB.unmarshalFellesformat(xml);

        var iterator = fellesformat.getAny().iterator();

        XMLMsgHead xmlMsgHead = null;
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof XMLMsgHead) {
                xmlMsgHead = (XMLMsgHead) next;
                break;
            }
        }

        if (xmlMsgHead == null) {
            throw new ClassCastException("null cannot be cast to non-null type no.kith.xmlstds.msghead._2006_05_24.XMLMsgHead");
        }

        return xmlMsgHead;
    }
}