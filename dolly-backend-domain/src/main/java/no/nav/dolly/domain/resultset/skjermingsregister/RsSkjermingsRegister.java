package no.nav.dolly.domain.resultset.skjermingsregister;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
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
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsSkjermingsRegister {

    @ApiModelProperty(position = 1, value = "Ident på skjermet person")
    private String personident;

    @ApiModelProperty(position = 2, value = "Fornavn til skjermet person")
    private String fornavn;

    @ApiModelProperty(position = 3, value = "Etternavn til skjermet person")
    private String etternavn;

    @ApiModelProperty(position = 4, value = "Dato og tidspunkt personen ble skjermet fra")
    private LocalDateTime skjermetFra;

    @ApiModelProperty(position = 5, value = "Dato og tidspunkt personen blir skjermet til")
    private LocalDateTime skjermetTil;
}
