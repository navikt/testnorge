package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdreRequest {

    private List<Ordre> sletting;
    private List<Ordre> split;
    private List<Ordre> oppretting;
    private List<Ordre> merge;
    private List<Ordre> opplysninger;

    public List<Ordre> getSletting() {

        if (isNull(sletting)) {
            sletting = new ArrayList<>();
        }
        return sletting;
    }

    public List<Ordre> getSplit() {

        if (isNull(split)) {
            split = new ArrayList<>();
        }
        return split;
    }

    public List<Ordre> getOppretting() {

        if (isNull(oppretting)) {
            oppretting = new ArrayList<>();
        }
        return oppretting;
    }

    public List<Ordre> getMerge() {

        if (isNull(merge)) {
            merge = new ArrayList<>();
        }
        return merge;
    }

    public List<Ordre> getOpplysninger() {

        if (isNull(opplysninger)) {
            opplysninger = new ArrayList<>();
        }
        return opplysninger;
    }
}
