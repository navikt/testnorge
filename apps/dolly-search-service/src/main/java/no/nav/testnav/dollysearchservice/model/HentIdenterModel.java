package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentIdenterModel {
    List<IdenterModel> identer;
}
