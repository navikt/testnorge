package no.nav.registre.tss.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Samhandler {

    private String ident;
    private String navn;
    private Integer alder;
    private TssType type;

    public Samhandler(Person person, TssType type) {
        ident = person.getFnr();
        navn = person.getNavn();
        alder = person.getAlder();
        this.type = type;
    }

    private Samhandler(String ident, String navn, Integer alder, TssType type) {
        this.ident = ident;
        this.navn = navn;
        this.alder = alder;
        this.type = type;
    }
}
