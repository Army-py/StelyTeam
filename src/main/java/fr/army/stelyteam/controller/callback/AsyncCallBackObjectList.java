package fr.army.stelyteam.controller.callback;

import java.util.List;

public interface AsyncCallBackObjectList<T> {

  void done(List<T> result);
}