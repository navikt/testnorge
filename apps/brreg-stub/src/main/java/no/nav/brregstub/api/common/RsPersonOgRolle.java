package no.nav.brregstub.api.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RsPersonOgRolle {

    @NotBlank
    private String fodselsnr;

    @NotBlank
    private String rolle;

    @NotBlank
    private String rollebeskrivelse;

    @NotBlank
    private String fornavn;

    @NotBlank
    private String slektsnavn;

    private String adresse1;

    private String postnr;

    private String poststed;
    private boolean fratraadt;
}
