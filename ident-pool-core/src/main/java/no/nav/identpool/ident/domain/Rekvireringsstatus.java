package no.nav.identpool.ident.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Rekvireringsstatus {
    LEDIG("ledig"),
    I_BRUK("i_bruk");

    private String status;
}
