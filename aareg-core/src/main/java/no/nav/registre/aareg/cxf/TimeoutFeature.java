package no.nav.registre.aareg.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public class TimeoutFeature extends AbstractFeature {

    private final int receiveTimeout;
    private final int connectionTimeout;

    public TimeoutFeature(int receiveTimeout, int connectionTimeout) {
        this.receiveTimeout = receiveTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public void initialize(Client client, Bus bus) {
        Conduit conduit = client.getConduit();
        if (conduit instanceof HTTPConduit) {
            HTTPConduit httpConduit = (HTTPConduit) conduit;
            if (httpConduit.getClient() == null) {
                HTTPClientPolicy policy = new HTTPClientPolicy();
                policy.setReceiveTimeout(receiveTimeout);
                policy.setConnectionTimeout(connectionTimeout);
                httpConduit.setClient(policy);
            } else {
                httpConduit.getClient().setReceiveTimeout(receiveTimeout);
                httpConduit.getClient().setConnectionTimeout(connectionTimeout);
            }
        }
        super.initialize(client, bus);
    }
}