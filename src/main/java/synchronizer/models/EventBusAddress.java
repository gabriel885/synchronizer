package synchronizer.models;

import java.util.HashSet;
import java.util.Set;

// represent EventBus address
public class EventBusAddress {

    private static final Set<String> registeredAddresses = new HashSet<>();
    private final String address;

    public EventBusAddress(String address) {
        registeredAddresses.add(address);
        this.address = address;
    }

    /**
     * get registered event bus addresses
     *
     * @return set of registered addresses
     */
    public Set<String> getRegisteredAddresses() {
        return registeredAddresses;
    }

    /**
     * @param address - event bus address
     * @return true if address is registered as event bus address, otherwise return false
     */
    public boolean isRegistered(String address) {
        return registeredAddresses.contains(address);
    }

    /**
     * @return event bus address as string
     */
    @Override
    public String toString() {
        return this.address;
    }
}
