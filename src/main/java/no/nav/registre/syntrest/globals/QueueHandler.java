package no.nav.registre.syntrest.globals;

import java.util.ArrayList;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueueHandler extends KubernetesUtils {
    private ArrayList<Integer> queue;

    public QueueHandler(){
        this.queue = new ArrayList<>();
    }

    public ArrayList<Integer> getQueue(){
        return queue;
    }

    public void addToQueue(int queueId){
        queue.add(queueId);
    }

    public void removeFromQueue(int queueId, ApiClient client, String appName) throws ApiException {
        for (int i = 0; i < queue.size(); i++){
            if (queue.get(i) == queueId){
                queue.remove(i);
            }
        }
        if (queue.size() == 0){
            log.info("ID: " + queueId + " is terminating " + appName);
            deleteApplication(client, appName);
        }
    }

    public int getQueueId(){
        return queue.size();
    }
}
