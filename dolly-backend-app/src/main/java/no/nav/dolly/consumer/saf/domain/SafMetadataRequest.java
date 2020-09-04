package no.nav.dolly.consumer.saf.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SafMetadataRequest {

    String query;

    SafMetadataRequest(String brukerId, String brukerType, String foerste) {
        query = String.format("{" +
                "query {%n" +
                "  dokumentoversiktBruker(brukerId: {id: \"%s\", type: %s}, foerste: %s) {%n" +
                "    journalposter {%n" +
                "      journalpostId%n" +
                "      tittel%n" +
                "      journalposttype%n" +
                "      journalstatus%n" +
                "      tema%n" +
                "      dokumenter {%n" +
                "        dokumentInfoId%n" +
                "        tittel%n" +
                "      }%n" +
                "    }%n" +
                "  }%n" +
                "}}", brukerId, brukerType, foerste);
    }
}
