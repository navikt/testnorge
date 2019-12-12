package no.nav.registre.arena.core.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.core.consumer.rs.responses.rettighet.NyRettighet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetFritakMeldekortRequest extends RettighetRequest {

    private List<NyRettighet> nyeFritak;
}
