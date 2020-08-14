package no.nav.dolly.domain.resultset.sykemelding;

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
public class RsSykemelding {

    private RsSyntSykemelding syntSykemelding;
    private RsDetaljertSykemelding detaljertSykemelding;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class RsSyntSykemelding {

        @ApiModelProperty(position = 1)
        private String arbeidsforholdId;

        @ApiModelProperty(position = 2)
        private String ident;

        @ApiModelProperty(position = 3)
        private String orgnummer;

        @ApiModelProperty(position = 4)
        private LocalDateTime startDato;
    }
}
