package no.nav.dolly.domain.testperson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentIbruk extends IdentAttributes {

    private boolean ibruk;
}
