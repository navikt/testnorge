package no.nav.organisasjonforvalter.service;

import no.nav.organisasjonforvalter.provider.rs.requests.DeployRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon;
import org.springframework.stereotype.Service;

@Service
public class DeploymentService {

    public DeployResponse deploy(DeployRequest request){

//        organisasjonMottakConsumer.send(Organisasjon.newBuilder()
//                .setNavn(orgname.get(0))
//                .setMetadata(Metadata.newBuilder()
//                        .setEnhetstype("AS")
//                        .setOrgnummer(orgNummer.get(0))
//                        .setMiljo("t4")
//                        .build())
//                .build());
        return null;
    }
}
