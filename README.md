# Connect Four

**Author:** Kuryliak Oleksii

## Table of Contents
1.  [Introduction](#introduction)
2.  [Features](#features)
3.  [Technologies Stack](#technologies-stack)
    *   [Backend](#backend)
    *   [Frontend](#frontend)
    *   [Database](#database)
    *   [DevOps & Deployment](#devops--deployment)
4.  [Project Structure](#project-structure)
5.  [Getting Started](#getting-started)
    *   [Prerequisites](#prerequisites)
    *   [Running Locally](#running-locally)
6.  [API Overview](#api-overview)
7.  [Deployment](#deployment)

## Introduction
This project is a web-based implementation of the classic game "Connect Four". Users can register, log in, play Connect Four against other players in real-time, view leaderboards, comment on the game, and rate it. The application is designed with a modern, responsive user interface and a robust backend.

## Features
*   User authentication (registration and login) with JWT.
*   Real-time multiplayer Connect Four gameplay.
*   Game lobbies for finding opponents.
*   Score tracking and leaderboards (MMR-based and win-based).
*   Game rating system.
*   Commenting system for the game.
*   Player statistics.
*   Responsive design for various screen sizes.

## Technologies Stack

### Backend
*   **Language:** Java 17+
*   **Framework:** Spring Boot 3.x
    *   Spring Web: For building RESTful APIs.
    *   Spring Data JPA: For database interaction.
    *   Spring Security: For authentication and authorization using JWT.
*   **ORM:** Hibernate
*   **Build Tool:** Apache Maven
*   **API Documentation:** OpenAPI (Swagger) likely configured if `/v3/api-docs/**` and `/swagger-ui/**` are exposed.

### Frontend
*   **Language:** TypeScript
*   **Framework/Library:** React 18.x
*   **Build Tool/Bundler:** Vite
*   **Styling:**
    *   Tailwind CSS: Utility-first CSS framework.
    *   Shadcn/ui: Re-usable UI components.
*   **State Management:** React Context API, TanStack Query (React Query) for server state.
*   **Routing:** Likely `react-router-dom` (common choice for React projects).

### Database
*   **Type:** PostgreSQL (relational database)

### DevOps & Deployment
*   **Containerization:** Docker
*   **Orchestration:** Docker Compose (for local development and multi-container setup)
*   **Web Server/Reverse Proxy:** Nginx (serving frontend static files and proxying API requests to the backend)
*   **Cloud Platform:** Google Cloud Platform (GCP) - Deployed on a Compute Engine virtual instance.

## Project Structure
The workspace is organized into several main directories:

*   `backend/`: Contains the Java Spring Boot application.
    *   `src/main/java/sk/tuke/gamestudio/`: Main application code.
        *   `entity/`: JPA entities.
        *   `repository/`: Spring Data JPA repositories.
        *   `service/`: Business logic and service layers (including JPA and potentially older RestClient/JDBC implementations).
        *   `security/`: Spring Security configuration, JWT filter.
        *   `server/controller/`: REST API controllers.
        *   `game/connectfour/`: Core game logic for Connect Four (console version might be present).
    *   `src/main/resources/`: Configuration files like `application.properties`.
    *   `pom.xml`: Maven project configuration.
    *   `Dockerfile`: For building the backend Docker image.
*   `frontend/`: Contains the TypeScript React application.
    *   `src/`: Main source code.
        *   `components/`: Reusable React components (including `ui/` for Shadcn components).
        *   `pages/`: Top-level page components.
        *   `services/`: API client logic.
        *   `context/`: React context for state management (e.g., AuthContext).
        *   `hooks/`: Custom React hooks.
        *   `lib/`: Utility functions.
        *   `main.tsx`: Application entry point.
        *   `App.tsx`: Root application component.
    *   `public/`: Static assets.
    *   `package.json`: NPM project configuration and dependencies.
    *   `vite.config.ts`: Vite configuration.
    *   `tailwind.config.ts`: Tailwind CSS configuration.
    *   `Dockerfile`: For building the frontend Docker image.
*   `database/`: Contains PostgreSQL setup.
    *   `Dockerfile`: For the PostgreSQL Docker image.
    *   `init.sql`: Database initialization script.
*   `diagram/`: Contains project diagrams (class diagrams, sequence diagrams, etc.).
*   `docker-compose.yml`: Defines and orchestrates the `backend`, `frontend`, and `db` services.
*   `nginx.conf`: Nginx configuration file, typically used as a reverse proxy.
*   `README.md`: This file.

## Getting Started

### Prerequisites
*   Docker: [Install Docker](https://docs.docker.com/get-docker/)
*   Docker Compose: Usually comes with Docker Desktop. If not, [Install Docker Compose](https://docs.docker.com/compose/install/)
*   A web browser (e.g., Chrome, Firefox, Edge)

### Running Locally
1.  **Clone the repository (if applicable).**
2.  **Navigate to the root directory of the project.**
3.  **Ensure environment variables are set up if required.**
    *   The backend `application.properties` might rely on environment variables for database credentials or JWT secrets, especially when not using the default Docker Compose setup.
    *   The frontend might require environment variables for API base URLs (e.g., `VITE_API_BASE_URL`).
4.  **Build and run the application using Docker Compose:**
    ```bash
    docker-compose up --build -d
    ```
    The `-d` flag runs the containers in detached mode.
5.  **Access the application:**
    *   The frontend should be accessible at `http://localhost` (or another port if configured in `docker-compose.yml` or `nginx.conf`). Nginx typically listens on port 80.

## API Overview
The backend exposes RESTful APIs for various functionalities. Key services include:

*   **Authentication (`/api/connect4/auth/`)**:
    *   User registration (`/register`)
    *   User login (`/login`)
*   **Score (`/api/score/`)**:
    *   Add score (POST, authenticated)
    *   Get top scores for a game (GET `/{game}`)
    *   Reset scores for a game (DELETE `/{game}`, admin only)
*   **Rating (`/api/connect4/rating/`)**:
    *   Get average rating for a game (GET `/average?game={gameName}`)
    *   Get user's rating for a game (GET `/my-rating?game={gameName}`, authenticated)
    *   Set/update rating for a game (POST, authenticated)
    *   Delete user's rating for a game (DELETE `?game={gameName}`, authenticated)
*   **Comment (`/api/connect4/comment/`)**:
    *   Get comments for a game (GET `?game={gameName}`)
    *   Add a comment (POST, authenticated)
    *   Delete a comment (DELETE `/{commentId}`, authenticated, user must own comment or be admin)
    *   Clear all comments for a game (DELETE `?game={gameName}`, admin only)
*   **Player Statistics (`/api/connect4/leaderboard/`)**:
    *   Get leaderboard (GET `?game={gameName}&sortBy={mmr|wins}&limit={limit}`)
    *   Get player stats (GET `/player/{username}?game={gameName}`, authenticated)
    *   Reset player stats (DELETE `/player/{username}?game={gameName}`, admin only)
    *   Reset all player stats for a game (DELETE `?game={gameName}`, admin only)
*   **Game (`/api/connect4/game/`)**:
    *   Create new game session (POST `/create`, authenticated)
    *   Get game state (GET `/{gameSessionId}`, authenticated)
    *   Make a move (POST `/{gameSessionId}/move`, authenticated)
    *   Forfeit game (POST `/{gameSessionId}/forfeit`, authenticated)
*   **Lobby (`/api/connect4/lobby/`)**:
    *   Endpoints for managing game lobbies, finding opponents, etc. (specifics depend on implementation).

*(Note: Some API paths like `/api/score/` seem to use a different base path than `/api/connect4/`. This is based on the provided controller configurations.)*

## Deployment
The application is deployed on a **Google Cloud Platform (GCP) Compute Engine virtual instance**.
Docker containers (backend, frontend, database) are likely run on this instance, with Nginx managing incoming traffic and serving the application.
The `nginx.conf` file handles routing requests to the appropriate service (frontend static files or backend API).
The domain `connect4-rop.duckdns.org` (seen in `PlayerStatsController.java` comments) might be pointing to this GCP instance.