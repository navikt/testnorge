package no.nav.dolly.bestilling.bregstub.domain;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrregRequestWrapper {

    private List<OrganisasjonTo> organisasjonTo;
    private RolleoversiktTo rolleoversiktTo;

    public List<OrganisasjonTo> getOrganisasjonTo() {
        if (isNull(organisasjonTo)) {
            organisasjonTo = new ArrayList<>();
        }
        return organisasjonTo;
    }
}
