package no.nav.testnav.apps.bankkontoservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@XmlRootElement(name = "sfePersonData")
public class BankkontoNorskResponse extends EndringsmeldingResponse {

    private BankkontoNorskRequest.SfeAjourforing sfeAjourforing;
}
