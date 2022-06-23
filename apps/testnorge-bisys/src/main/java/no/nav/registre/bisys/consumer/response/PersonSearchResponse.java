package no.nav.registre.bisys.consumer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonSearchResponse {
    private int numberOfItems;
    private List<PersonDTO> items;
}