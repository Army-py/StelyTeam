package fr.army.stelyteam.utils.manager.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemStackSerializer {
    
    public String serialize(ItemStack[] itemStack) {
        try {
            final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(itemStack);
            return Base64Coder.encodeLines(arrayOutputStream.toByteArray());
            // return new BigInteger(1, arrayOutputStream.toByteArray()).toString(64);
        } catch (final Exception exception) {
            throw new RuntimeException("Error turning ItemStack into base64", exception);
        }
    }


    public ItemStack[] deserialize(String base64) {
        try {
            final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            // final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(new BigInteger(base64, 64).toByteArray());
            final BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);
            return (ItemStack[]) objectInputStream.readObject();
        } catch (final Exception exception) {
            throw new RuntimeException("Error turning base64 into ItemStack", exception);
        }
    }
}
