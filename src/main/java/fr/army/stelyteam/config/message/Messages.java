package fr.army.stelyteam.config.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.exception.MessageNotFoundException;

public enum Messages {
    
    PREFIX,
    COMMAND_INVALID,
    NO_COMMAND_IN_CONVERSATION,
    NOT_ENOUGH_MONEY,
    FEATURE_DISABLED,
    CANNOT_ADD_NEGATIVE_MONEY,
    CANNOT_WITHDRAW_NEGATIVE_MONEY,
    CONVERSATION_TIMEOUT,
    CONVERSATION_CANCEL,
    PAID,
    RECEIVED,
    VALUE_TRUE,
    VALUE_FALSE,
    WRONG_AMOUNT,
    NO_RECEIVED_INVITATION,
    PLAYER_ALREADY_IN_TEAM,
    PLAYER_REPONDS_INVITATION,
    PLAYER_NOT_IN_TEAM,
    PLAYER_NOT_IN_THIS_TEAM,
    PLAYER_OFFLINE, // Placeholders
    NO_TEAM,
    UPGRADE_ALREADY_UNLOCKED,
    TEAM_DESCRIPTION_TOO_LONG,
    TEAM_DESCRIPTION_HAS_BLOCKED_COLORS,
    TEAM_NAME_ALREADY_EXISTS,
    TEAM_NAME_CANNOT_CONTAINS_SPACE,
    TEAM_NAME_TOO_SHORT,
    TEAM_NAME_TOO_LONG,
    TEAM_PREFIX_TOO_LONG,
    TEAM_PREFIX_TOO_SHORT,
    TEAM_PREFIX_HAS_BLOCKED_COLORS,
    TEAM_DOES_NOT_EXIST,
    TEAM_BANK_NOT_UNLOCKED,
    TEAM_OWNERS_OFFLINE,
    TEAM_OWNERS_RESPONDS_INVITATION,
    NO_TEAM_MEMBERS,
    TEAM_STORAGE_ALREADY_OPEN,
    TEAM_REACHED_MAX_MEMBERS,
    TEAM_BANK_MONEY_ADDED,
    SEND_DEPOSIT_MONEY_AMOUNT,
    TEAM_BANK_REACHED_MAX_MONEY,
    TEAM_BANK_MONEY_WITHDRAWN, // Placeholders
    SEND_WITHDRAW_MONEY_AMOUNT,
    TEAM_BANK_REACHED_MIN_MONEY,
    SEND_TEAM_NAME,
    SEND_TEAM_PREFIX,
    TEAM_CREATED,
    TEAM_DELETED,
    SEND_NEW_TEAM_NAME,
    TEAM_NAME_EDITED, // Placeholders
    SEND_NEW_TEAM_PREFIX,
    TEAM_PREFIX_EDITED,
    SEND_NEW_TEAM_DESCRIPTION,
    TEAM_DESCRIPTION_EDITED,
    TEAM_HOME_REDEFINE,
    TEAM_HOME_CREATED,
    TEAM_HOME_DELETED,
    TEAM_HOME_NOT_SET,
    TEAM_BANK_ALREADY_UNLOCKED,
    TEAM_BANK_UNLOCKED,
    TEAM_CLAIM_ALREADY_UNLOCKED,
    TEAM_CLAIM_UNLOCKED,
    MUST_UNLOCK_PREVIOUS_LEVEL,
    INCREASE_MEMBERS_LIMIT,
    INCREASE_STORAGES_LIMIT,
    ALREADY_ALLIED_WITH_TEAM,
    NOT_ALLIED_WITH_TEAM,
    SEND_TEAM_ALLIED_NAME_TO_ADD,
    ALLIANCE_INVITATION_SENT,
    ALLIANCE_INVITATION_RECEIVED,
    ACCEPT_ALLIANCE_INVITATION,
    REFUSE_ALLIANCE_INVITATION,
    TEAM_ISNT_ALLIED,
    ALLIANCE_REMOVED,
    REMOVE_ALLIANCE,
    ALLIANCE_HAS_BEEN_REMOVE,
    JOINED_TEAM,
    SEND_PLAYER_NAME_TO_PROMOTE,
    SEND_PLAYER_NAME_TO_ADD,
    MEMBER_INVITATION_SENT,
    CANNOT_EXCLUDE_YOURSELF,
    CANNOT_EXCLUDE_HIGHER_RANK,
    MEMBER_KICKED,
    MEMBER_INVITATION_RECEIVED, // Placeholders
    ACCEPT_MEMBER_INVITATION,
    REFUSE_MEMBER_INVITATION,
    ACCEPTED_INVITATION,
    REFUSED_INVITATION,
    KICK_MEMBER,
    PROMOTE_OWNER,
    HAVE_BEEN_KICKED_FROM_TEAM,
    HAVE_BEEN_PROMOTED_OWNER,
    HAVE_BEEN_PROMOTED,
    HAVE_BEEN_DEMOTE,
    LEFT_TEAM,
    OWNER_CANNOT_LEAVE_TEAM,
    DELETED_TEAM,
    BROADCAST_ADD_NEW_MEMBER,
    BROADCAST_KICK_MEMBER,
    BROADCAST_ADD_NEW_ALLIANCE,
    BROADCAST_REMOVE_ALLIANCE,
    BROADCAST_MEMBER_LEAVE_TEAM,
    BROADCAST_TEAM_BANK_UNLOCKED,
    BROADCAST_TEAM_CLAIM_UNLOCKED,
    BROADCAST_TEAM_DELETED,
    BROADCAST_NEW_MEMBER_UPGRADE_UNLOCKED,
    BROADCAST_NEW_STORAGE_UPGRADE_UNLOCKED,
    COMMAND_STELYTEAM_HELP_OUTPUT,
    COMMAND_STELYTEAM_ADMIN_OUTPUT,
    COMMAND_STELYTEAM_HOME_NOT_SET,
    COMMAND_STELYTEAM_HOME_TELEPORT,
    COMMAND_STELYTEAM_VISUAL_USAGE,
    COMMAND_STELYTEAM_VISUAL_OUTPUT,
    COMMAND_STELYTEAM_INFO_USAGE,
    COMMAND_STELYTEAM_INFO_OUTPUT,
    COMMAND_STELYTEAM_ACCEPT_OUTPUT,
    COMMAND_STELYTEAM_DENY_OUTPUT,
    COMMAND_STELYTEAM_DELETE_USAGE,
    COMMAND_STELYTEAM_DELETE_OUTPUT,
    COMMAND_STELYTEAM_MONEY_USAGE,
    COMMAND_STELYTEAM_MONEY_OUTPUT,
    COMMAND_STELYTEAM_CLAIM_USAGE,
    COMMAND_STELYTEAM_CLAIM_OUTPUT,
    COMMAND_STELYTEAM_UPGRADE_USAGE,
    COMMAND_STELYTEAM_UPGRADE_MAX_LEVEL,
    COMMAND_STELYTEAM_UPGRADE_OUTPUT,
    COMMAND_STELYTEAM_DOWNGRADE_USAGE,
    COMMAND_STELYTEAM_DOWNGRADE_MIN_LEVEL,
    COMMAND_STELYTEAM_DOWNGRADE_OUTPUT,
    COMMAND_STELYTEAM_EDITNAME_USAGE,
    COMMAND_STELYTEAM_EDITNAME_OUTPUT,
    COMMAND_STELYTEAM_EDITPREFIX_USAGE,
    COMMAND_STELYTEAM_EDITPREFIX_OUTPUT,
    COMMAND_STELYTEAM_CHANGEOWNER_USAGE,
    COMMAND_STELYTEAM_CHANGEOWNER_ALREADY_OWNER,
    COMMAND_STELYTEAM_CHANGEOWNER_NOT_IN_TEAM,
    COMMAND_STELYTEAM_CHANGEOWNER_OUTPUT,
    COMMAND_STELYTEAM_ADDMEMBER_USAGE,
    COMMAND_STELYTEAM_ADDMEMBER_OUTPUT,
    COMMAND_STELYTEAM_REMOVEMEMBER_USAGE,
    COMMAND_STELYTEAM_REMOVEMEMBER_NOT_IN_TEAM,
    COMMAND_STELYTEAM_REMOVEMEMBER_OUTPUT,
    COMMAND_TEAMCHAT_USAGE,
    COMMAND_TEAMCHAT_BLANK_MESSAGE,
    COMMAND_TEAMCHAT_NO_TEAM
    ;

