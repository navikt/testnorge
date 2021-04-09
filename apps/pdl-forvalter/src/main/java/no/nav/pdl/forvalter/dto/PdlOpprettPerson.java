package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOpprettPerson {

    private String opprettetIdent;
    private List<String> historiskeIdenter;

    public List<String> getHistoriskeIdenter() {
        if (isNull(historiskeIdenter)) {
            return new ArrayList<>();
        }
        return historiskeIdenter;
    }
}
