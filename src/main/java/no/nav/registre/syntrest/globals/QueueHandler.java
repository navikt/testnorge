package no.nav.registre.syntrest.globals;

import java.util.ArrayList;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;

@Slf4j
public class QueueHandler extends KubernetesUtils {
    private ArrayList<Integer> queue;

    public QueueHandler(){
        this.queue = new ArrayList<>();
    }

    public ArrayList<Integer> getQueue(){
        return queue;
    }

    public void addToQueue(int queueId){
        ArrayList<Integer> newList  = new ArrayList<>();
        for (int i : queue){
            newList.add(i);
        }
        newList.add(queueId);
        this.queue = newList;
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
