package fr.army.stelyteam.repository.callback;

import java.util.List;

public interface AsyncCallBackObjectList<T> {

  void done(List<T> result);
}