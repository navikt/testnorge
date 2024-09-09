package no.nav.testnav.apps.statusfrontend.slack;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertsInfoContributor implements InfoContributor {

    private final SlackService slack;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("slackAlerts", slack.getCurrentAlerts());
    }

}
