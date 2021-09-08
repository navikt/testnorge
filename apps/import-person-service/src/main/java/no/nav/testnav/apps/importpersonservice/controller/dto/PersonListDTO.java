package no.nav.testnav.apps.importpersonservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonListDTO {
    List<PersonDTO> personList;
}
