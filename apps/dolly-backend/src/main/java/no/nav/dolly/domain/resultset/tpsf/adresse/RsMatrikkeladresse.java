package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
@JsonTypeName("Matrikkeladresse")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsMatrikkeladresse extends RsAdresse {

    private String mellomnavn;

    private String gardsnr;

    private String bruksnr;

    private String festenr;

    private String undernr;

    @Override
    public String getAdressetype() {
        return "MATR";
    }
}
