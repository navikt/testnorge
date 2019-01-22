package no.nav.registre.syntrest.globals;

import java.util.ArrayList;

public class QueueHandler {
    private static QueueHandler queueHandler = null;
    private ArrayList<Integer> queue;

    public QueueHandler getInstance() {
        if (queueHandler == null){
            queueHandler = new QueueHandler();
        }
        return queueHandler;
    }

    private ArrayList<Integer> getQueue(){
        return queue;
    }

    private void addToQueue(int queueId){
        queue.add(queueId);
    }

    private void removeFromQueue(int queueId, String appName){
        queue.remove(queueId);
        if (queue.size() == 0){
            //TODO insert code for close kubernetes pod
        }
    }

    private int getQueueId(){
        return queue.size();
    }
}
