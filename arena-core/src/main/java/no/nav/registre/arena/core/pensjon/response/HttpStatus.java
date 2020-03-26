package no.nav.registre.arena.core.pensjon.response;

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
public class HttpStatus {

    private String reasonPhrase;
    private Integer status;
}
