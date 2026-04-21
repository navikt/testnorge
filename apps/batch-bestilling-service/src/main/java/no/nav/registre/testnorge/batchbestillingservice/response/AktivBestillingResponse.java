package no.nav.registre.testnorge.batchbestillingservice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AktivBestillingResponse {

    private Long id;
    private Integer antallIdenter;
    private Integer antallLevert;
    private boolean ferdig;
    private long gruppeId;
    private boolean stoppet;
}

