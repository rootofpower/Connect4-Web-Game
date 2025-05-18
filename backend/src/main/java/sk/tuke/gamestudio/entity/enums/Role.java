package sk.tuke.gamestudio.entity.enums;

public enum Role {
    USER,
    /**
     * User role gives authorized user access to playing in multiplayer mode,
     * growing up in leaderboard,
     * rate and change his/her rating,
     * add comments.
     */
    GUEST,
    /**
     * Guest role is given to all users who are not logged in.
     * Guest can play only in local mode(realized in frontend).
     * Guest cannot rate or comment.
     * Guest cannot have writes in leaderboard.
     * Guest can only see leaderboard.
     * Guest can see avg. rating.
     * Guest can see comments.
     */
    ADMIN
    /**
     * Admin role gives authorized user access to all functionalities.
     * Admin can manage users, games, comments, ratings and leaderboard.
     * Admin can see all data in the system.
     * Admin can change any data in the system.
     */
}
