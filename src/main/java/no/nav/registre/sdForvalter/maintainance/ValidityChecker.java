package no.nav.registre.sdForvalter.maintainance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.Team;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.model.Varighet;
import no.nav.registre.sdForvalter.database.repository.VarighetRepository;
import no.nav.registre.sdForvalter.service.StaticDataService;
import no.nav.registre.sdForvalter.service.notification.SlackNotificationService;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidityChecker {

    private final StaticDataService staticDataService;
    private final SlackNotificationService slackNotificationService;
    private final VarighetRepository varighetRepository;

    @Scheduled(cron = "0 0 0 * * ? *")
    public void validatePeriods() {
        Map<String, Object> notficiationData = new HashMap<>();
        Set<Team> teams = staticDataService.getAllTeams();
        for (Team team : teams) {
            for (Varighet varighet : team.getVarigheter()) {
                if (varighet.shouldNotify() && !varighet.isHasNotified()) {
                    Map<String, Object> data = buildMessageData(varighet);
                    try {
                        Map<String, Object> responseMessage = slackNotificationService.notifyTeam(team, data);
                        if (responseMessage != null) {
                            varighet.setHasNotified(true);
                            varighetRepository.save(varighet);
                        }
                        notficiationData.put(team.getNavn(), responseMessage);
                    } catch (HttpStatusCodeException e) {
                        log.error(e.getMessage(), e);
                    }
                } else if (varighet.shouldDelete()) {
                    varighetRepository.delete(varighet);
                }
            }
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info(mapper.writeValueAsString(notficiationData));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, Object> buildMessageData(Varighet varighet) {
        Map<String, Object> data = new HashMap<>();
        Collection<TpsModel> tps = varighet.getTps();
        Collection<AaregModel> aareg = varighet.getAareg();
        Collection<KrrModel> krr = varighet.getKrr();
        Collection<EregModel> ereg = varighet.getEreg();
        if (tps != null && !tps.isEmpty())
            data.put("tps", tps);
        if (tps != null && !aareg.isEmpty())
            data.put("aareg", aareg);
        if (tps != null && !krr.isEmpty())
            data.put("krr", krr);
        if (tps != null && !ereg.isEmpty())
            data.put("ereg", ereg);

        return data;
    }
}
