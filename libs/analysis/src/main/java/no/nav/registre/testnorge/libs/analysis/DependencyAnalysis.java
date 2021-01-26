package no.nav.registre.testnorge.libs.analysis;

import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.analysis.domain.NaisDependency;


public class DependencyAnalysis {
    private final Reflections reflections;

    public DependencyAnalysis(String basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<NaisDependency> analyze() {

        Set<Class<?>> classesDependencyOn = reflections.getTypesAnnotatedWith(NaisDependencyOn.class);

        Set<NaisDependencyOn> dependencies = new HashSet<>(classesDependencyOn
                .stream()
                .map(value -> value.getAnnotation(NaisDependencyOn.class))
                .collect(Collectors.toSet()));

        Set<Class<?>> classesDependenciesOn = reflections.getTypesAnnotatedWith(NaisDependenciesOn.class);

        dependencies.addAll(classesDependenciesOn
                .stream()
                .map(value -> value.getAnnotation(NaisDependenciesOn.class))
                .map(value -> Arrays.asList(value.value()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
        );
        return dependencies.stream().map(NaisDependency::new).collect(Collectors.toSet());
    }
}
