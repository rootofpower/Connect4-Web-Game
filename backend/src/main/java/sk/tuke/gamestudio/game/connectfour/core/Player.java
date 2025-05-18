package sk.tuke.gamestudio.game.connectfour.core;

import lombok.Getter;

@Getter
public class Player {
    private final int id;
    private final String name;
    private final String color;

    public Player(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

}