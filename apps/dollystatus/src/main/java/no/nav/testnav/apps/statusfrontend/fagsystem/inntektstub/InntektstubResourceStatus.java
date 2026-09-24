package no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub;

import tools.jackson.databind.JsonNode;

public record InntektstubResourceStatus(boolean empty, boolean expectedDataPresent) {

    public static InntektstubResourceStatus from(
            JsonNode response,
            InntektstubRequest expectedRequest
    ) {
        if (!response.isArray()) {
            throw new IllegalStateException("Inntektstub-oppslaget returnerte ugyldig respons.");
        }
        if (response.isEmpty()) {
            return emptyStatus();
        }
        for (var entry : response) {
            rejectValidationError(entry);
            for (var income : entry.path("inntektsliste")) {
                rejectValidationError(income);
            }
        }
        for (var entry : response) {
            if (matches(entry, expectedRequest)) {
                return new InntektstubResourceStatus(false, true);
            }
        }
        return new InntektstubResourceStatus(false, false);
    }

    public static InntektstubResourceStatus emptyStatus() {
        return new InntektstubResourceStatus(true, false);
    }

    private static void rejectValidationError(JsonNode entry) {
        if (!entry.path("feilmelding").asText("").isBlank()) {
            throw new IllegalArgumentException("Inntektstub avviste testdata.");
        }
    }

    private static boolean matches(JsonNode entry, InntektstubRequest expectedRequest) {
        if (!expectedRequest.norskIdent().equals(entry.path("norskIdent").asString())
                || !expectedRequest.aarMaaned().equals(entry.path("aarMaaned").asString())
                || !expectedRequest.opplysningspliktig().equals(
                entry.path("opplysningspliktig").asString())
                || !expectedRequest.virksomhet().equals(entry.path("virksomhet").asString())) {
            return false;
        }
        var expectedIncome = expectedRequest.inntektsliste().getFirst();
        var incomes = entry.path("inntektsliste");
        if (!incomes.isArray()) {
            return false;
        }
        for (var income : incomes) {
            if (expectedIncome.inntektstype().equals(income.path("inntektstype").asString())
                    && Double.compare(
                    expectedIncome.beloep(),
                    income.path("beloep").asDouble()) == 0
                    && expectedIncome.beskrivelse().equals(income.path("beskrivelse").asString())
                    && expectedIncome.fordel().equals(income.path("fordel").asString())) {
                return true;
            }
        }
        return false;
    }
}
