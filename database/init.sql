CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, 
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL, 
    created_at TIMESTAMP(6) NOT NULL 
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    game VARCHAR(100) NOT NULL,
    comment TEXT NOT NULL,
    commented_at TIMESTAMP(6) WITH TIME ZONE NOT NULL, 
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE 
);
CREATE INDEX IF NOT EXISTS idx_comment_user ON comments(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_game_time ON comments(game, commented_at DESC);

CREATE TABLE IF NOT EXISTS ratings (
    id BIGSERIAL PRIMARY KEY, 
    game VARCHAR(100) NOT NULL,
    rating FLOAT NOT NULL CHECK (rating >= 0 AND rating <= 5),
    rated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL, 
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE, 
    CONSTRAINT ratings_unique_user_game UNIQUE (user_id, game)
);
CREATE INDEX IF NOT EXISTS idx_rating_game ON ratings(game);

CREATE TABLE IF NOT EXISTS player_stats (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game VARCHAR(100) NOT NULL,
    wins INTEGER NOT NULL DEFAULT 0,
    losses INTEGER NOT NULL DEFAULT 0,
    draws INTEGER NOT NULL DEFAULT 0,
    mmr INTEGER NOT NULL DEFAULT 1000,
    last_played_at TIMESTAMP(6) NOT NULL, 
    CONSTRAINT player_stats_unique_user_game UNIQUE (user_id, game)
);
CREATE INDEX IF NOT EXISTS idx_playerstats_game_mmr ON player_stats(game, mmr DESC);
CREATE INDEX IF NOT EXISTS idx_playerstats_game_wins ON player_stats(game, wins DESC);

CREATE TABLE IF NOT EXISTS lobbies (
    id BIGSERIAL PRIMARY KEY,
    lobby_code VARCHAR(6) UNIQUE NOT NULL,
    host_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    opponent_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    host_ready BOOLEAN NOT NULL DEFAULT false,
    opponent_ready BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(30) NOT NULL,
    game_name VARCHAR(100) NOT NULL,
    game_session_id BIGINT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL 
);
CREATE INDEX IF NOT EXISTS idx_lobby_status ON lobbies(status);

CREATE TABLE IF NOT EXISTS game_sessions (
    id BIGSERIAL PRIMARY KEY,
    game_name VARCHAR(100) NOT NULL,
    player1_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    player2_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    board_state TEXT NOT NULL,
    current_player_username VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL, 
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL, 
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL, 
    lobby_id BIGINT NULL 
);
CREATE INDEX IF NOT EXISTS idx_gamesession_status ON game_sessions(status);
CREATE INDEX IF NOT EXISTS idx_gamesession_lobby_id ON game_sessions(lobby_id);

CREATE TABLE IF NOT EXISTS scores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game VARCHAR(100) NOT NULL,
    points INTEGER NOT NULL DEFAULT 0,
    played_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);
