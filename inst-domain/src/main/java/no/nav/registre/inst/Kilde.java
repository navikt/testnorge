package no.nav.registre.inst;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Kilde {
        private String navn;
        private List<Institusjonsforholdsmelding> data;
}
