package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum Tags {
    SALESFORCE("Salesforce"),
    UTBETALING("Utbetaling"),
    DOLLY("Dolly");

    private final String beskrivelse;

    Tags(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagBeskrivelse {

        private String tag;
        private String beskrivelse;
    }
}
