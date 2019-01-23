package no.nav.registre.syntrest.globals;

import java.util.ArrayList;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;

@Slf4j
public class QueueHandler extends KubernetesUtils {
    private static QueueHandler queueHandler = null;
    private ArrayList<Integer> queue;

    private QueueHandler(){
        this.queue = new ArrayList<>();
    }

    public static QueueHandler getInstance() {
        if (queueHandler == null){
            queueHandler = new QueueHandler();
        }
        return queueHandler;
    }

    public ArrayList<Integer> getQueue(){
        return queue;
    }

    public void addToQueue(int queueId){
        queue.add(queueId);
    }

    public void removeFromQueue(int queueId, ApiClient client, String appName) throws ApiException {
        queue.remove(queueId);
        if (queue.size() == 0){
            log.info("Terminating " + appName);
            deleteApplication(client, appName);
        }
    }

    public int getQueueId(){
        return queue.size();
    }
}
