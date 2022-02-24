package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static java.util.Objects.nonNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OppholdsadresseDTO extends AdresseDTO {

    private OppholdAnnetSted oppholdAnnetSted;
    private VegadresseDTO vegadresse;
    private UtenlandskAdresseDTO utenlandskAdresse;
    private MatrikkeladresseDTO matrikkeladresse;

    @JsonIgnore
    public int countAdresser() {

        return count(getVegadresse()) + count(getUtenlandskAdresse()) + count(getMatrikkeladresse());
    }

    @Override
    @JsonIgnore
    public boolean isAdresseNorge() {

        return nonNull(getVegadresse()) || nonNull(getMatrikkeladresse());
    }
}
