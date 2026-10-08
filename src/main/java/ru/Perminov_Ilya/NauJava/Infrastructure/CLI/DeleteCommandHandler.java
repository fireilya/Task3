package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import org.springframework.stereotype.Component;
import ru.Perminov_Ilya.NauJava.Services.PlaneService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@Component
public class DeleteCommandHandler implements CommandHandler {
    private final PlaneService planeService;
    private final static String ID_PARAM = "--id";
    private final static String HELP_PARAM = "--help";
    private final static String HELP_MESSAGE =
            """
                    Delete command used for plane deletion
                    Syntax: delete <param> <value>
                    Required parameters:
                    --id - id of plane to delete. Type: String
                    """;

    public DeleteCommandHandler(PlaneService planeService) {
        this.planeService = planeService;
    }

    @Override
    public String processCommand(Scanner commandScanner) {
        try {
            var params = parseParameters(commandScanner);
            if (params.containsKey(HELP_PARAM))
                return HELP_MESSAGE;
            var planeId = UUID.fromString(params.get(ID_PARAM));
            return planeService.deletePlane(planeId)
                    ? "Plane with id \"" + planeId + "\" deleted successfully"
                    : "Cannot delete plane with id \"" + planeId + "\"";
        } catch (IllegalArgumentException e) {
            return "Wrong value format of parameter " + ID_PARAM + " parameter";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    private HashMap<String, String> parseParameters(final Scanner commandScanner) {
        var requiredParameters = new HashMap<>(Map.ofEntries(Map.entry(ID_PARAM, "")));
        while (commandScanner.hasNext()) {
            var paramName = commandScanner.next().toLowerCase();
            if (paramName.equals(HELP_PARAM))
            {
                requiredParameters.put(HELP_PARAM, HELP_MESSAGE);
                return requiredParameters;
            }
            CommandProcessingInfrastructure.ensureParameterParsable(commandScanner, requiredParameters, paramName);
            if (paramName.equals(ID_PARAM)) {
                requiredParameters.put(ID_PARAM, commandScanner.next());
            } else {
                throw new RuntimeException("Unknown parameter: " + paramName);
            }
        }
        CommandProcessingInfrastructure.ensureRequiredParameters(requiredParameters);
        return requiredParameters;
    }
}
