package no.nav.dolly.plugins;

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.plugins.ide.idea.model.IdeaModel;

@SuppressWarnings("unused")
public class DollyLibsPlugin implements Plugin<Project> {

    @Override
    @SuppressWarnings("Duplicates") // We're intentionally explicit, even if it means duplicating code.
    public void apply(Project project) {

        var pluginManager = project.getPluginManager();
        pluginManager.apply(DollySonarPlugin.class);
        pluginManager.apply(DollyVersionsPlugin.class);

        var versions = (DollyVersionsPlugin.DollyVersionCatalog) project.getExtensions().getByName("versions");

        var plugins = project.getPlugins();
        plugins.apply("idea");
        plugins.apply("io.spring.dependency-management");
        plugins.apply(JavaLibraryPlugin.class);

        project.setGroup("no.nav.testnav.libs");

        var idea = project.getExtensions().getByType(IdeaModel.class).getModule();
        idea.setDownloadJavadoc(true);
        idea.setDownloadSources(true);

        var java = project.getExtensions().getByType(JavaPluginExtension.class);
        java.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(21));

        var configurations = project.getConfigurations();
        var compileOnlyConfig = configurations.maybeCreate("compileOnly");
        var annotationProcessorConfig = configurations.maybeCreate("annotationProcessor");
        compileOnlyConfig.extendsFrom(annotationProcessorConfig);

        var dependencyManagement = project.getExtensions().getByType(DependencyManagementExtension.class);
        dependencyManagement.setApplyMavenExclusions(false);
        dependencyManagement.imports(imports -> {
            imports.mavenBom("org.springframework.boot:spring-boot-dependencies:" + versions.springBoot);
            imports.mavenBom("org.springframework.cloud:spring-cloud-dependencies:" + versions.springCloud);
            imports.mavenBom("org.testcontainers:testcontainers-bom:" + versions.testcontainers);
        });

        project.getTasks().withType(JavaCompile.class).configureEach(task ->
                task.getOptions().getCompilerArgs().add("-parameters")
        );

        var dependencies = project.getDependencies();
        dependencies.add("annotationProcessor", "org.projectlombok:lombok");
        dependencies.add("annotationProcessor", "org.springframework.boot:spring-boot-configuration-processor");
        dependencies.add("implementation", "org.projectlombok:lombok");
        dependencies.add("testAnnotationProcessor", "org.projectlombok:lombok");
        dependencies.add("testImplementation", project.getDependencies().platform("org.junit:junit-bom:" + versions.junit));
        dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test");
        dependencies.add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher");

        project
                .getTasks()
                .withType(Test.class)
                .configureEach(test -> {
                    test.useJUnitPlatform();
                    test.doFirst(task -> {
                        var jvmArgs = test.getJvmArgs();
                        jvmArgs.add("--add-opens");
                        jvmArgs.add("java.base/java.lang=ALL-UNNAMED");
                        project
                                .getConfigurations()
                                .getByName("testRuntimeClasspath")
                                .getResolvedConfiguration()
                                .getResolvedArtifacts()
                                .stream()
                                .map(ResolvedArtifact::getFile)
                                .filter(f -> f.getName().contains("byte-buddy-agent"))
                                .findFirst()
                                .ifPresent(file -> test.getJvmArgs().add("-javaagent:" + file));
                    });
                });

        var repositories = project.getRepositories();
        repositories.mavenCentral();
        repositories.maven(maven -> {
            maven.setName("Confluent");
            maven.setUrl("https://packages.confluent.io/maven/");
        });
        repositories.mavenLocal();

    }

}










