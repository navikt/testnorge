package no.nav.registre.bisys.service.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barn {

    private String fnr;
    private String morFnr;
    private String farFnr;
}
