package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort;

import java.util.List;

public record SkattekortData(
        String utstedtDato,
        int inntektsaar,
        String resultatForSkattekort,
        List<Forskuddstrekk> forskuddstrekkList,
        List<String> tilleggsopplysningList
) {

    public record Forskuddstrekk(String trekkode, Frikort frikort) {
    }

    public record Frikort(int frikortBeloep) {
    }
}
