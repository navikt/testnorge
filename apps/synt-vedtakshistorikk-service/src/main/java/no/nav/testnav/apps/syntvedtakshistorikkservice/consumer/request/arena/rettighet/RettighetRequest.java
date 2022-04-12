package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.io.Serializable;
import java.util.List;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RettighetAapRequest.class, name = "aap"),
        @JsonSubTypes.Type(value = RettighetAap115Request.class, name = "aap115"),
        @JsonSubTypes.Type(value = RettighetUngUfoerRequest.class, name = "ungUfoer"),
        @JsonSubTypes.Type(value = RettighetTvungenForvaltningRequest.class, name = "tvungenForvaltning"),
        @JsonSubTypes.Type(value = RettighetFritakMeldekortRequest.class, name = "fritakMeldekort"),
        @JsonSubTypes.Type(value = RettighetTiltaksdeltakelseRequest.class, name = "tiltaksdeltakelse"),
        @JsonSubTypes.Type(value = RettighetTiltakspengerRequest.class, name = "tiltakspenger"),
        @JsonSubTypes.Type(value = RettighetTiltaksaktivitetRequest.class, name = "tiltaksaktivitet"),
        @JsonSubTypes.Type(value = RettighetTilleggsytelseRequest.class, name = "tilleggsytelse"),
        @JsonSubTypes.Type(value = RettighetTilleggRequest.class, name = "tilleggstonad"),
        @JsonSubTypes.Type(value = RettighetEndreDeltakerstatusRequest.class, name = "endreDeltakerstatus")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class RettighetRequest implements Serializable {

    private String personident;
    private String miljoe;

    @JsonIgnore
    public abstract String getArenaForvalterUrlPath();

    public abstract List<NyttVedtakAap> getVedtakAap();

    public abstract List<NyttVedtakTiltak> getVedtakTiltak();

    public abstract List<NyttVedtakTillegg> getVedtakTillegg();
}
