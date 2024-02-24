package fr.army.stelyteam.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Rank;
import fr.army.stelyteam.utils.builder.ItemBuilder;

public class Config {

    private final YamlConfiguration config;
    
    public static String databaseType;
    public static String mysqlHost;
    public static String mysqlDatabase;
    public static String mysqlUser;
    public static String mysqlPassword;
    public static String sqliteFile;
    public static String language;
    public static List<String> serverNames;
    public static boolean enableTeamChat;
    public static String teamChatFormat;
    public static int conversationTimeout;
    public static List<String> conversationCancelWords;
    public static List<String> blockedColors;
    public static boolean openMenuWhenCreated;
    public static int teamNameMinLength;
    public static int teamNameMaxLength;
    public static int teamPrefixMinLength;
    public static int teamPrefixMaxLength;
    public static int teamDescriptionMinLength;
    public static int teamDescriptionMaxLength;
    public static int loreSplitMembersLenght;
    public static double teamBankMaxMoneyLimit;
    public static int teamMaxMembersLimit;
    public static String teamDefaultDescription;
    public static boolean confirmCreateTeam;
    public static boolean confirmRemoveTeam;
    public static boolean confirmAddMoney;
    public static boolean confirmWithdrawMoney;
    public static double priceCreateTeam;
    public static double priceEditTeamName;
    public static double priceEditTeamPrefix;
    public static double priceEditTeamDescription;
    public static double priceUnlockTeamBank;
    public static double priceUnlockTeamClaim;
    public static double[] pricesUpgradeMembersLimit;
    public static double[] pricesUpgradeStorageLimit;
    public static List<Rank> ranks;
    public static ItemStack noPermissionItem;


    public Config(YamlConfiguration config) {
        this.config = config;
    }


    public void load(){
        databaseType = this.config.getString("database-type");
        mysqlHost = this.config.getString("mysql.host");
        mysqlDatabase = this.config.getString("mysql.database");
        mysqlUser = this.config.getString("mysql.user");
        mysqlPassword = this.config.getString("mysql.password");
        sqliteFile = this.config.getString("sqlite.file");
        language = this.config.getString("language");
        serverNames = this.config.getStringList("server-names");
        // TODO: voir pour changer new String[0] par String[]::new

        enableTeamChat = this.config.getBoolean("team-chat.enable");
        teamChatFormat = this.config.getString("team-chat.format");
        conversationTimeout = this.config.getInt("conversation-timeout");
        conversationCancelWords = this.config.getStringList("conversation-cancel-words");
        blockedColors = this.config.getStringList("blocked-colors");
        // TODO: ajouter le blocage des couleurs hexa 

        openMenuWhenCreated = this.config.getBoolean("open-menu-when-created");
        teamNameMinLength = this.config.getInt("team-name.min-length");
        teamNameMaxLength = this.config.getInt("team-name.max-length");
        teamPrefixMinLength = this.config.getInt("team-prefix.min-length");
        teamPrefixMaxLength = this.config.getInt("team-prefix.max-length");
        teamDescriptionMinLength = this.config.getInt("team-description.min-length");
        teamDescriptionMaxLength = this.config.getInt("team-description.max-length");
        loreSplitMembersLenght = this.config.getInt("lore-split-members-lenght");
        teamBankMaxMoneyLimit = this.config.getDouble("team-bank.max-money-limit");
        teamMaxMembersLimit = this.config.getInt("team-max-members-limit");
        teamDefaultDescription = this.config.getString("team-default-description");
        confirmCreateTeam = this.config.getBoolean("confirmation.create-team");
        confirmRemoveTeam = this.config.getBoolean("confirmation.remove-team");
        confirmAddMoney = this.config.getBoolean("confirmation.add-money");
        confirmWithdrawMoney = this.config.getBoolean("confirmation.withdraw-money");
        priceCreateTeam = this.config.getDouble("price.create-team");
        priceEditTeamName = this.config.getDouble("price.edit-team-name");
        priceEditTeamPrefix = this.config.getDouble("price.edit-team-prefix");
        priceEditTeamDescription = this.config.getDouble("price.edit-team-description");
        priceUnlockTeamBank = this.config.getDouble("price.unlock-team-bank");
        priceUnlockTeamClaim = this.config.getDouble("price.unlock-team-claim");
        pricesUpgradeMembersLimit = this.config.getDoubleList("price.upgrade-members-limit").stream().mapToDouble(Double::doubleValue).toArray();
        pricesUpgradeStorageLimit = this.config.getDoubleList("price.upgrade-storage-limit").stream().mapToDouble(Double::doubleValue).toArray();
        ranks = buildRanks();
        noPermissionItem = buildNoPermissionItem();
    }


