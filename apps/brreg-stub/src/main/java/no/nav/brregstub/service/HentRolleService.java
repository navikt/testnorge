package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.mapper.HentRolleMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HentRolleService {

private final HentRolleRepository hentRolleRepository;

private final ObjectMapper objectMapper;

@SneakyThrows
public Mono<Optional<RsOrganisasjon>> lagreEllerOppdaterDataForHentRolle(RsOrganisasjon request) {
return Mono.fromCallable(() -> {
HentRolleMapper.map(request); //sjekker om input kan mappes før lagring

var rollutskrift = hentRolleRepository.findByOrgnr(request.getOrgnr())
.orElseGet(() -> {
var hentRolle = new HentRolle();
hentRolle.setOrgnr(request.getOrgnr());
return hentRolle;
});

rollutskrift.setJson(objectMapper.writeValueAsString(request));

hentRolleRepository.save(rollutskrift);
return Optional.of(request);
})
.subscribeOn(Schedulers.boundedElastic());
}

@SneakyThrows
public Mono<Optional<RsOrganisasjon>> hentRolle(Integer ident) {
return Mono.fromCallable(() -> {
var hentRolle = hentRolleRepository.findByOrgnr(ident);

if (hentRolle.isPresent()) {
var to = objectMapper.readValue(hentRolle.get().getJson(), RsOrganisasjon.class);
return Optional.of(to);
}
return Optional.<RsOrganisasjon>empty();
})
.subscribeOn(Schedulers.boundedElastic());
}

public Mono<Void> slettHentRolle(Integer ident) {
return Mono.fromRunnable(() ->
hentRolleRepository.findByOrgnr(ident).ifPresent(hentRolleRepository::delete))
.subscribeOn(Schedulers.boundedElastic())
.then();
}

}
