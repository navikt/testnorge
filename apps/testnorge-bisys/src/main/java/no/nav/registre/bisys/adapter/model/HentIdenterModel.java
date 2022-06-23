package no.nav.registre.bisys.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentIdenterModel {
    List<IdenterModel> identer;
}
