package no.nav.dolly.bestilling.udistub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdiPersonWrapper {

    public enum Status {NEW, UPDATE}

    private UdiPerson udiPerson;
    private RsAliasRequest aliasRequest;
    private Status status;
}
