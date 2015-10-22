package main.controller;

public interface Command {
    void execute();

    void undo();
}
