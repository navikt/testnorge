package no.nav.registre.sdForvalter.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import no.nav.registre.sdForvalter.consumer.rs.SlackConsumer;
import no.nav.registre.sdForvalter.database.model.Team;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService implements NotificationService {

    private final SlackConsumer slackConsumer;

    @Override
    public Map<String, Object> notifyTeam(Team team, Map<String, Object> data) {
        return slackConsumer.post(team.getSlackKanalId(), data);
    }
}
