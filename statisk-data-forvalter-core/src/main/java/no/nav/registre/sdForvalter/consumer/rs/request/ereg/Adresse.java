package no.nav.registre.sdForvalter.consumer.rs.request.ereg;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@NoArgsConstructor
@Getter
@Setter
public class Adresse {

    private List<String> adresser;
    private String postnr;
    private String kommunenr;
    private String landkode;
    private String poststed;


}
