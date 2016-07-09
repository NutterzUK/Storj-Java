package storj.io.restclient.model;

/**
 * Created by Stephen Nutbrown on 03/07/2016.
 */
public class Contact {

    private String protocol;
    private String address;
    private int port;
    private String lastSeen;
    private String nodeID;


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeId) {
        this.nodeID = nodeId;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "protocol='" + protocol + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", lastSeen='" + lastSeen + '\'' +
                ", nodeID='" + nodeID + '\'' +
                '}';
    }
}
