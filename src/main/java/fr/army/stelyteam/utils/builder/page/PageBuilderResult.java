package fr.army.stelyteam.utils.builder.page;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PageBuilderResult<C> {

    private final List<List<C>> pages;

    public PageBuilderResult(List<List<C>> pages) {
        this.pages = pages;
    }

    public List<List<C>> getPages() {
        return pages;
    }

    @Nullable
    public List<C> getPage(int page) {
        try {
            return pages.get(page);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
