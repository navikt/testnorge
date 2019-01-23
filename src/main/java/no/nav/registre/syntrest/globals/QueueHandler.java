package no.nav.registre.syntrest.globals;

import java.util.ArrayList;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;

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
            deleteApplication(client, appName);
        }
    }

    public int getQueueId(){
        return queue.size();
    }
}
