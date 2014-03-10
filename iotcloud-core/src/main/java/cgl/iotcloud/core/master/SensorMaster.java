package cgl.iotcloud.core.master;

import cgl.iotcloud.core.Utils;
import cgl.iotcloud.core.master.store.InMemorySensorData;
import cgl.iotcloud.core.master.thrift.TMasterService;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;

public class SensorMaster {
    private static Logger LOG = LoggerFactory.getLogger(SensorMaster.class);

    public static void main(String[] args) {
        // read the configuration file
        Map conf = Utils.readConfig();

        // configures the sensor store
        InMemorySensorData sensorStore = new InMemorySensorData();

        // now start the server to listen for the sites



        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(1205);
            THsHaServer server = new THsHaServer(
                    new THsHaServer.Args(serverTransport).processor(
                            new TMasterService.Processor <MasterServiceHandler>(new MasterServiceHandler())).executorService(Executors.newFixedThreadPool(10)));
            server.serve();
        } catch (TTransportException e) {
            String msg = "Error starting the Thrift server";
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }
}