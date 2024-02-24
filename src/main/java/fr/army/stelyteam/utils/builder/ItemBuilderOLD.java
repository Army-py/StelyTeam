package fr.army.stelyteam.utils.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.button.ButtonItem;


public class ItemBuilderOLD {

	public static ItemStack getItem(ButtonItem buttonItem){
		return getItem(buttonItem.getMaterial(), buttonItem.getName(), buttonItem.getLore(), buttonItem.getSkullTexture(), buttonItem.isGlow());
	}

	public static ItemStack getItem(Material material, String displayName, List<String> lore, String headTexture, boolean isEnchanted) {
		if (material.equals(Material.PLAYER_HEAD) && !headTexture.isBlank()) return getCustomHead(headTexture, displayName, lore, null);

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

		// NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "buttonName");
		// meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, buttonName);

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		return item;
	}


	public static ItemStack getPlayerHead(UUID uuid, String name, List<String> lore) {
		return getCustomHead(null, name, lore, uuid);
	}


	private static ItemStack getCustomHead(String texture, String name, List<String> lore, UUID uuid) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);

		// PlayerProfile pprofile = Bukkit.createPlayerProfile(uuid);
		// pprofile.getTextures().set

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
		GameProfile profile = new GameProfile(uuid == null ? UUID.randomUUID() : uuid,
				uuid == null ? null : Bukkit.getOfflinePlayer(uuid).getName());

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

		if (uuid != null){
			NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "uuid");
			skullMeta.getPersistentDataContainer().set(key, PersistentDataType.LONG_ARRAY, new long[]{uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()});
		}

		// if (buttonName != null){
		// 	NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "buttonName");
		// 	skullMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, buttonName);
		// }

		skullMeta.setDisplayName(name);
        item.setItemMeta(skullMeta);
        return item;
	}
}
