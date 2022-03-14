package no.nav.identpool.dto;

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
public class TpsIdentStatusDTO {

    private String ident;
    private List<String> miljoer;

    public List<String> getMiljoer() {
        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }
}
