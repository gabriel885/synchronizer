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

    public Set<String> getRegisteredAddresses(){ return registeredAddresses; }

    @Override
    public String toString(){
        return this.address;
    }
}
