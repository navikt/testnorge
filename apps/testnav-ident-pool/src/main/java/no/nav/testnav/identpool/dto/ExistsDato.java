package no.nav.testnav.identpool.dto;

import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTermType;

import java.time.LocalDate;

public interface ExistsDato {

    LocalDate getFoedselsdato();

    String getIdenttype();

    Long getAntall();
}
