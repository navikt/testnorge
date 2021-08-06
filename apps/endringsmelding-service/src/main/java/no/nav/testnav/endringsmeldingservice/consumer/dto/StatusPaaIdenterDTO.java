package no.nav.testnav.endringsmeldingservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StatusPaaIdenterDTO {
    List<IdentDTO> statusPaaIdenter;
}
