package no.nav.testnav.apps.importfratpsfservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.importfratpsfservice.consumer.DollyConsumer;
import no.nav.testnav.apps.importfratpsfservice.consumer.PdlForvalterConsumer;
import no.nav.testnav.apps.importfratpsfservice.consumer.TpsfConsumer;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingGruppe;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;
    private final PdlForvalterConsumer pdlForvalterConsumer;
    private final DollyConsumer dollyConsumer;
    private final MapperFacade mapperFacade;

    public Flux<String> importIdenter(Long skdgruppeId, Long dollyGruppeId) {

        return tpsfConsumer.getSkdGrupper()
                .filter(gruppe -> gruppe.getId().equals(skdgruppeId))
                .next()
                .flatMapMany(gruppe -> Flux.range(0, gruppe.getAntallSider().intValue()))
                .flatMap(page ->  tpsfConsumer.getSkdMeldinger(skdgruppeId, page.longValue()))
                .map(melding -> mapperFacade.map(melding, PersonDTO.class))
                .flatMap(person -> pdlForvalterConsumer.putPdlPerson(person.getIdent(), person).thenReturn(person))
                .flatMap(person -> pdlForvalterConsumer.postOrdrePdlPerson(person.getIdent()).thenReturn(person))
                .flatMap(person -> dollyConsumer.putGrupperIdent(dollyGruppeId, person.getIdent(), Master.PDL).thenReturn(person))
                .map(person -> format("Importert ident %s -- %s %s" + person.getIdent(),
                        person.getNavn().get(0).getFornavn(), person.getNavn().get(0).getEtternavn()));
    }
}
