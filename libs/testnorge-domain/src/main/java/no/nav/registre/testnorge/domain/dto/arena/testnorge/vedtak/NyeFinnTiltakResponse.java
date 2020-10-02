package no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NyeFinnTiltakResponse {

    @JsonProperty("nyeFinntiltak")
    private List<NyttVedtakTiltak> nyeFinnTiltak;

    @JsonProperty("nyeFinntiltakFeilList")
    private List<NyttVedtakFeil> nyeFinnTiltakFeilList;


    public List<NyttVedtakTiltak> getNyeFinnTiltak() {
        return nyeFinnTiltak != null ? nyeFinnTiltak : Collections.emptyList();
    }

    public List<NyttVedtakFeil> getNyeFinntiltakFeilList() {
        return nyeFinnTiltakFeilList != null ? nyeFinnTiltakFeilList : Collections.emptyList();
    }
}