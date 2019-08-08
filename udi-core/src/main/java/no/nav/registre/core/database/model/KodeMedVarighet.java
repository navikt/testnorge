package no.nav.registre.core.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.common.v2.Kodeverk;

import javax.persistence.Embeddable;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Embeddable
public class KodeMedVarighet {

    Kodeverk varighetKode;

    Integer varighet;

    Periode periode;
}
