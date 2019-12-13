package no.nav.registre.arena.core.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.rettighet.NyRettighet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetTvungenForvaltningRequest extends RettighetRequest {

    private List<NyRettighet> nyeAatfor;
}
