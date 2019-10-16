package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsPerson;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyUpdateRequest extends RsDollyBestilling {

    private RsPerson tpsfPerson;
}