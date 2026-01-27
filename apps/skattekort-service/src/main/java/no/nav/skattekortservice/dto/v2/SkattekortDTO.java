package no.nav.skattekortservice.dto.v2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortDTO {

    private LocalDate utstedtDato;

    @NotNull
    @Min(2025)
    @Max(3000)
    private Integer inntektsaar;

    private ResultatForSkattekort resultatForSkattekort;

    @NotNull
    @Valid
    private List<ForskuddstrekkDTO> forskuddstrekkList;

    private List<TilleggsopplysningType> tilleggsopplysningList;

    public List<ForskuddstrekkDTO> getForskuddstrekkList() {
        if (isNull(forskuddstrekkList)) {
            forskuddstrekkList = new ArrayList<>();
        }
        return forskuddstrekkList;
    }

    public List<TilleggsopplysningType> getTilleggsopplysningList() {
        if (isNull(tilleggsopplysningList)) {
            tilleggsopplysningList = new ArrayList<>();
        }
        return tilleggsopplysningList;
    }
}
