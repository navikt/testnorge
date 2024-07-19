package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerdiId implements Serializable {
    private String navn;
    private String verdi;
}
