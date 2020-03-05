package no.nav.registre.sdForvalter.database.model.factory;

import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;
import no.nav.registre.sdForvalter.database.ModelEnum;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.TpsIdentModel;

public class DataEntityFactory {

    public static Object create(ModelEnum dataType, List<String> content, List<String> headers) {
        switch (dataType) {
            case AAREG:
                AaregModel aaregModel = AaregModel.builder().build();
                aaregModel.updateFromString(content, headers);
                if (isEmpty(aaregModel.getFnr())) {
                    throw new IllegalArgumentException(String.format("Unable to create %s because it is missing FNR", dataType));
                }
                return aaregModel;
            case TPS:
                TpsIdentModel tpsIdentModel = TpsIdentModel.builder().build();
                tpsIdentModel.updateFromString(content, headers);
                if (isEmpty(tpsIdentModel.getFnr())) {
                    throw new IllegalArgumentException(String.format("Unable to create %s because it is missing FNR", dataType));
                }
                return tpsIdentModel;
            case DKIF:
                KrrModel krrModel = KrrModel.builder().build();
                krrModel.updateFromString(content, headers);
                if (isEmpty(krrModel.getFnr())) {
                    throw new IllegalArgumentException(String.format("Unable to create %s because it is missing FNR", dataType));
                }
                return krrModel;
        }
        throw new IllegalArgumentException(String.format("Unable to create model of type: %s", dataType));
    }

}
