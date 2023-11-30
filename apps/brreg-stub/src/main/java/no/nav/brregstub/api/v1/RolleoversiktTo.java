package no.nav.brregstub.api.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RolleoversiktTo {

    @NotBlank
    private String fnr;

    private LocalDate fodselsdato;

    @NotNull
    @Valid
    private RsNavn navn;

    @NotNull
    @Valid
    private RsAdresse adresse;

    @Valid
    @NotEmpty
    private List<RolleTo> enheter = new LinkedList<>();

    private Integer hovedstatus = 0;

    private List<Integer> understatuser = new LinkedList<>();

    public List<Integer> getUnderstatuser() {
        if (understatuser == null) {
            understatuser = new LinkedList<>();
        }
        return understatuser;
    }
}
