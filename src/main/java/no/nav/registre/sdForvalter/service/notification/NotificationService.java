package no.nav.registre.sdForvalter.service.notification;

import java.util.Map;

import no.nav.registre.sdForvalter.database.model.Team;

public interface NotificationService {

    Map<String, Object> notifyTeam(Team team, Map<String, Object> data);
}
