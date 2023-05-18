package fr.army.stelyteam.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.subcommand.manage.SubCmdToggle;
import fr.army.stelyteam.external.ExternalManager;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;

public class CmdTeamTchat implements CommandExecutor, TabCompleter {
    
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private DatabaseManager sqlManager;
    private SQLiteDataManager sqliteManager;
    private MessageManager messageManager;
    private ExternalManager externalManager;
    private Map<String, SubCommand> subCommands;


    public CmdTeamTchat(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getDatabaseManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.messageManager = plugin.getMessageManager();
        this.externalManager = plugin.getExternalManager();
        this.subCommands = new HashMap<>();
        initSubCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            UUID senderId = player.getUniqueId();
            String playerName = player.getName();
            Team team = Team.initFromPlayerName(playerName);

            if (cacheManager.isInConversation(playerName)){
                player.sendRawMessage(messageManager.getMessage("common.no_command_in_conv"));
                return true;
            }

            if (!sqliteManager.isRegistered(player.getName())) {
                sqliteManager.registerPlayer(player);
            }

            
            if (args.length == 0){
                player.sendMessage("Usage: /teamtchat <message>");
            }else{
                if (subCommands.containsKey(args[0]) && !((SubCommand) subCommands.get(args[0])).isOpCommand()){
                    SubCommand subCmd = (SubCommand) subCommands.get(args[0]);
                    if (subCmd.execute(player, args)){
                        return true;
                    }
                }else if (sender.isOp()){
                    if (subCommands.containsKey(args[0]) && ((SubCommand) subCommands.get(args[0])).isOpCommand()){
                        SubCommand subCmd = (SubCommand) subCommands.get(args[0]);
                        if (subCmd.execute(player, args)){
                            return true;
                        }
                    }
                }else{
                    externalManager.registerMessage(senderId, true);
                }
            }
        }
    return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            List<String> result = new ArrayList<>();
            for (String subcommand : subCommands.keySet()) {
                if (subcommand.toLowerCase().toLowerCase().startsWith(args[0]) && !((SubCommand) subCommands.get(subcommand)).isOpCommand()){
                    result.add(subcommand);
                }
            }
            if (sender.isOp()){
                for (String subcommand : subCommands.keySet()) {
                    if (subcommand.toLowerCase().toLowerCase().startsWith(args[0]) && ((SubCommand) subCommands.get(subcommand)).isOpCommand()){
                        result.add(subcommand);
                    }
                }
            }
            return result;
        }
        return null;
    }


    private void initSubCommands(){
        subCommands.put("toggle", new SubCmdToggle(plugin));
    }
}
