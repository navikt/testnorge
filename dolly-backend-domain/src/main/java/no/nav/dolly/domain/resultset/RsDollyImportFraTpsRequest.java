package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyImportFraTpsRequest {

    private String environment;
    private List<String> identer;
}
