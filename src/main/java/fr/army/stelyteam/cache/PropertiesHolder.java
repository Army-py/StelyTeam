package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface PropertiesHolder {

    @NotNull UUID getId();

    void save(@NotNull List<SaveProperty<?>> saveValues);

}
