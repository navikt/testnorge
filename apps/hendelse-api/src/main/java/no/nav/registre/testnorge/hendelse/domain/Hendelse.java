package no.nav.registre.testnorge.hendelse.domain;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import no.nav.registre.testnorge.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseType;
import no.nav.registre.testnorge.hendelse.repository.model.HendelseModel;
import no.nav.registre.testnorge.hendelse.repository.model.IdentModel;


@Getter
public class Hendelse {

    private final String ident;
    private final LocalDate fom;
    private final LocalDate tom;
    private final HendelseType type;

    public Hendelse(HendelseModel model) {
        ident = model.getIdent().getIdent();
        fom = toLocalDate(model.getFom());
        tom = toLocalDate(model.getTom());
        type = model.getHendelse();
    }

    public Hendelse(HendelseDTO dto) {
        ident = dto.getIdent();
        fom = dto.getFom();
        tom = dto.getTom();
        type = dto.getType();
    }

    public HendelseDTO toDTO() {
        return HendelseDTO
                .builder()
                .fom(fom)
                .tom(tom)
                .ident(ident)
                .type(type)
                .build();
    }

    public HendelseModel toHendelseModel(IdentModel identModel) {
        return HendelseModel
                .builder()
                .fom(toDate(fom))
                .tom(toDate(tom))
                .hendelse(type)
                .ident(identModel)
                .build();
    }


    private Date toDate(LocalDate localDate) {
        return localDate != null ? java.sql.Date.valueOf(localDate) : null;
    }

    private LocalDate toLocalDate(Date date) {
        return date != null
                ? Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
    }
}
