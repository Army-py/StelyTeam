package fr.army.stelyteam.utils.builder.page;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PageBuilder {

    private static final PageBuilder INSTANCE = new PageBuilder();


    public static PageBuilder getInstance() {
        return INSTANCE;
    }


    public <C> PageBuilderResult<C> buildPage(@NotNull List<C> components, int componentPerPage) {
        final int size = components.size();
        final int page = (int) Math.ceil((double) size / componentPerPage);

        final List<List<C>> pages = new ArrayList<>(page);

        for (int i = 0; i < page; i++) {
            final int start = i * componentPerPage;
            final int end = Math.min(start + componentPerPage, size);

            pages.add(components.subList(start, end));
        }

        return new PageBuilderResult<>(pages);
    }


    public <C> PageBuilderResult<C> buildEmptyPage() {
        final List<List<C>> pages = new ArrayList<>();
        pages.add(new ArrayList<>(0));
        return new PageBuilderResult<>(pages);
    }
}
