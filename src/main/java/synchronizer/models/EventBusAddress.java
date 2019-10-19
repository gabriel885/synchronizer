package synchronizer.models;

import io.vertx.core.VertxException;

import java.util.HashSet;
import java.util.Set;

// represent EventBus address
public class EventBusAddress {

    private String address;
    private static Set<String> registeredAddresses = new HashSet<>();

    public EventBusAddress(String address){
        // check for registered addresses
        if (registeredAddresses.contains(address)){
            // TODO: Why new instantiation of the same address should cause exception?
            //throw new VertxException(String.format("Failed to register new EventBus address %s - already registered", address));
        }
        registeredAddresses.add(address);
        this.address = address;
    }
    @Override
    public String toString(){
        return this.address;
    }
}
