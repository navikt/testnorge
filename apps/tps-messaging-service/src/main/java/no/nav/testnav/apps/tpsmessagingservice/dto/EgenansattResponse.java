package no.nav.testnav.apps.tpsmessagingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.xml.bind.annotation.XmlRootElement;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement(name = "sfePersonData")
public class EgenansattResponse extends EndringsmeldingResponse {

    private EgenansattRequest.SfeAjourforing sfeAjourforing;
}
