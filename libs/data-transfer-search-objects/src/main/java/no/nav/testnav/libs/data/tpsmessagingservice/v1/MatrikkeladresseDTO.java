package no.nav.testnav.libs.data.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MatrikkeladresseDTO extends AdresseDTO {

    private String mellomnavn;

    private String gardsnr;

    private String bruksnr;

    private String festenr;

    private String undernr;

    @Override
    public Adressetype getAdressetype() {
        return Adressetype.MATR;
    }
}
