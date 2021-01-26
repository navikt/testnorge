package no.nav.registre.testnorge.libs.analysis;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.analysis.domain.Dependency;

@Slf4j
public class DependencyAnalysis {
    private final Reflections reflections;

    public DependencyAnalysis(String basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<Dependency> analyze() {
        log.info("Finner annoterte avhenigheter...");

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

        log.info("Fant {} annoterte avhenigheter.", dependencies.size());
        return dependencies.stream().map(Dependency::new).collect(Collectors.toSet());
    }
}
