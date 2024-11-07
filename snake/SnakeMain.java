package com.main;

public class SnakeMain {
    public static void main(String[] args) {

        new GameFrame();
    }

    /*
    Todo: Make things more modular. Move methods into other classes
    Make apple spawn only in unobstructed spot: Collection of valid spots? Update as snake moves
    snakeGame.requestFocus();   Grants focus to the panel, making it so that panel is 'listening' to key presses
        Idea: Control two game at the same time
    Better documentation
    Add restart button
    Add size options?
    Make game more responsive? Shorter delay, check every other timestep?
     */
}
