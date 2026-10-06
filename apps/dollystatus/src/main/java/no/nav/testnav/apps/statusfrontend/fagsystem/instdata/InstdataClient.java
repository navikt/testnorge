package no.nav.testnav.apps.statusfrontend.fagsystem.instdata;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InstdataClient {

    Mono<InstdataEnvironments> getEnvironments(RunId runId);

    Mono<InstdataResourceStatus> getInstdata(
            RunId runId,
            String ident,
            String environment,
            InstdataRecord expectedRecord
    );

    Mono<Void> createInstdata(RunId runId, String environment, InstdataRecord record);

    Mono<Void> deleteInstdata(RunId runId, String ident, List<String> environments);
}
