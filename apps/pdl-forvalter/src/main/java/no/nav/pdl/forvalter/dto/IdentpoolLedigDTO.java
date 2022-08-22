package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentpoolLedigDTO {

    private String ident;
    private boolean ledig;

    @JsonIgnore
    public boolean isIBruk() {
        return !isLedig();
    }
}
