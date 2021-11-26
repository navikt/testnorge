package no.nav.brregstub.api.v1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
