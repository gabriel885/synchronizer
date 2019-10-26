package synchronizer.models.diff;

public enum DeltaType {
    INSERT,
    CHANGE,
    DELETE,
    EQUAL, // checksum must match
}
