package fr.army.stelyteam.team;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Rank {
    
    private final int inversedPriority;
    private final String name;
    private final ItemStack itemStack;


    public Rank(@NotNull int inversedPriority, @NotNull String name, @NotNull ItemStack itemStack){
        this.inversedPriority = inversedPriority;
        this.name = name;
        this.itemStack = itemStack;
    }

    public int getInversedPriority() {
        return inversedPriority;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
