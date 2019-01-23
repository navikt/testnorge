package no.nav.registre.sigrun.domain;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Getter
public class Grunnlag {

    @NotNull
    private List<Map<String, String>> grunnlag;

    public Grunnlag(Object obj) {
        this.grunnlag = (ArrayList<Map<String, String>>) obj;
    }

    public List<Map<String, String>> getGrunnlag() {
        return grunnlag;
    }
}
