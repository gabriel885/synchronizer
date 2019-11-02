package synchronizer.models;

import io.vertx.core.VertxException;

import java.util.HashSet;
import java.util.Set;

// represent EventBus address
public class EventBusAddress {

    private String address;
    private static Set<String> registeredAddresses = new HashSet<>();

    public EventBusAddress(String address){
        registeredAddresses.add(address);
        this.address = address;
    }

    /**
     * get registered event bus addresses
     * @return
     */
    public Set<String> getRegisteredAddresses(){ return registeredAddresses; }

    /**
     *
     * @param address
     * @return true if address is registered as event bus address or not
     */
    public boolean isRegistered(String address){
        return registeredAddresses.contains(address);
    }

    /**
     *
     * @return event bus address as string
     */
    @Override
    public String toString(){
        return this.address;
    }
}
