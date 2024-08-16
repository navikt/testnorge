package no.nav.testnav.levendearbeidsforholdscheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    private Integer interval;
}
