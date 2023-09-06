package fr.army.stelyteam.menu.button;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.utils.builder.ItemBuilder;

public class ButtonItem {
    
    private final Material material;
    private final String name;
    private final int amount;
    private final List<String> lore;
    private final boolean glow;
    private final String skullTexture;

    public ButtonItem(Material material, String name, int amount, List<String> lore, boolean glow, String skullTexture) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.lore = lore;
        this.glow = glow;
        this.skullTexture = skullTexture;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean isGlow() {
        return glow;
    }

    public String getSkullTexture() {
        return skullTexture;
    }

    public ItemStack build(){
        return ItemBuilder.getItem(this);
    }
}
