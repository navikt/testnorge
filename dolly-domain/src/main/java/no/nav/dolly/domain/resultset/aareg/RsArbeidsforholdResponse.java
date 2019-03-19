package no.nav.dolly.domain.resultset.aareg;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsforholdResponse {

    private List<Object> arbeidsforholdList;
}
