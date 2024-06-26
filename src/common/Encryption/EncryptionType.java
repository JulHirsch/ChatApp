package common.Encryption;

public enum EncryptionType {
    NONE("None"),
    CAESAR("Caesar"),
    RSA("RSA");

    private final String displayName;

    EncryptionType(String displayName) {
        this.displayName = displayName;
    }

    public static EncryptionType fromString(String text) {
        for (EncryptionType type : EncryptionType.values()) {
            if (type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
