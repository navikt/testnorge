package no.nav.dolly.bestilling.instdata.mapper;

import lombok.Getter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataDTO;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class InstdataMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsInstdata.class, InstdataDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdata rsInstdata, InstdataDTO instdata, MappingContext context) {

                        instdata.setNorskident((String) context.getProperty("ident"));
                        instdata.setRegistrertAv("Dolly");

                        instdata.setOppholdstype(mapKategori(rsInstdata.getInstitusjonstype()).name());
                        instdata.setInstitusjonsnummer(mapInstitusjonsnummer(rsInstdata.getInstitusjonstype()));
                    }

                    private static InstdataOppholdstype mapKategori(RsInstdata.InstdataInstitusjonstype type) {

                        return switch (type) {
                            case AS -> InstdataOppholdstype.A;
                            case FO -> InstdataOppholdstype.S;
                            default -> InstdataOppholdstype.R;
                        };
                    }

                    private static String mapInstitusjonsnummer(RsInstdata.InstdataInstitusjonstype type) {

                        return switch (type) {
                            case AS -> Institusjonsnummer.ADAMSTUEN_SYKEHJEM.getId();
                            case FO -> Institusjonsnummer.INDRE_OSTFOLD_FENGSEL.getId();
                            default -> Institusjonsnummer.HELGELANDSSYKEHUSET_HF.getId();
                        };
                    }
                })
                .byDefault()
                .register();
    }

    @Getter
    public enum InstdataOppholdstype {

        A ("Alders- og sykehjem"),
        D ("Dagpasient"),
        F ("Ferieopphold"),
        H ("Heldøgnpasient"),
        P ("Fødsel"),
        R ("Opptreningsinstitusjon"),
        S ("Soningsfange"),
        V ("Varetektsfange");

        private final String beskrivelse;

        InstdataOppholdstype(String beskrivelse) {
            this.beskrivelse = beskrivelse;
        }
    }

    @Getter
    public enum Institusjonsnummer {

        ADAMSTUEN_SYKEHJEM("1877666"),
        INDRE_OSTFOLD_FENGSEL("9700072"),
        HELGELANDSSYKEHUSET_HF("1879731");

        Institusjonsnummer(String id) {
            this.id = id;
        }

        private final String id;
    }

}
