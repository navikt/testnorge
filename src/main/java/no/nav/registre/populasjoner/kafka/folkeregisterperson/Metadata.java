package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class Metadata {

    private final String opplysningsId;
    @NotEmpty
    private final String master;
    @NotNull
    private final List<Endring> endringer;
    @Setter
    private Boolean historisk;
}