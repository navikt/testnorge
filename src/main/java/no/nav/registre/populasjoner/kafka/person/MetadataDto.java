package no.nav.registre.populasjoner.kafka.person;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class MetadataDto {

    private final String opplysningsId;
    @NotEmpty
    private final String master;
    @NotNull
    private final List<EndringDto> endringer;
    @Setter
    private Boolean historisk;
}