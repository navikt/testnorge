package no.nav.registre.arena.core.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import no.nav.registre.arena.domain.rettighet.NyttVedtak;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RettighetAapRequest.class, name = "aap"),
        @JsonSubTypes.Type(value = RettighetAap115Request.class, name = "aap115"),
        @JsonSubTypes.Type(value = RettighetUngUfoerRequest.class, name = "ungUfoer"),
        @JsonSubTypes.Type(value = RettighetTvungenForvaltningRequest.class, name = "tvungenForvaltning"),
        @JsonSubTypes.Type(value = RettighetFritakMeldekortRequest.class, name = "fritakMeldekort")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class RettighetRequest implements Serializable {

    private String personident;
    private String miljoe;

    public abstract List<NyttVedtak> getVedtak();
}
