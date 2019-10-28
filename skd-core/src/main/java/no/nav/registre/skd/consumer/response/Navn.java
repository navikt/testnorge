package no.nav.registre.skd.consumer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Navn {
    private String fornavn;
    private String etternavn;
}
