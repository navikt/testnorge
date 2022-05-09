package no.nav.testnav.identpool.consumers.command;

import no.nav.testnav.identpool.dto.TpsIdentStatusDTO;

import java.util.function.Function;

@FunctionalInterface
public interface TpsValidation extends Function<TpsIdentStatusDTO, Boolean> {

}