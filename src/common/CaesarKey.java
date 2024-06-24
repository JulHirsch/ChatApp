package common;

public record CaesarKey(int shift) implements IKey {
    public CaesarKey(int shift) {
        this.shift = shift % 26;
    }
}
