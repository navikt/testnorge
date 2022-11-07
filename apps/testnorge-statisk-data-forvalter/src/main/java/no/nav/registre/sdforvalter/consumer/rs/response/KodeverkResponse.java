package no.nav.registre.sdforvalter.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KodeverkResponse {

    private List<String> koder;
}
