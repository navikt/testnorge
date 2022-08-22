package no.nav.testnav.mocks.tokendingsmock.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Arguments {
    private String audience;
    private String subject_token;
    private String pid;
}
