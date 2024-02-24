package fr.army.stelyteam.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MemberAddMoneyEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final OfflinePlayer sender;
    private boolean confirm;
    private double money;

    public MemberAddMoneyEvent(boolean async, OfflinePlayer sender, boolean confirm, double money) {
        super(async);
        this.sender = sender;
        this.confirm = confirm;
        this.money = money;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    

    public OfflinePlayer getSender() {
        return sender;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public double getMoney() {
        return money;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
