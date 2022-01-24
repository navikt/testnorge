package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public enum Tags {
    SALESFORCE("Salesforce"),
    DOLLY("Dolly");

    private String beskrivelse;

    Tags(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagBeskrivelse {

        private String tag;
        private String beskrivelse;
    }
}