    public String getMessage(Map<Placeholders, String> args) {
        final YamlConfiguration messages = StelyTeamPlugin.getPlugin().getMessages();

        String message = messages.getString(this.toString());
        if (message == null){
            new MessageNotFoundException(this, Config.language);
            return " ";
        }

        for(Placeholders placeholder : args.keySet()){
            message = message.replace("{" + placeholder.toString() + "}", args.get(placeholder));
        }

        return message;
    }

    public String getMessage(String... args) {
        final YamlConfiguration messages = StelyTeamPlugin.getPlugin().getMessages();

        String message = messages.getString(this.toString());
        if (message == null){
            new MessageNotFoundException(this, Config.language);
            return " ";
        }

        for(int i = 0; i < args.length; i++){
            message = message.replace("{" + i + "}", args[i]);
        }

        return message;
    }

    public String getMessage() {
        final YamlConfiguration messages = StelyTeamPlugin.getPlugin().getMessages();

        final String message = messages.getString(this.toString());
        if (message == null){
            new MessageNotFoundException(this, Config.language);
            return " ";
        }

        return message;
    }

    public List<String> getListMessage(Map<Placeholders, String> args){
        final YamlConfiguration messages = StelyTeamPlugin.getPlugin().getMessages();
        List<String> listMessages = new ArrayList<>();

        List<String> msgs = messages.getStringList(this.toString());
        if (msgs == null){
            new MessageNotFoundException(this, Config.language);
            return Collections.emptyList();
        }

        for(String message : msgs){
            for(Placeholders placeholder : args.keySet()){
                message = message.replace("{" + placeholder.toString() + "}", args.get(placeholder));
            }
            listMessages.add(message);
        }

        return listMessages;
    }

    public List<String> getListMessage(String... args){
        final YamlConfiguration messages = StelyTeamPlugin.getPlugin().getMessages();
        List<String> listMessages = new ArrayList<>();

        List<String> msgs = messages.getStringList(this.toString());
        if (msgs == null){
            new MessageNotFoundException(this, Config.language);
            return Collections.emptyList();
        }

        for(String message : msgs){
            for(int i = 0; i < args.length; i++){
                message = message.replace("{" + i + "}", args[i]);
            }
            listMessages.add(message);
        }

        return listMessages;
    }
}
