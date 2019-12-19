package no.nav.registre.arena.core.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RettighetAapRequest.class, name = "aap"),
        @JsonSubTypes.Type(value = RettighetUngUfoerRequest.class, name = "ungUfoer"),
        @JsonSubTypes.Type(value = RettighetTvungenForvaltningRequest.class, name = "tvungenForvaltning"),
        @JsonSubTypes.Type(value = RettighetFritakMeldekortRequest.class, name = "fritakMeldekort")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetRequest implements Serializable {

    private String personident;
    private String miljoe;
}
