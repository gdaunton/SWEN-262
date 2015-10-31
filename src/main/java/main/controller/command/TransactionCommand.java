package main.controller.command;

public class TransactionCommand implements Command{

    private  Command command1;
    private Command command2;

    public TransactionCommand(Command command1, Command command2) {
        this.command1 = command1;
        this.command2 = command2;
    }

    @Override
    public void execute() {
        command1.execute();
        command2.execute();
    }

    @Override
    public void undo() {
        command1.undo();
        command2.undo();
    }
}
