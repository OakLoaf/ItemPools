package org.lushplugins.itempools.hook;

public enum HookId {
    FANCY_HOLOGRAMS("fancy-holograms"),
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