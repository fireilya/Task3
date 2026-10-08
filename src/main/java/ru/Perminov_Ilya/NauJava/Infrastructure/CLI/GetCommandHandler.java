package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import org.springframework.stereotype.Component;
import ru.Perminov_Ilya.NauJava.Services.PlaneService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@Component
public class GetCommandHandler implements CommandHandler {

    private final PlaneService planeService;
    private final static String ALL_SUBCOMMAND = "all";
    private final static String FM_SUBCOMMAND = "fm";
    private final static String CURRENT_STATE_SUBCOMMAND = "currentstate";
    private final static String LIST_SUBCOMMAND = "list";
    private final static String ID_PARAM = "--id";
    private final static String HELP_PARAM = "--help";
    private final static String HELP_MESSAGE =
            """
                    Get command used to get info about needed plane
                    Get command has next subcommands:
                    get all <param> <value> - get all info about plane and has next params:
                    --id - id of plane you interested in. Type: String
                    get fm <param> <value> - get flight model of plane and has next params:
                    --id - id of plane you interested in. Type: String
                    get currentState <param> <value> - get current state of plane and has next params:
                    --id - id of plane you interested in. Type: String
                    get list - get all list of planes in id-name pair view. Hasn't params
                    """;

    public GetCommandHandler(PlaneService planeService) {
        this.planeService = planeService;
    }

    @Override
    public String processCommand(Scanner commandScanner) {
        if (!commandScanner.hasNext())
            return "Subcommand is missing";
        var subCommand = commandScanner.next().toLowerCase();
        if (subCommand.equals(HELP_PARAM))
            return HELP_MESSAGE;
        if (subCommand.equals(LIST_SUBCOMMAND))
            return planeService.getPlanesList();
        try {
            var params = parseParameters(commandScanner);
            var planeId = UUID.fromString(params.get(ID_PARAM));
            return switch (subCommand) {
                case ALL_SUBCOMMAND -> planeService.getAllPlaneInfo(planeId);
                case FM_SUBCOMMAND -> planeService.getPlaneFM(planeId);
                case CURRENT_STATE_SUBCOMMAND -> planeService.getCurrentPlaneState(planeId);
                default -> "Unknown subcommand \"" + subCommand + '\"';
            };
        } catch (IllegalArgumentException e) {
            return "Wrong format of " + ID_PARAM + " parameter";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    private HashMap<String, String> parseParameters(final Scanner commandScanner) {
        var requiredParameters = new HashMap<>(Map.ofEntries(Map.entry(ID_PARAM, "")));
        while (commandScanner.hasNext()) {
            var paramName = commandScanner.next();
            CommandProcessingInfrastructure.ensureParameterParsable(commandScanner, requiredParameters, paramName);
            if (paramName.equals(ID_PARAM)) {
                requiredParameters.put(paramName, commandScanner.next());
            } else {
                throw new RuntimeException("Unknown parameter: " + paramName);
            }
        }
        CommandProcessingInfrastructure.ensureRequiredParameters(requiredParameters);
        return requiredParameters;
    }
}
