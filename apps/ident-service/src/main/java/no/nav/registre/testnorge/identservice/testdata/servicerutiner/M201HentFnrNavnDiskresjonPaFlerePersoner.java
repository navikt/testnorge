package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import no.nav.registre.testnorge.identservice.testdata.request.TpsHentFnrHistMultiServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionBuilder;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.ServiceRoutineRequestTransform;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.resolvers.ServiceRoutineResolver;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.ResponseStatusTransformer;
import no.nav.registre.testnorge.identservice.testdata.transformation.response.ResponseDataTransformer;


public class M201HentFnrNavnDiskresjonPaFlerePersoner implements ServiceRoutineResolver {

    public static final String REQUEST_QUEUE_SERVICE_RUTINE_ALIAS = "TPS_FORESPORSEL_XML_O";

    @Override
    public TpsServiceRoutineDefinitionRequest resolve() {
        return TpsServiceRoutineDefinitionBuilder.aTpsServiceRoutine()
                .name("FS03-FDLISTER-DISKNAVN-M")
                .internalName("M201 Hent Fnr Navn")
                .javaClass(TpsHentFnrHistMultiServiceRoutineRequest.class)
                .config()
                .requestQueue(REQUEST_QUEUE_SERVICE_RUTINE_ALIAS)

                .and()
                .parameter()
                .name("antallFnr")
                .required()
                .type(TpsParameterType.STRING)
                .and()

                .parameter()
                .name("nFnr")
                .required()
                .type(TpsParameterType.STRING)
                .and()

                .parameter()
                .name("aksjonsKode")
                .required()
                .type(TpsParameterType.STRING)
                .values("A0", "B0", "A1", "B1")

                .and()
                .transformer()
                .preSend(ServiceRoutineRequestTransform.serviceRoutineXmlWrappingAppender())
                .postSend(ResponseDataTransformer.extractDataFromXmlElement("personDataM201"))
                .postSend(ResponseStatusTransformer.extractStatusFromXmlElement("svarStatus"))
                .and()

                .build();
    }
}
