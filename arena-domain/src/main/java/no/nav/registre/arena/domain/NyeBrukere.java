package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NyeBrukere {
    @JsonProperty("nyeBrukere")
    List<NyBruker> nyeBrukere;
}
