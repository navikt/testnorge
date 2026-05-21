package no.nav.dolly.plugins;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.util.List;
import java.util.Optional;

final class MockitoAgentSupport {

    private static final String JAVA_BASE_OPEN = "java.base/java.lang=ALL-UNNAMED";
    private static final String MOCKITO_GROUP = "org.mockito";
    private static final String MOCKITO_CORE = "mockito-core";
    private static final String TEST_RUNTIME_CLASSPATH = "testRuntimeClasspath";

    private MockitoAgentSupport() {
    }

    static void configureTests(Project project) {
        project
                .getTasks()
                .withType(Test.class)
                .configureEach(test -> {
                    test.useJUnitPlatform();
                    test.jvmArgs("--add-opens", JAVA_BASE_OPEN);
                    test.getJvmArgumentProviders().add(() -> findMockitoAgent(project)
                            .map(MockitoAgentSupport::toJavaAgentArgument)
                            .map(List::of)
                            .orElseGet(List::of));
                });
    }

    private static String toJavaAgentArgument(File file) {
        return "-javaagent:" + file.getAbsolutePath();
    }

    private static Optional<File> findMockitoAgent(Project project) {
        return project
                .getConfigurations()
                .getByName(TEST_RUNTIME_CLASSPATH)
                .getResolvedConfiguration()
                .getResolvedArtifacts()
                .stream()
                .filter(MockitoAgentSupport::isMockitoCoreArtifact)
                .map(ResolvedArtifact::getFile)
                .findFirst();
    }

    private static boolean isMockitoCoreArtifact(ResolvedArtifact artifact) {
        var moduleId = artifact.getModuleVersion().getId();
        return MOCKITO_GROUP.equals(moduleId.getGroup()) && MOCKITO_CORE.equals(moduleId.getName());
    }

}

