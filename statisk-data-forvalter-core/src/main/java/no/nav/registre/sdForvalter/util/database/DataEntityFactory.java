package no.nav.registre.sdForvalter.util.database;

import java.util.List;

import no.nav.registre.sdForvalter.database.ModelEnum;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.DkifModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;

class DataEntityFactory {

    static Object create(ModelEnum dataType, List<String> content, List<String> headers) {
        switch (dataType) {
            case AAREG:
                AaregModel aaregModel = AaregModel.builder().build();
                aaregModel.updateFromString(content, headers);
                if ("".equals(aaregModel.getFnr()) || aaregModel.getFnr() == null) {
                    throw new IllegalArgumentException(String.format("Unable to create %s because it is missing FNR", dataType));
                }
                return aaregModel;
            case TPS:
                TpsModel tpsModel = TpsModel.builder().build();
                tpsModel.updateFromString(content, headers);
                if ("".equals(tpsModel.getFnr()) || tpsModel.getFnr() == null) {
                    throw new IllegalArgumentException(String.format("Unable to create %s because it is missing FNR", dataType));
                }
                return tpsModel;
            case DKIF:
                DkifModel dkifModel = DkifModel.builder().build();
                dkifModel.updateFromString(content, headers);
                if ("".equals(dkifModel.getFnr()) || dkifModel.getFnr() == null) {
                    throw new IllegalArgumentException(String.format("Unable to create %s because it is missing FNR", dataType));
                }
                return dkifModel;
        }
        throw new IllegalArgumentException(String.format("Unable to create model of type: %s", dataType));
    }

}
