package fr.army.stelyteam.command.subcommand.info;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class SubCmdInfo extends SubCommand {

    private final StorageManager storageManager;
    private DatabaseManager sqlManager;
    private YamlConfiguration config;
    private YamlConfiguration messages;
    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;


    public SubCmdInfo(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        this.storageManager = plugin.getStorageManager();
        this.sqlManager = plugin.getDatabaseManager();
        this.config = plugin.getConfig();
        this.messages = plugin.getMessages();
        this.messageManager = plugin.getMessageManager();
        this.colorsBuilder = new ColorsBuilder(plugin);
    }


    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_info.usage"));
            return true;
        }

        args[0] = "";
        final String combinedArgs = String.join("", args);

        Team team = storageManager.retreiveTeam(combinedArgs/*, TeamField.NAME, TeamField.PREFIX, TeamField.OWNER, TeamField.CREATION_DATE, TeamField.DESCRIPTION, */);
        if (team == null) {
            team = storageManager.retreivePlayerTeam(combinedArgs);
        }

        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }

        String yesMessage = messages.getString("commands.stelyteam_info.true");
        String noMessage = messages.getString("commands.stelyteam_info.false");
        Integer maxMembers = config.getInt("teamMaxMembers");

        final String name = team.getName().retrieve();
        final String prefix = team.getPrefix().retrieve();
        final String owner = team.getOwner().retrieve();
        final String creationDate = new SimpleDateFormat("dd/MM/yyyy").format(team.getCreationDate().retrieve());
        final String description = team.getDescription().retrieve();
        final Integer rawMembersLevel = team.getUpgrades().getMembers().retrieve();
        final int memberLevel = rawMembersLevel == null ? 0 : rawMembersLevel;
        final Boolean unlockedBankAccount = team.getBankAccount().getUnlocked().retrieve();
        final String readableUnlockedBankAccount = unlockedBankAccount == null || !unlockedBankAccount ? noMessage : yesMessage;
        Collection<Member> teamMembers = team.getTeamMembers();
        List<String> lore = messages.getStringList("commands.stelyteam_info.output");

        lore = replaceInLore(lore, "%NAME%", name);
        lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(prefix));
        lore = replaceInLore(lore, "%OWNER%", owner);
        lore = replaceInLore(lore, "%DATE%", creationDate);
        lore = replaceInLore(lore, "%UNLOCK_BANK%", readableUnlockedBankAccount);
        lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers.size()));
        lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers + memberLevel));
        lore = replaceInLore(lore, "%MEMBERS%", String.join(", ", team.getMembersName()));
        lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(description));

        player.sendMessage(String.join("\n", lore));

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2){
            if (args[0].equals("info")){
                List<String> result = new ArrayList<>();
                for (String teamID : sqlManager.getTeamsName()) {
                    if (teamID.toLowerCase().startsWith(args[1].toLowerCase())){
                        result.add(teamID);
                    }
                }
                for (String playerName : sqlManager.getMembers()) {
                    if (playerName.toLowerCase().startsWith(args[1].toLowerCase())){
                        result.add(playerName);
                    }
                }
                return result;
            }
        }
        return null;
    }


    @Override
    public boolean isOpCommand() {
        return false;
    }


    private List<String> replaceInLore(List<String> lore, String value, String replace){
        List<String> newLore = new ArrayList<>();
        for(String str : lore){
            newLore.add(str.replace(value, replace));
        }
        return newLore;
    }


    private String IntegerToString(Integer value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


}
