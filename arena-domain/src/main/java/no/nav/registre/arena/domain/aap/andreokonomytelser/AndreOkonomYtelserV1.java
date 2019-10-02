package no.nav.registre.arena.domain.aap.andreokonomytelser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AndreOkonomYtelserV1 {
    private List<AnnenOkonomYtelseV1> annenOkonomYtelse;
}
