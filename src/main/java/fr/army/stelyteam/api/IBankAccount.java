package fr.army.stelyteam.api;

public interface IBankAccount {

    boolean isEnable();

    double getMoney();

    void changeAmount(double money);

    void incrementAmount(double money);

    void decrementAmount(double money);

}
