package no.nav.testnav.mocks.tokendings.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Arguments {
    @JsonProperty("audience")
    private String audience;
    @JsonProperty("subject_token")
    private String subjectToken;
    @JsonProperty("pid")
    private String pid;
}
