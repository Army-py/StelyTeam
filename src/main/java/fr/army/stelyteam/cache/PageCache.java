package fr.army.stelyteam.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PageCache {

    private final Map<UUID, Integer> cachedPages;

    public PageCache() {
        this.cachedPages = new HashMap<>();
    }

    public void addPage(UUID uuid, int page) {
        cachedPages.put(uuid, page);
    }

    public int getPage(UUID uuid) {
        return cachedPages.get(uuid);
    }

    public void removePage(UUID uuid) {
        cachedPages.remove(uuid);
    }

    public boolean hasPage(UUID uuid) {
        return cachedPages.containsKey(uuid);
    }

    public void clear() {
        cachedPages.clear();
    }
}
