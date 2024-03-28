package me.dave.itempools.hook;

public enum HookId {
    PLACEHOLDER_API("placeholder-api");

    private final String id;

    HookId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}