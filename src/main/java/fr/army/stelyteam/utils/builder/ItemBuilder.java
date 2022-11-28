package fr.army.stelyteam.utils.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;


public class ItemBuilder {
	public static ItemStack getItem(Material material, String name, List<String> lore, String headTexture, boolean isEnchanted) {
		if (material.equals(Material.PLAYER_HEAD) && !headTexture.isBlank()) return getCustomHead(headTexture, name, lore, null);

		ItemStack item = new ItemStack(material, 1);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if(!lore.isEmpty()) {
			List<String> loreList = (List<String>) lore;
			meta.setLore(loreList);
		}

		if (isEnchanted) {
			meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		}
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}


	public static ItemStack getPlayerHead(OfflinePlayer player, String name, List<String> lore) {
		// ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		// SkullMeta meta = (SkullMeta) item.getItemMeta();
		// if (player != null) meta.setOwningPlayer(player);
		// meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		// meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		// meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		// meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		// meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		// meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		// if(!lore.isEmpty()) {
		// 	List<String> loreList = (List<String>) lore;
		// 	meta.setLore(loreList);
		// }

		// meta.setDisplayName(name);
		// item.setItemMeta(meta);
		// return item;
		return getCustomHead(null, name, lore, player);
	}


	private static ItemStack getCustomHead(String texture, String name, List<String> lore, OfflinePlayer player) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(player == null ? UUID.randomUUID() : player.getUniqueId(), player == null ? null : player.getName());

		if (texture != null) {
        	profile.getProperties().put("textures", new Property("textures", texture));
		}

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

		skullMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		skullMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		skullMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		skullMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		skullMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		skullMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if(!lore.isEmpty()) {
			List<String> loreList = (List<String>) lore;
			skullMeta.setLore(loreList);
		}

		skullMeta.setDisplayName(name);
        item.setItemMeta(skullMeta);
        return item;
	}
}
