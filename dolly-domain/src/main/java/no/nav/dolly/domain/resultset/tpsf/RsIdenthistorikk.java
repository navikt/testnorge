package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsIdenthistorikk {

    private String identtype;
    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private String kjonn;
}