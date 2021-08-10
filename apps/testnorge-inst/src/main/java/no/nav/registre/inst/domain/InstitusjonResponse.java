package no.nav.registre.inst.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class InstitusjonResponse {

    private List<Institusjonsopphold> q1;
    private List<Institusjonsopphold> q2;
    private List<Institusjonsopphold> q4;
    private List<Institusjonsopphold> t0;
    private List<Institusjonsopphold> t4;
    private List<Institusjonsopphold> t6;
}
