package fr.army.stelyteam.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class SerializeManager {
    
    public String itemStackToBase64(ItemStack itemStack) {
        try {
            final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(itemStack);
            return Base64Coder.encodeLines(arrayOutputStream.toByteArray());
        } catch (final Exception exception) {
            throw new RuntimeException("Error turning ItemStack into base64", exception);
        }
    }


    public ItemStack itemStackFromBase64(String base64) {
        try {
            final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            final BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);
            return (ItemStack) objectInputStream.readObject();
        } catch (final Exception exception) {
            throw new RuntimeException("Error turning base64 into ItemStack", exception);
        }
    }
}
