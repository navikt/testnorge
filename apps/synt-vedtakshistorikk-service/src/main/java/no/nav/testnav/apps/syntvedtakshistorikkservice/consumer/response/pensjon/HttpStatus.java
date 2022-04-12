package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpStatus {

    private String reasonPhrase;
    private Integer status;
}
