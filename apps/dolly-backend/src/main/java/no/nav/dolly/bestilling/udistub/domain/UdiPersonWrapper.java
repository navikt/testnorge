package no.nav.dolly.bestilling.udistub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdiPersonWrapper {

    public enum Status {NEW, UPDATE}

    private UdiPerson udiPerson;
    private Status status;
}
