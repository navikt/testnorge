package no.nav.testnav.apps.tpsmessagingservice.dto;

import lombok.Data;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
public class EndringsmeldingResponse {

    private SfeTilbakemelding sfeTilbakemelding;
}
