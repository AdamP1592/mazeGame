# Maze Puzzle Game

## Overview
This project is a simple maze puzzle game written in Java using Swing. It dynamically generates a maze using a variant of Prim's algorithm, and allows the player to navigate through the maze while encountering various tile effects. The primary objective is to reach the goal tile, which completes the puzzle.

## Features
- **Maze Generation**:  
  - Generates a maze with customizable dimensions.
  - Uses a randomized version of Prim's algorithm.
  - Supports seeding for reproducible maze layouts.
  
- **Graphical Interface**:  
  - Utilizes Java Swing for rendering the maze and game elements.
  - Provides a dialog for inputting maze dimensions and seed value.

- **Player Movement and Interaction**:  
  - Controlled via keyboard: move forward/backward and turn left/right.
  - Displays the player’s current health and direction indicator.
  - Updates dynamically as the player moves through the maze.

- **Tile Effects**:  
  - **Fire**: Reduces player health gradually.
  - **Stick**: Temporarily disables the player's movement.
  - **Heal**: Increases the player's health.
  - Effects are applied when the player steps on special "TrappedFloor" tiles.

- **Game Progression**:  
  - Reaching the goal tile triggers a win condition with an option to restart or exit.
  - Game over scenario when the player's health drops below zero.

## Requirements
- **Java Development Kit (JDK)**: Version 8 or above.
- **Java Swing**: Included with the standard JDK.

## How to Run
1. **Compile the Project**  
   Open a terminal in the project directory and compile all Java files:

```bash
javac -o compiledFolder gamepkg/*.java
```

2. **Run the Game**  
   Execute the main class:
```bash
java -cp compiledFolder gamepkg.GameWindow
```

3. **Input Maze Parameters**  
   A dialog will prompt you to enter:
   - Number of rows
   - Number of columns
   - Seed value for maze generation

## Controls
- **W**: Move forward.
- **S**: Move backward.
- **Q**: Turn left.
- **E**: Turn right.

## Project Structure
- **GameWindow & GamePanel**:  
  - Handles the main game window, input dialog, and game loop.
  
- **mazeGenerator**:  
  - Contains the algorithm to generate the maze.
  - Determines the starting point and places the goal tile using a breadth-first search.

- **Entity Classes**:  
  - **Entity**: Base class for all in-game objects.
  - **Player**: Manages player properties, movement, and active effects.
  - **Wall & Floor**: Represent basic maze components.
  - **TrappedFloor**: A floor tile that triggers an effect when stepped on.
  - **Goal**: Marks the endpoint of the maze.

- **Effects**:  
  - **Effect**: Base class for tile effects.
  - **Fire**: Applies damage over time.
  - **Stick**: Temporarily restricts movement.
  - **Heal**: Restores health.

## Future Enhancements
- Improve graphical fidelity and animations.
- Introduce additional power-ups or hazards.
- Expand maze generation with more complexity.
- Incorporate sound effects and enhanced user interface elements.

## License
This project is licensed under CC.
