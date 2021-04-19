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
    private List<Organisasjon> virksomhenter;

    public String getName() {
        return name;
    }

    public String getOrgnummer() {
        return orgnummer;
    }

    public String getEnhetstype() {
        return enhetstype;
    }

    public List<Organisasjon> getVirksomhenter() {
        if (virksomhenter == null) {
            virksomhenter = new ArrayList<>();
        }
        return virksomhenter;
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

    public void setVirksomhenter(List<Organisasjon> virksomhenter) {
        this.virksomhenter = virksomhenter;
    }

    public no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon toAvroOrganiasjon() {
        return no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon
                .newBuilder()
                .setEnhetstype(enhetstype)
                .setOrgnummer(orgnummer)
                .setUnderenheter(virksomhenter.stream().map(Organisasjon::toAvroOrganiasjon).collect(Collectors.toList()))
                .setNavn(DetaljertNavn.newBuilder().setNavn1(name).setRedigertNavn(name).build())
                .build();
    }

}