    private List<Rank> buildRanks(){
        List<Integer> inversedPriorities = new ArrayList<>();
        List<Rank> ranks = new ArrayList<>();

        ConfigurationSection ranksSection = config.getConfigurationSection("ranks");

        if (ranksSection == null){
            StelyTeamPlugin.getPlugin().getLogger().severe("The ranks section is not defined !");
            return ranks;
        }

        for (String rank : ranksSection.getKeys(false)){
            final ConfigurationSection rankDetails = config.getConfigurationSection("ranks." + rank);

            if (rankDetails == null){
                StelyTeamPlugin.getPlugin().getLogger().severe("The rank " + rank + " is not defined !");
                continue;
            }

            final int inversedPriority = rankDetails.getInt("inversed-priority");

            if (inversedPriorities.contains(inversedPriority)){
                StelyTeamPlugin.getPlugin().getLogger().severe("The inversed priority " + inversedPriority + " is already used for another rank !");
                continue;
            }
            inversedPriorities.add(inversedPriority);

            final String rankName = rankDetails.getString("rank-name");
            if (rankName == null){
                StelyTeamPlugin.getPlugin().getLogger().severe("The rank name for the rank " + rank + " is not valid !");
                continue;
            }

            final ConfigurationSection itemSection = config.getConfigurationSection("ranks." + rank + ".item");

            if (itemSection == null){
                StelyTeamPlugin.getPlugin().getLogger().severe("The item section for the rank " + rank + " is not defined !");
                continue;
            }

            final Material material = Material.getMaterial(Objects.requireNonNull(itemSection.getString("material")));
            final String name = itemSection.getString("name");
            final String skullTexture = itemSection.getString("skull-texture");
            final int amount = itemSection.getInt("amount");
            final boolean isGlow = itemSection.getBoolean("is-glowing");
            final List<String> lore = itemSection.getStringList("lore");
            if (material == null){
                StelyTeamPlugin.getPlugin().getLogger().severe("The material " + itemSection.getString("material") + " is not valid for the rank " + rank + " !");
                continue;
            }

            final ItemStack item = new ItemBuilder(material)
                .setDisplayName(name)
                .setSkullTexture(skullTexture)
                .setAmount(amount)
                .setGlow(isGlow)
                .setLore(lore)
                .buildItem();
            
            ranks.add(new Rank(inversedPriority, rankName, item));
        }

        ranks.sort(new SortRanks());
        return ranks;
    }

    private ItemStack buildNoPermissionItem(){
        final ConfigurationSection noPermSection = config.getConfigurationSection("no-permission-item");

        if (noPermSection == null){
            StelyTeamPlugin.getPlugin().getLogger().severe("The no permission item section is not defined !");
            return new ItemStack(Material.AIR);
        }

        final String materialName = noPermSection.getString("material");
        if (materialName == null){
            StelyTeamPlugin.getPlugin().getLogger().severe("The material for the no permission item is not defined !");
            return new ItemStack(Material.AIR);
        }
        Material material = Material.getMaterial(materialName);


        if (material == null){
            StelyTeamPlugin.getPlugin().getLogger().severe("The material " + noPermSection.getString("material") + " is not valid for the no permission item !");
            material = Material.AIR;
        }

        final String name = noPermSection.getString("name");
        final String skullTexture = noPermSection.getString("skull-texture");
        final int amount = noPermSection.getInt("amount");
        final boolean isGlow = noPermSection.getBoolean("is-glowing");
        final List<String> lore = noPermSection.getStringList("lore");

        return new ItemBuilder(material)
            .setDisplayName(name)
            .setSkullTexture(skullTexture)
            .setAmount(amount)
            .setGlow(isGlow)
            .setLore(lore)
            .buildItem();
    }


    private static class SortRanks implements Comparator<Rank>{

        @Override
        public int compare(Rank o1, Rank o2) {
            return o1.getInversedPriority() - o2.getInversedPriority();
        }
    }
}
