package no.nav.registre.testnorge.identservice.testdata.transformation;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
@FunctionalInterface
public interface TransformStrategy {

    boolean isSupported(Object o);

}
