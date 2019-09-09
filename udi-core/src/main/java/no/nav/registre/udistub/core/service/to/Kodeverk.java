package no.nav.registre.udistub.core.service.to;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Kodeverk implements Comparable<no.udi.common.v2.Kodeverk> {

    private String kode;
    private String visningsnavn;
    private String type;

    public no.udi.common.v2.Kodeverk udiKodeverk() {
        no.udi.common.v2.Kodeverk kodeverk = new no.udi.common.v2.Kodeverk();
        kodeverk.setKode(this.kode);
        kodeverk.setType(this.type);
        kodeverk.setVisningsnavn(this.visningsnavn);
        return kodeverk;
    }

    @Override
    public int compareTo(no.udi.common.v2.Kodeverk o) {
        return 0;
    }
}
