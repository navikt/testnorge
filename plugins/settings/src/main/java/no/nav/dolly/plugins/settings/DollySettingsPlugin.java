package no.nav.dolly.plugins.settings;

import com.gradle.develocity.agent.gradle.DevelocityConfiguration;
import com.gradle.develocity.agent.gradle.DevelocityPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

public class DollySettingsPlugin implements Plugin<Settings> {

    @Override
    public void apply(Settings settings) {

        settings.getPluginManager().apply(DevelocityPlugin.class);

        var isCi = settings
                .getProviders()
                .environmentVariable("CI")
                .isPresent();

        settings
                .getExtensions()
                .getByType(DevelocityConfiguration.class)
                .buildScan(buildScan -> {
                    buildScan.getTermsOfUseUrl().set("https://gradle.com/terms-of-service");
                    buildScan.getTermsOfUseAgree().set("yes");
                    buildScan.getPublishing().onlyIf(context -> isCi);
                });

    }

}
