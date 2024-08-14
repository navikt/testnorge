package no.nav.registre.testnorge.levendearbeidsforhold.domain.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Gyldighetsperiode extends Periode {

    @Override
    @SuppressWarnings({"pmd:ConsecutiveLiteralAppends", "pmd:ConsecutiveAppendsShouldReuse", "fb-contrib:UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "pmd:AppendCharacterWithChar"})
    public String toString() {
        return "Gyldighetsperiode{" + "fom=" + getFomAsString() + ", tom=" + getTomAsString() + "}";
    }
}
