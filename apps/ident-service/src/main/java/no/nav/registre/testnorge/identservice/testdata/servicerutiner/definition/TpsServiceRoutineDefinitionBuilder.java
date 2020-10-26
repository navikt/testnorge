package no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition;


import no.nav.registre.testnorge.identservice.testdata.config.TpsRequestConfig;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsParameter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsParameterType;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization.ServiceRutineAuthorisationStrategy;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.Transformer;

import java.util.ArrayList;
import java.util.List;

public class TpsServiceRoutineDefinitionBuilder {

    public static final String REQUIRED = "required";
    private String name;
    private String internalName;
    private Class<?> javaClass;
    private List<TpsParameter> parameters = new ArrayList<>();
    private List<Transformer> transformers = new ArrayList<>();
    private TpsRequestConfig requestConfig;
    private List<ServiceRutineAuthorisationStrategy> securitySearchAuthorisationStrategies = new ArrayList<>();

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
        routine.setRequiredSecurityServiceStrategies(securitySearchAuthorisationStrategies);
        return routine;
    }

    public static TpsServiceRoutineDefinitionBuilder aTpsServiceRoutine() {
        return new TpsServiceRoutineDefinitionBuilder();
    }

    public class TpsServiceRoutineSecurityBuilder {

        private List<ServiceRutineAuthorisationStrategy> serviceStrategies = new ArrayList<>();

        public TpsServiceRoutineSecurityBuilder addRequiredSearchAuthorisationStrategy(ServiceRutineAuthorisationStrategy serviceStrategy) {
            this.serviceStrategies.add(serviceStrategy);
            return this;
        }

        public TpsServiceRoutineDefinitionBuilder addSecurity() {
            securitySearchAuthorisationStrategies = this.serviceStrategies;
            return TpsServiceRoutineDefinitionBuilder.this;
        }
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
    }

    public class TransformerBuilder {

        private List<Transformer> transformers;

        TransformerBuilder() {
            this.transformers = new ArrayList<>();
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
