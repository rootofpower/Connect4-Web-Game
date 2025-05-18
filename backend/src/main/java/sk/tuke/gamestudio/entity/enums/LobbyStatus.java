package sk.tuke.gamestudio.entity.enums;

public enum LobbyStatus {
    WAITING_FOR_OPPONENT,
    // The player is waiting for the opponent to join the game

    WAITING_FOR_READY,
    // The player is waiting for the opponent to be ready to start the game

    READY_TO_START,
    // players is ready to start the game

    IN_GAME,
    // players is currently in a game

    FINISHED
    // players is finished with the game
}
