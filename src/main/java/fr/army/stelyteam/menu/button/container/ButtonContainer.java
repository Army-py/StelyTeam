package fr.army.stelyteam.menu.button.container;

public class ButtonContainer<T> {
    
    private final T content;

    public ButtonContainer(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
