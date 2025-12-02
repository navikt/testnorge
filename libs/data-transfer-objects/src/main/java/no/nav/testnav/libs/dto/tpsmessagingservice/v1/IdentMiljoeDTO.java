package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IdentMiljoeDTO {

    private String ident;
    private Set<String> miljoer;

    public Set<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new HashSet<>();
        }
        return miljoer;
    }
}