package no.nav.registre.hodejegeren.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlettIdenterRequest {

    private List<String> identerSomSkalSlettes;
}
