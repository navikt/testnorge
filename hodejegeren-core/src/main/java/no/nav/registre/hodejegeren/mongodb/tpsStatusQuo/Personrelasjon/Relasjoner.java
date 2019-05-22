package no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Personrelasjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relasjoner {

    private List<Relasjon> relasjon;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Relasjon {

        private String kortnavn;
        private String datoDo;
        private String typeRelBeskr;
        private String mellomnavn;
        private String etternavn;
        private Integer adresseStatus;
        private String adrStatusBeskr;
        private String spesregType;
        private String fornavn;
        private String fnrRelasjon;
        private String typeRelasjon;
    }
}
