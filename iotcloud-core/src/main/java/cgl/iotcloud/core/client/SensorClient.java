package cgl.iotcloud.core.client;

import cgl.iotcloud.core.Configuration;
import cgl.iotcloud.core.api.thrift.*;
import cgl.iotcloud.core.master.SensorSiteDescriptor;
import cgl.iotcloud.core.sensorsite.SensorDeployDescriptor;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.List;
import java.util.Map;

public class SensorClient {
    private TMasterAPIService.Client client;

    private Map conf;

    public SensorClient(Map conf) {
        this.conf = conf;

        String host = Configuration.getMasterHost(conf);
        int port = Configuration.getMasterAPIPort(conf);

        TTransport transport = new TSocket(host, port);
        TProtocol protocol = new TBinaryProtocol(transport);

        this.client = new TMasterAPIService.Client(protocol);
    }

    public List<String> getSensorSites() {
        try {
            return this.client.getSites();
        } catch (TException e) {
            throw new RuntimeException("Error occurred while getting the registered sites", e);
        }
    }

    public SensorSiteDescriptor getSensorSite(String id) {
        try {
            TSiteDetailsResponse response = this.client.getSite(id);
            if (response.getState().getState() == TResponseState.SUCCESS) {
                TSiteDetails details = response.getDetails();
                SensorSiteDescriptor siteDescriptor =
                        new SensorSiteDescriptor(details.getSiteId(), details.getPort(), details.getHost());
                return siteDescriptor;
            }
            return null;
        } catch (TException e) {
            throw new RuntimeException("Error occurred while getting the registered sites", e);
        }
    }

    public boolean deploySensor(SensorDeployDescriptor deployDescriptor) {
        TSensorDetails sensorDetails = new TSensorDetails(deployDescriptor.getJarName(), deployDescriptor.getClassName());
        try {
            TResponse response = client.deploySensor(deployDescriptor.getDeploySites(), sensorDetails);
            return response.getState() == TResponseState.SUCCESS;
        } catch (TException e) {
            throw new RuntimeException("Failed to deploy the sensor", e);
        }
    }


}
