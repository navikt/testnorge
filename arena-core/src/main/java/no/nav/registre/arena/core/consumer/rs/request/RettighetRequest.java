package no.nav.registre.arena.core.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import no.nav.registre.arena.domain.vedtak.NyttVedtakAap;
import no.nav.registre.arena.domain.vedtak.NyttVedtakTiltak;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RettighetAapRequest.class, name = "aap"),
        @JsonSubTypes.Type(value = RettighetAap115Request.class, name = "aap115"),
        @JsonSubTypes.Type(value = RettighetUngUfoerRequest.class, name = "ungUfoer"),
        @JsonSubTypes.Type(value = RettighetTvungenForvaltningRequest.class, name = "tvungenForvaltning"),
        @JsonSubTypes.Type(value = RettighetFritakMeldekortRequest.class, name = "fritakMeldekort"),
        @JsonSubTypes.Type(value = RettighetTiltaksdeltakelseRequest.class, name = "tiltaksdeltakelse"),
        @JsonSubTypes.Type(value = RettighetTiltakspengerRequest.class, name = "tiltakspenger"),
        @JsonSubTypes.Type(value = RettighetBarnetilleggRequest.class, name = "barnetillegg")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RettighetRequest implements Serializable {

    private String personident;
    private String miljoe;

    public abstract List<NyttVedtakAap> getVedtakAap();

    public abstract List<NyttVedtakTiltak> getVedtakTiltak();
}
