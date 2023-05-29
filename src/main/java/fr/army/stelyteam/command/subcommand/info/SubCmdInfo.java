package fr.army.stelyteam.command.subcommand.info;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.TeamField;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SubCmdInfo extends SubCommand {

    private final TeamManager teamManager;
    private final DatabaseManager sqlManager;
    private final YamlConfiguration config;
    private final YamlConfiguration messages;
    private final MessageManager messageManager;
    private final ColorsBuilder colorsBuilder;


    public SubCmdInfo(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        this.teamManager = plugin.getTeamManager();
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

        final Team team = teamManager.getTeam(combinedArgs,
                TeamField.NAME,
                TeamField.PREFIX,
                TeamField.OWNER,
                TeamField.CREATION_DATE,
                TeamField.DESCRIPTION,
                TeamField.UPGRADES_MEMBERS,
                TeamField.BANK_UNLOCKED,
                TeamField.MEMBERS
        );
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }

        String yesMessage = messages.getString("commands.stelyteam_info.true");
        String noMessage = messages.getString("commands.stelyteam_info.false");
        int maxMembers = config.getInt("teamMaxMembers");

        final String name = team.getName().get();
        final String prefix = team.getPrefix().get();
        final String owner = team.getOwner().get();
        final String creationDate = new SimpleDateFormat("dd/MM/yyyy").format(team.getCreationDate().get());
        final String rawDescription = team.getDescription().get();
        final String description = rawDescription == null ? config.getString("team.defaultDescription") : rawDescription;
        final Integer rawMembersLevel = team.getUpgrades().getMembers().get();
        final int memberLevel = rawMembersLevel == null ? 0 : rawMembersLevel;
        final Boolean unlockedBankAccount = team.getBankAccount().getUnlocked().get();
        final String readableUnlockedBankAccount = unlockedBankAccount == null || !unlockedBankAccount ? noMessage : yesMessage;
        final Set<Member> members = team.getMembers();
        final String[] memberNames = getMemberNames(members.toArray(new Member[0]));

        List<String> lore = messages.getStringList("commands.stelyteam_info.output");

        lore = replaceInLore(lore, "%NAME%", name);
        lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(prefix));
        lore = replaceInLore(lore, "%OWNER%", owner);
        lore = replaceInLore(lore, "%DATE%", creationDate);
        lore = replaceInLore(lore, "%UNLOCK_BANK%", readableUnlockedBankAccount);
        lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(members.size()));
        lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers + memberLevel));
        lore = replaceInLore(lore, "%MEMBERS%", String.join(", ", memberNames));
        lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(description));

        player.sendMessage(String.join("\n", lore));

        return true;
    }

    private String[] getMemberNames(Member[] members) {
        final String[] names = new String[members.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = members[i].getMemberName();
        }
        return names;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (args[0].equals("info")) {
                List<String> result = new ArrayList<>();
                for (String teamID : sqlManager.getTeamsName()) {
                    if (teamID.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(teamID);
                    }
                }
                for (String playerName : sqlManager.getMembers()) {
                    if (playerName.toLowerCase().startsWith(args[1].toLowerCase())) {
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


    private List<String> replaceInLore(List<String> lore, String value, String replace) {
        List<String> newLore = new ArrayList<>();
        for (String str : lore) {
            newLore.add(str.replace(value, replace));
        }
        return newLore;
    }


    private String IntegerToString(Integer value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }


}
