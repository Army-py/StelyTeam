package fr.army.stelyteam.utils.builder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(@NotNull Material material){
        this.item = new ItemStack(material);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder(@NotNull ItemStack item){
        this.item = new ItemStack(item);
        this.meta = this.item.getItemMeta();
    }


    public ItemBuilder setMaterial(@NotNull Material material){
        this.item.setType(material);
        return this;
    }

    public ItemBuilder setAmount(@NotNull int amount){
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(@Nullable String displayName){
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder setLore(@Nullable List<String> lore){
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder setGlow(@NotNull boolean isGlow){
        if (!isGlow) return this;
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setSkullTexture(@Nullable String texture){
        if (texture == null || texture.isBlank()) return this;
        GameProfile profile = new GameProfile(toUUID(texture), (String) null);
        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            Field mtd = meta.getClass().getDeclaredField("profile");
            mtd.setAccessible(true);
            mtd.set(meta, profile);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public ItemBuilder setPlayerHead(@Nullable OfflinePlayer player){
        GameProfile profile = new GameProfile(player.getUniqueId(), player.getName());

        try {
            Field mtd = meta.getClass().getDeclaredField("profile");
            mtd.setAccessible(true);
            mtd.set(meta, profile);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public ItemBuilder hideItemAttributes(){
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }


    public ItemStack buildItem(){
        this.item.setItemMeta(this.meta);
        return this.item;
    }


    private UUID toUUID(String input) {
        long val = input.hashCode();
        return new UUID(val, val);
    }
}