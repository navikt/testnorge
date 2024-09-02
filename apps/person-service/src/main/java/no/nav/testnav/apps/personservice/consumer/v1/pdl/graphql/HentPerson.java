package no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentPerson {
    List<Navn> navn;
    List<Kjoenn> kjoenn;
    List<Foedselsdato> foedselsdato;
    List<Bostedsadresse> bostedsadresse;
    List<Folkeregisteridentifikator> folkeregisteridentifikator;
    List<Folkeregisterpersonstatus> folkeregisterpersonstatus;

    public List<Navn> getNavn() {

        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }

    public List<Foedselsdato> getFoedselsdato() {

        if (isNull(foedselsdato)) {
            foedselsdato = new ArrayList<>();
        }
        return foedselsdato;
    }

    public List<Kjoenn> getKjoenn() {

        if (isNull(kjoenn)) {
            kjoenn = new ArrayList<>();
        }
        return kjoenn;
    }

    public List<Folkeregisteridentifikator> getFolkeregisteridentifikator() {

        if (isNull(folkeregisteridentifikator)) {
            folkeregisteridentifikator = new ArrayList<>();
        }
        return folkeregisteridentifikator;
    }

    public List<Bostedsadresse> getBostedsadresse() {

        if(isNull(bostedsadresse)) {
            bostedsadresse = new ArrayList<>();
        }
        return bostedsadresse;
    }

    public List<Folkeregisterpersonstatus> getFolkeregisterpersonstatus() {

        if (isNull(folkeregisterpersonstatus)) {
            folkeregisterpersonstatus = new ArrayList<>();
        }
        return folkeregisterpersonstatus;
    }
}
