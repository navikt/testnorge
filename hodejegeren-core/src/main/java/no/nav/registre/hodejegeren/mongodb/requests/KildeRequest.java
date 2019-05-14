package no.nav.registre.hodejegeren.mongodb.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KildeRequest {

    private String navn;
    private List<Object> data;
}
