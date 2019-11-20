package synchronizer.models;

import java.util.HashSet;
import java.util.Set;

// represent vertx.SharedData local map address
public class SharedDataMapAddress {

    private static Set<String> registeredMaps = new HashSet<>();
    private String mapName;

    public SharedDataMapAddress(String mapName) {
        registeredMaps.add(mapName);
        this.mapName = mapName;
    }

    /**
     * returning the actual reference to registered SharedData maps
     *
     * @return
     */
    public Set<String> getRegisteredMaps() {
        return registeredMaps;
    }

    @Override
    public String toString() {
        return this.mapName;
    }
}
