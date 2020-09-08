package no.nav.registre.testnorge.libs.dependencyanalysis;

import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dependencyanalysis.domain.ApplicationDependencies;

public class DependencyAnalysis {

    private final String applicationName;
    private final Reflections reflections;

    public DependencyAnalysis(String applicationName, String basePackage) {
        this.applicationName = applicationName;
        reflections = new Reflections(basePackage);
    }

    public ApplicationDependencies analyze() {

        Set<DependencyOn> set = new HashSet<>();

        Set<Class<?>> classesDependencyOn = reflections.getTypesAnnotatedWith(DependencyOn.class);

        set.addAll(classesDependencyOn
                .stream()
                .map(value -> value.getAnnotation(DependencyOn.class))
                .collect(Collectors.toSet())
        );

        Set<Class<?>> classesDependenciesOn = reflections.getTypesAnnotatedWith(DependenciesOn.class);

        set.addAll(classesDependenciesOn
                .stream()
                .map(value -> value.getAnnotation(DependenciesOn.class))
                .map(value -> Arrays.asList(value.value()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
        );

        return new ApplicationDependencies(applicationName, set);

    }
}
