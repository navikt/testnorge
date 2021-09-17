package no.nav.testnav.apps.oversiktfrontend.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AccessDTO {
    String name;
    List<ApplicationDTO> accessTo;
    List<ApplicationDTO> accessFrom;
}