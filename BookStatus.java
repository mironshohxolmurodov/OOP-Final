public enum BookStatus {
    AVAILABLE,
    RESERVED,
    LOANED,
    LOST;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
