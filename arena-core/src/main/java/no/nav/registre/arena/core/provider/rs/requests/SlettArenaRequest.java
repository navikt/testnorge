package no.nav.registre.arena.core.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SlettArenaRequest {
    String miljoe;
    List<String> identer;
}
