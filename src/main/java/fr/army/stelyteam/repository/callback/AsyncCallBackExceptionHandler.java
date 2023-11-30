package fr.army.stelyteam.repository.callback;

import fr.army.stelyteam.repository.exception.ControllerException;

public interface AsyncCallBackExceptionHandler {
    void error(ControllerException e);
}
