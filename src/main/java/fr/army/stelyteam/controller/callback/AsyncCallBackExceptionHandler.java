package fr.army.stelyteam.controller.callback;

import fr.army.stelyteam.controller.exception.ControllerException;

public interface AsyncCallBackExceptionHandler {
    void error(ControllerException e);
}
