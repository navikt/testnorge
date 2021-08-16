package no.nav.registre.inst.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class InstitusjonResponse {

    private List<InstitusjonsoppholdV2> q1;
    private List<InstitusjonsoppholdV2> q2;
    private List<InstitusjonsoppholdV2> q4;
    private List<InstitusjonsoppholdV2> t0;
    private List<InstitusjonsoppholdV2> t4;
    private List<InstitusjonsoppholdV2> t6;
}
