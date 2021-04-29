package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.DetaljertNavn;

@Data
public class Organisasjon {
    private String name;
    private String orgnummer;
    private String enhetstype;
    private List<Organisasjon> virksomheter;

    public String getName() {
        return name;
    }

    public String getOrgnummer() {
        return orgnummer;
    }

    public String getEnhetstype() {
        return enhetstype;
    }

    public List<Organisasjon> getVirksomheter() {
        if (virksomheter == null) {
            virksomheter = new ArrayList<>();
        }
        return virksomheter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrgnummer(String orgnummer) {
        this.orgnummer = orgnummer;
    }

    public void setEnhetstype(String enhetstype) {
        this.enhetstype = enhetstype;
    }

    public void setVirksomheter(List<Organisasjon> virksomheter) {
        this.virksomheter = virksomheter;
    }

    public no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon toAvroOrganisasjon() {
        return no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon
                .newBuilder()
                .setEnhetstype(enhetstype)
                .setOrgnummer(orgnummer)
                .setUnderenheter(getVirksomheter().stream().map(Organisasjon::toAvroOrganisasjon).collect(Collectors.toList()))
                .setNavn(DetaljertNavn.newBuilder().setNavn1(name).setRedigertNavn(name).build())
                .build();
    }

}
