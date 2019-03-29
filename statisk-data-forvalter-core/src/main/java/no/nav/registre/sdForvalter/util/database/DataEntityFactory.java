package no.nav.registre.sdForvalter.util.database;

import javax.persistence.Entity;
import java.util.List;

import no.nav.registre.sdForvalter.database.ModelEnum;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.DkifModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;

class DataEntityFactory {

    static Entity create(ModelEnum dataType, List<String> content, List<String> headers) {
        switch (dataType) {
            case AAREG:
                AaregModel aaregModel = AaregModel.builder().build();
                aaregModel.updateFromString(content, headers);
                return (Entity) aaregModel;
            case TPS:
                TpsModel tpsModel = TpsModel.builder().build();
                tpsModel.updateFromString(content, headers);
                return (Entity) tpsModel;
            case DKIF:
                DkifModel dkifModel = DkifModel.builder().build();
                dkifModel.updateFromString(content, headers);
                return (Entity) dkifModel;
        }
        throw new IllegalArgumentException(String.format("Unable to create model of type: %s", dataType));
    }

}
