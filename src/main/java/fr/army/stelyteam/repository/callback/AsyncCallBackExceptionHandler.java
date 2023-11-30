package fr.army.stelyteam.repository.callback;

import fr.army.stelyteam.repository.exception.RepositoryException;

public interface AsyncCallBackExceptionHandler {
    void error(RepositoryException e);
}
