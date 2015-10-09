package main.controller;


import java.util.LinkedList;
import java.util.Queue;

public class Controller {
    private Queue<Command> commandUndoStack = new LinkedList<Command>();
    private Queue<Command> commandBackStack = new LinkedList<Command>();

    public void executeCommand(Command command){
        command.execute();
        commandBackStack.add(command);
    }

    public void undo(){
        Command undo = commandBackStack.poll();
        undo.undo();
        commandUndoStack.add(undo);
    }

    public void redo(){
        Command redo = commandUndoStack.poll();
        redo.execute();
        commandUndoStack.add(redo);
    }


}
