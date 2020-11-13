package no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition;


import no.nav.registre.testnorge.identservice.testdata.config.TpsRequestConfig;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsParameter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsParameterType;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.RequestTransformer;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.ResponseTransformer;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.Transformer;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TpsServiceRoutineDefinitionBuilder {

    public static final String REQUIRED = "required";
    private String name;
    private String internalName;
    private Class<?> javaClass;
    private List<TpsParameter> parameters = new ArrayList<>();
    private List<Transformer> transformers = new ArrayList<>();
    private TpsRequestConfig requestConfig;

    public TransformerBuilder transformer() {
        return new TransformerBuilder();
    }

    public TpsRequestConfigBuilder config() {
        return new TpsRequestConfigBuilder();
    }

    public TpsServiceRoutineDefinitionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TpsServiceRoutineDefinitionBuilder internalName(String internalName) {
        this.internalName = internalName;
        return this;
    }

    public TpsServiceRoutineDefinitionBuilder javaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
        return this;
    }

    public TpsServiceRoutineParameterBuilder parameter() {
        return new TpsServiceRoutineParameterBuilder();
    }

    public TpsServiceRoutineSecurityBuilder securityBuilder() {
        return new TpsServiceRoutineSecurityBuilder();
    }

    public TpsServiceRoutineDefinitionRequest build() {
        TpsServiceRoutineDefinitionRequest routine = new TpsServiceRoutineDefinitionRequest();
        routine.setName(name);
        routine.setInternalName(internalName);
        routine.setJavaClass(javaClass);
        routine.setParameters(parameters);
        routine.setTransformers(transformers);
        routine.setConfig(requestConfig);
        return routine;
    }

    public static TpsServiceRoutineDefinitionBuilder aTpsServiceRoutine() {
        return new TpsServiceRoutineDefinitionBuilder();
    }

    public class TpsServiceRoutineSecurityBuilder {

    }

    public class TpsServiceRoutineParameterBuilder {

        private String name;
        private TpsParameterType type;
        private String use;
        private List<String> values = new ArrayList<>();

        public TpsServiceRoutineParameterBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TpsServiceRoutineParameterBuilder type(TpsParameterType type) {
            this.type = type;
            return this;
        }

        public TpsServiceRoutineParameterBuilder required() {
            this.use = REQUIRED;
            return this;
        }

        public TpsServiceRoutineParameterBuilder values(String... values) {
            this.values.addAll(asList(values));
            return this;
        }

        public TpsServiceRoutineDefinitionBuilder and() {
            TpsParameter param = new TpsParameter();
            param.setName(name);
            param.setType(type);
            param.setUse(use);
            param.setValues(values);
            TpsServiceRoutineDefinitionBuilder.this.parameters.add(param);
            return TpsServiceRoutineDefinitionBuilder.this;
        }
    }

    public class TransformerBuilder {

        private List<Transformer> transformers;

        TransformerBuilder() {
            this.transformers = new ArrayList<>();
        }

        public TransformerBuilder preSend(RequestTransformer transformer) {
            transformers.add(transformer);
            return this;
        }

        public TransformerBuilder postSend(ResponseTransformer transformer) {
            transformers.add(transformer);
            return this;
        }

        public TpsServiceRoutineDefinitionBuilder and() {
            TpsServiceRoutineDefinitionBuilder.this.transformers.addAll(transformers);
            return TpsServiceRoutineDefinitionBuilder.this;
        }
    }

    public class TpsRequestConfigBuilder {

        private TpsRequestConfig config;

        TpsRequestConfigBuilder() {
            this.config = new TpsRequestConfig();
        }

        public TpsRequestConfigBuilder requestQueue(String requestQueue) {
            config.setRequestQueue(requestQueue);
            return this;
        }

        public TpsServiceRoutineDefinitionBuilder and() {
            TpsServiceRoutineDefinitionBuilder.this.requestConfig = config;
            return TpsServiceRoutineDefinitionBuilder.this;
        }
    }

}
