package no.nav.registre.testnorge.libs.analysis;

import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.analysis.domain.Dependency;


public class DependencyAnalysis {
    private final Reflections reflections;

    public DependencyAnalysis(String basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<Dependency> analyze() {

        Set<Class<?>> classesDependencyOn = reflections.getTypesAnnotatedWith(DependencyOn.class);

        Set<DependencyOn> dependencies = new HashSet<>(classesDependencyOn
                .stream()
                .map(value -> value.getAnnotation(DependencyOn.class))
                .collect(Collectors.toSet()));

        Set<Class<?>> classesDependenciesOn = reflections.getTypesAnnotatedWith(DependenciesOn.class);

        dependencies.addAll(classesDependenciesOn
                .stream()
                .map(value -> value.getAnnotation(DependenciesOn.class))
                .map(value -> Arrays.asList(value.value()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
        );
        return dependencies.stream().map(Dependency::new).collect(Collectors.toSet());
    }
}
