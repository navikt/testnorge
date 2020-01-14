package no.nav.registre.arena.core.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.rettighet.NyttVedtak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetAap115Request extends RettighetRequest {

    private List<NyttVedtak> nyeAap115;

    @JsonProperty("nyeAap115")
    @Override public List<NyttVedtak> getVedtak() {
        return nyeAap115;
    }
}
