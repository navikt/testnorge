package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BostedadresseDTO extends AdresseDTO {

    private LocalDateTime angittFlyttedato;
    private VegadresseDTO vegadresse;
    private UkjentBostedDTO ukjentBosted;
    private MatrikkeladresseDTO matrikkeladresse;
    private UtenlandskAdresseDTO utenlandskAdresse;

    @JsonIgnore
    public int countAdresser() {

        return count(getVegadresse()) + count(getMatrikkeladresse()) +
                count(getUkjentBosted()) + count(getUtenlandskAdresse());
    }

    @Override
    @JsonIgnore
    public boolean isAdresseNorge() {

        return nonNull(getVegadresse()) || nonNull(getUkjentBosted()) || nonNull(getMatrikkeladresse());
    }
}
