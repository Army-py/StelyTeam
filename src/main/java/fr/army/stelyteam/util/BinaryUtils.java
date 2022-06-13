package fr.army.stelyteam.util;

import java.util.UUID;

public class BinaryUtils {

    /**
     * Get the {@code int} from the bit representation stored in a byte array
     *
     * @param array  The byte array that contains the bit representation
     * @param offset The index where the bit representation starts
     * @return An {@code int} with the given bit representation
     */
    public static int toInt(byte[] array, int offset) {
        int i = 0;
        for (int index = offset; index < Integer.BYTES + offset; ++index) {
            i = (i << 8) | (array[index] & 0xFF);
        }
        return i;
    }

    /**
     * Get the {@code long} from the bit representation stored in a byte array
     *
     * @param array  The byte array that contains the bit representation
     * @param offset The index where the bit representation starts
     * @return A {@code long} with the given bit representation
     */
    public static long toLong(byte[] array, int offset) {
        long l = 0;
        for (int index = offset; index < Long.BYTES + offset; ++index) {
            l = (l << 8) | (array[index] & 0xFF);
        }
        return l;
    }

    /**
     * Get the {@link UUID} from the bit representation stored in a byte array
     *
     * @param array  The byte array that contains the bit representation
     * @param offset The index where the bit representation starts
     * @return An {@link UUID} with the given bit representation
     */
    public static UUID toUUID(byte[] array, int offset) {
        return new UUID(toLong(array, offset), toLong(array, offset + Long.BYTES));
    }

    /**
     * Transfer the bit representation of an {@code int} into a byte array
     *
     * @param i      The {@code int} to put in the array
     * @param output The byte array that will store the bit representation
     * @param offset The index where the bit representation must start
     */
    public static void toByteArray(int i, byte[] output, int offset) {
        if (output.length < offset + Integer.BYTES) {
            throw new IndexOutOfBoundsException();
        }
        for (int index = 0; index < Integer.BYTES; ++index) {
            output[index + offset] = (byte) ((i >>> ((Integer.BYTES - index - 1) << 3)) & 0xFF);
        }
    }

    /**
     * Transfer the bit representation of a {@code long} into a byte array
     *
     * @param l      The {@code long} to put in the array
     * @param output The byte array that will store the bit representation
     * @param offset The index where the bit representation must start
     */
    public static void toByteArray(long l, byte[] output, int offset) {
        if (output.length < offset + Long.BYTES) {
            throw new IndexOutOfBoundsException();
        }
        for (int index = 0; index < Long.BYTES; ++index) {
            output[index + offset] = (byte) ((l >>> ((Long.BYTES - index - 1) << 3)) & 0xFF);
        }
    }

    /**
     * Transfer the bit representation of an {@link UUID} into a byte array
     *
     * @param uuid   The {@link UUID} to put in the array
     * @param output The byte array that will store the bit representation
     * @param offset The index where the bit representation must start
     */
    public static void toByteArray(UUID uuid, byte[] output, int offset) {
        toByteArray(uuid.getMostSignificantBits(), output, offset);
        toByteArray(uuid.getLeastSignificantBits(), output, offset + Long.BYTES);
    }

}
