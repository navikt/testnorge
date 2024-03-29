package no.nav.brregstub.api.v2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RsRolleoversikt {

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
    private List<RsRolle> enheter = new LinkedList<>();

    private Integer hovedstatus = 0;

    private List<Integer> understatuser = new LinkedList<>();

    public List<Integer> getUnderstatuser() {
        if (understatuser == null) {
            understatuser = new ArrayList<>();
        }
        return understatuser;
    }
}
