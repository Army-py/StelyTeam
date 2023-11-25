package fr.army.stelyteam.controller.callback;

public interface AsyncCallBackObject<T> {

  void done(T result);
}
