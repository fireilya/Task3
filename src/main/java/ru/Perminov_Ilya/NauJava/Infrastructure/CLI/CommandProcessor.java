package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

@Component
public class CommandProcessor {
    private final HashMap<String, CommandHandler> commandHandlers = new HashMap<>();
    private final static String CREATE_COMMAND = "create";
    private final static String GET_COMMAND = "get";
    private final static String SET_COMMAND = "set";
    private final static String DELETE_COMMAND = "delete";
    private final static String HELP_MESSAGE =
            """
                    Ups, it looks like you don't know how to use my app. It's ok. I'll show you
                    Welcome to my plane db imitator app. This app has next commands:
                    create - create new plane
                    get - get planes info
                    set - set planes parameters
                    delete - delete plane
                    for more info about this commands type command name and use --help option
                    """;

    public CommandProcessor(
            CreateCommandHandler createCommandHandler,
            GetCommandHandler getCommandHandler,
            SetCommandHandler setCommandHandler,
            DeleteCommandHandler deleteCommandHandler) {
        commandHandlers.put(CREATE_COMMAND, createCommandHandler);
        commandHandlers.put(GET_COMMAND, getCommandHandler);
        commandHandlers.put(SET_COMMAND, setCommandHandler);
        commandHandlers.put(DELETE_COMMAND, deleteCommandHandler);
    }

    public String processCommand(final String command) {
        var commandScanner = new Scanner(command);
        commandScanner.useLocale(Locale.ROOT);
        var commandName = commandScanner.next().toLowerCase();
        if (!commandHandlers.containsKey(commandName))
            return HELP_MESSAGE;
        return commandHandlers.get(commandName).processCommand(commandScanner);
    }
}
