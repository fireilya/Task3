package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import org.springframework.stereotype.Component;
import ru.Perminov_Ilya.NauJava.Requests.PlaneCreateRequest;
import ru.Perminov_Ilya.NauJava.Services.PlaneService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class CreateCommandHandler implements CommandHandler {

    private final PlaneService planeService;

    private final static String NAME_PARAM = "--name";
    private final static String MAX_ENGINE_THRUST_PARAM = "--maxenginethrust";
    private final static String WEP_BOOST_PERCENT_PARAM = "--wepboostpercent";
    private final static String MAX_SPEED_PARAM = "--maxspeed";
    private final static String MASS_PARAM = "--mass";
    private final static String MAX_ANGLE_OF_ATTACK_PARAM = "--maxangleofattack";
    private final static String HELP_PARAM = "--help";
    private final static String HELP_MESSAGE =
            """
                    Create command used for plane creation
                    Syntax: create <param> <value>
                    Required params:
                    --name - name of the plane being build. type: String
                    --maxenginethrust - max thrust of plane engine in kgf. type: Double
                    --wepboostpercent - percent which thrust increased while wep. type: Double
                    --maxspeed - max speed that plane can reach in km/h. type: Double
                    --mass - mass of plane in kg. type: Double
                    --maxangleofattack - max plane AoA. type: Double
                    """;

    public CreateCommandHandler(final PlaneService planeService) {
        this.planeService = planeService;
    }

    @Override
    public String processCommand(Scanner commandScanner) {
        try {
            var parameters = parseParameters(commandScanner);
            if (parameters.containsKey(HELP_PARAM))
                return HELP_MESSAGE;
            return executeCommand(parameters);
        } catch (final RuntimeException e) {
            return e.getMessage();
        }
    }

    private HashMap<String, String> parseParameters(final Scanner commandScanner) {
        var requiredParameters = new HashMap<>(Map.ofEntries(
                Map.entry(NAME_PARAM, ""),
                Map.entry(MAX_ENGINE_THRUST_PARAM, ""),
                Map.entry(WEP_BOOST_PERCENT_PARAM, ""),
                Map.entry(MAX_SPEED_PARAM, ""),
                Map.entry(MASS_PARAM, ""),
                Map.entry(MAX_ANGLE_OF_ATTACK_PARAM, "")));

        while (commandScanner.hasNext()) {
            var parameterToken = commandScanner.next().toLowerCase();
            if (parameterToken.equals(HELP_PARAM))
            {
                requiredParameters.put(HELP_PARAM, HELP_MESSAGE);
                return requiredParameters;
            }
            CommandProcessingInfrastructure.ensureParameterParsable(commandScanner, requiredParameters, parameterToken);
            switch (parameterToken) {
                case NAME_PARAM:
                    var name = CommandProcessingInfrastructure.getStringParameterValue(commandScanner);
                    if (name.isEmpty())
                        throw new RuntimeException(
                                "Can't read value of --name parameter! Check the correctness of quotation and try again!");
                    requiredParameters.put(parameterToken, name);
                    break;
                case MAX_ENGINE_THRUST_PARAM:
                case WEP_BOOST_PERCENT_PARAM:
                case MAX_SPEED_PARAM:
                case MASS_PARAM:
                case MAX_ANGLE_OF_ATTACK_PARAM:
                    if (!commandScanner.hasNextDouble())
                        throw new RuntimeException("Wrong format of value for " + parameterToken + " parameter!");
                    requiredParameters.put(parameterToken, commandScanner.next());
                    break;
                default:
                    throw new RuntimeException("Unknown parameter " + parameterToken);
            }
        }
        CommandProcessingInfrastructure.ensureRequiredParameters(requiredParameters);
        return requiredParameters;
    }

    private String executeCommand(final HashMap<String, String> commandParameters) {
        var planeCreateRequest = new PlaneCreateRequest(
                commandParameters.get(NAME_PARAM),
                Double.parseDouble(commandParameters.get(MAX_ENGINE_THRUST_PARAM)),
                Double.parseDouble(commandParameters.get(WEP_BOOST_PERCENT_PARAM)),
                Double.parseDouble(commandParameters.get(MAX_SPEED_PARAM)),
                Double.parseDouble(commandParameters.get(MASS_PARAM)),
                Double.parseDouble(commandParameters.get(MAX_ANGLE_OF_ATTACK_PARAM)));
        var createdPlaneId = planeService.createPlane(planeCreateRequest);
        return createdPlaneId == null ? "Cannot create plane" : "Plane successfully created! New plane ID: " + createdPlaneId;
    }
}
