package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlRettIdentitetErUkjent extends RsPdlRettIdentitet{

    private Boolean rettIdentitetErUkjent;
}
