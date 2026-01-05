package no.nav.testnav.identpool.consumers.command;

import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;

import java.util.function.Function;

@FunctionalInterface
public interface TpsValidation extends Function<TpsIdentStatusDTO, Boolean> {

}