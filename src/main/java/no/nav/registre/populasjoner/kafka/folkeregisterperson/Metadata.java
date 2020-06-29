package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metadata {

    private String opplysningsId;
    private String master;
    private List<Endring> endringer;
    private Boolean historisk;
}