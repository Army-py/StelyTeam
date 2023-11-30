package fr.army.stelyteam.repository.callback;

public interface AsyncCallBackObject<T> {

  void done(T result);
}
