package no.nav.registre.varslingerapi.domain;


import lombok.Value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import no.nav.testnav.libs.dto.varslingerapi.v1.VarslingDTO;
import no.nav.registre.varslingerapi.repository.model.VarslingModel;

@Value
public class Varsling {

    String varslingId;
    LocalDate fom;
    LocalDate tom;

    public Varsling(VarslingModel model) {
        this.varslingId = model.getVarslingId();
        this.fom = toLocalDate(model.getFom());
        this.tom = toLocalDate(model.getTom());
    }


    public Varsling(VarslingDTO dto) {
        this.varslingId = dto.getVarslingId();
        this.fom = dto.getFom();
        this.tom = dto.getTom();
    }

    private static LocalDate toLocalDate(Date date) {
        return date != null
                ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
    }

    private static Date toDate(LocalDate localDate) {
        return localDate != null
                ? Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                : null;
    }


    public VarslingDTO toDTO() {
        return VarslingDTO
                .builder()
                .varslingId(varslingId)
                .fom(fom)
                .tom(tom)
                .build();
    }


    public VarslingModel toModel() {
        return VarslingModel
                .builder()
                .varslingId(varslingId)
                .fom(toDate(fom))
                .tom(toDate(tom))
                .build();
    }

}
