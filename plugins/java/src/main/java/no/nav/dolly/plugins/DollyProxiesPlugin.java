package no.nav.dolly.plugins;

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.plugins.ide.idea.model.IdeaModel;

import java.util.HashMap;

@SuppressWarnings("unused")
public class DollyProxiesPlugin implements Plugin<Project> {

    @Override
    @SuppressWarnings("Duplicates") // We're intentionally explicit, even if it means duplicating code.
    public void apply(Project project) {

        var pluginManager = project.getPluginManager();
        pluginManager.apply(DollySonarPlugin.class);
        pluginManager.apply(DollyVersionsPlugin.class);
        pluginManager.apply(DollyBuildValidationPlugin.class);

        var versions = (DollyVersionsPlugin.DollyVersionCatalog) project.getExtensions().getByName("versions");

        var plugins = project.getPlugins();
        plugins.apply("idea");
        plugins.apply("io.spring.dependency-management");
        plugins.apply(JavaPlugin.class);
        plugins.apply("org.springframework.boot");

        project.setGroup("no.nav.testnav.proxies");

        var idea = project.getExtensions().getByType(IdeaModel.class);
        idea.getModule().setDownloadJavadoc(true);
        idea.getModule().setDownloadSources(true);

        var java = project.getExtensions().getByType(JavaPluginExtension.class);
        java.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(21));

        var configurations = project.getConfigurations();
        var compileOnlyConfig = configurations.maybeCreate("compileOnly");
        var annotationProcessorConfig = configurations.maybeCreate("annotationProcessor");
        compileOnlyConfig.extendsFrom(annotationProcessorConfig);

        var dependencies = project.getDependencies();
        dependencies.add("annotationProcessor", "org.projectlombok:lombok");
        dependencies.add("annotationProcessor", "org.springframework.boot:spring-boot-configuration-processor");
        dependencies.add("compileOnly", "org.projectlombok:lombok");
        dependencies.add("developmentOnly", "org.springframework.boot:spring-boot-devtools");
        dependencies.add("implementation", "com.google.cloud:spring-cloud-gcp-starter-secretmanager:" + versions.gcpSecretManager);
        dependencies.add("implementation", "net.logstash.logback:logstash-logback-encoder:" + versions.logback);
        dependencies.add("implementation", "org.glassfish.expressly:expressly:" + versions.expressly);
        dependencies.add("implementation", "org.hibernate.validator:hibernate-validator");
        dependencies.add("implementation", "org.projectlombok:lombok");
        dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator");
        dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-oauth2-resource-server");
        dependencies.add("implementation", "org.springframework.cloud:spring-cloud-starter-gateway-server-webflux");
        dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-undertow");
        var springBootStarterWebflux = (ModuleDependency) dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-webflux");
        if (springBootStarterWebflux != null) {
            var springBootStarterReactorNettyExclusion = new HashMap<String, String>();
            springBootStarterReactorNettyExclusion.put("group", "org.springframework.boot");
            springBootStarterReactorNettyExclusion.put("module", "spring-boot-starter-reactor-netty");
            springBootStarterWebflux.exclude(springBootStarterReactorNettyExclusion);
        }
        dependencies.add("runtimeOnly", "io.grpc:grpc-netty:" + versions.grpc);
        dependencies.add("runtimeOnly", "io.micrometer:micrometer-registry-prometheus");
        dependencies.add("testAnnotationProcessor", "org.projectlombok:lombok");
        dependencies.add("testImplementation", project.getDependencies().platform("org.junit:junit-bom:" + versions.junit));
        dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test");
        dependencies.add("testImplementation", "org.springframework.cloud:spring-cloud-contract-wiremock");
        dependencies.add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher");

        var dependencyManagement = project.getExtensions().getByType(DependencyManagementExtension.class);
        dependencyManagement.setApplyMavenExclusions(false);
        dependencyManagement.imports(imports -> {
            imports.mavenBom("org.springframework.boot:spring-boot-dependencies:" + versions.springBoot);
            imports.mavenBom("org.springframework.cloud:spring-cloud-dependencies:" + versions.springCloud);
            imports.mavenBom("org.springframework.session:spring-session-bom:" + versions.springSession);
        });

        project.getTasks().withType(Test.class).configureEach(test -> {
            test.useJUnitPlatform();
            test.jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED");
            test.doFirst(task -> project
                    .getConfigurations()
                    .getByName("testRuntimeClasspath")
                    .getResolvedConfiguration()
                    .getResolvedArtifacts()
                    .stream()
                    .map(ResolvedArtifact::getFile)
                    .filter(f -> f.getName().contains("byte-buddy-agent"))
                    .findFirst()
                    .ifPresent(file -> test.getJvmArgs().add("-javaagent:" + file)));
        });

        project.afterEvaluate(p -> {
            var bootJarTask = p.getTasks().findByName("bootJar");
            if (bootJarTask != null) {
                var bootJar = (Jar) bootJarTask;
                bootJar.getArchiveFileName().set("app.jar");
                bootJar.setDuplicatesStrategy(DuplicatesStrategy.WARN);
            }
        });

        //noinspection ExtractMethodRecommender
        var repositories = project.getRepositories();
        repositories.mavenCentral();
        repositories.maven(maven -> {
            maven.setName("GitHub Packages");
            maven.setUrl("https://maven.pkg.github.com/navikt/maven-release");
            maven.credentials(creds -> {
                creds.setUsername("token");
                creds.setPassword(System.getenv("NAV_TOKEN"));
            });
        });
        repositories.maven(maven -> {
            maven.setName("Confluent");
            maven.setUrl("https://packages.confluent.io/maven/");
        });
        repositories.maven(maven -> {
            maven.setName("Shibboleth");
            maven.setUrl("https://build.shibboleth.net/maven/releases/");
        });
        repositories.mavenLocal();

    }

}





