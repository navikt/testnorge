package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl;

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
    List<Foedsel> foedsel;
    List<Bostedsadresse> bostedsadresse;
    List<Folkeregisteridentifikator> folkeregisteridentifikator;
    List<Folkeregisterpersonstatus> folkeregisterpersonstatus;

    public List<Navn> getNavn() {

        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }

    public List<Foedsel> getFoedsel() {

        if (isNull(foedsel)) {
            foedsel = new ArrayList<>();
        }
        return foedsel;
    }

    public List<Bostedsadresse> getBostedsadresse() {

        if (isNull(bostedsadresse)) {
            bostedsadresse = new ArrayList<>();
        }
        return bostedsadresse;
    }

    public List<Folkeregisteridentifikator> getFolkeregisteridentifikator() {

        if (isNull(folkeregisteridentifikator)) {
            folkeregisteridentifikator = new ArrayList<>();
        }
        return folkeregisteridentifikator;
    }

    public List<Folkeregisterpersonstatus> getFolkeregisterpersonstatus() {

        if (isNull(folkeregisterpersonstatus)) {
            folkeregisterpersonstatus = new ArrayList<>();
        }
        return folkeregisterpersonstatus;
    }
}
