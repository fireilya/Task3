package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import org.springframework.stereotype.Component;
import ru.Perminov_Ilya.NauJava.Services.PlaneService;

import java.util.*;

@Component
public class SetCommandHandler implements CommandHandler {
    private final PlaneService planeService;
    private final static String THRUST_SUBCOMMAND = "thrust";
    private final static String WEP_SUBCOMMAND = "wep";
    private final static String AOA_SUBCOMMAND = "aoa";
    private final static String ID_PARAM = "--id";
    private final static String VALUE_PARAM = "--value";
    private final static String HELP_PARAM = "--help";
    private final static String HELP_MESSAGE =
            """
                    set command used to set some changeable parameters of plane and has next subcommands:
                    set thrust - set new thrust percent of a plane and has next params:
                    --id - id of plane you interested in. Type: String
                    --value - new percent value in range [0; 100]. Type: Double
                    set wep - set wep of a plane. Wep activation available only if main thrust already 100%. Has next params:
                    --id - id of plane you interested in. Type: String
                    --value - wep new state. Type: Boolean (true/false)
                    set aoa - set new aoa of a plane. Cannot exceed max aoa value. Check plane info first. Has next params:
                    --id - id of plane you interested in. Type: String
                    --value - new angle of attack in degree. Must be greater then 0. Type: Double
                    """;

    public SetCommandHandler(PlaneService planeService) {
        this.planeService = planeService;
    }

    @Override
    public String processCommand(Scanner commandScanner) {
        if (!commandScanner.hasNext())
            return "Subcommand is missing";
        var subCommand = commandScanner.next().toLowerCase();
        if (subCommand.equals(HELP_PARAM))
            return HELP_MESSAGE;
        try {
            switch (subCommand) {
                case THRUST_SUBCOMMAND: {
                    var params = parseThrustAndAoaCommandsParameters(commandScanner);
                    if (params.containsKey(HELP_PARAM))
                        return HELP_MESSAGE;
                    return planeService.setPlaneThrust(
                            UUID.fromString(params.get(ID_PARAM)),
                            Double.parseDouble(params.get(VALUE_PARAM)));
                }
                case AOA_SUBCOMMAND: {
                    var params = parseThrustAndAoaCommandsParameters(commandScanner);
                    if (params.containsKey(HELP_PARAM))
                        return HELP_MESSAGE;
                    return planeService.setPlaneAngleOfAttack(
                            UUID.fromString(params.get(ID_PARAM)),
                            Double.parseDouble(params.get(VALUE_PARAM)));
                }
                case WEP_SUBCOMMAND: {
                    var params = parseWepCommandParameters(commandScanner);
                    if (params.containsKey(HELP_PARAM))
                        return HELP_MESSAGE;
                    return planeService.setPlaneWep(
                            UUID.fromString(params.get(ID_PARAM)),
                            Boolean.parseBoolean(params.get(VALUE_PARAM)));
                }
                default:
                    return "Unknown subcommand \"" + subCommand + "\"";
            }
        } catch (IllegalArgumentException e) {
            return "Wrong value format of parameter " + ID_PARAM + " parameter";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    private HashMap<String, String> parseThrustAndAoaCommandsParameters(final Scanner commandScanner) {
        var requiredParams = new HashMap<>(Map.ofEntries(
                Map.entry(ID_PARAM, ""),
                Map.entry(VALUE_PARAM, "")));
        while (commandScanner.hasNext()) {
            var paramName = commandScanner.next().toLowerCase();
            CommandProcessingInfrastructure.ensureParameterParsable(commandScanner, requiredParams, paramName);
            switch (paramName) {
                case ID_PARAM:
                    requiredParams.put(paramName, commandScanner.next());
                    break;
                case VALUE_PARAM:
                    if (!commandScanner.hasNextDouble())
                        throw new RuntimeException("Wrong value format of parameter " + paramName);
                    requiredParams.put(paramName, commandScanner.next());
                    break;
                default:
                    throw new RuntimeException("Unknown parameter " + paramName);
            }
        }
        CommandProcessingInfrastructure.ensureRequiredParameters(requiredParams);
        return requiredParams;
    }

    private HashMap<String, String> parseWepCommandParameters(final Scanner commandScanner) {
        var requiredParams = new HashMap<>(Map.ofEntries(
                Map.entry(ID_PARAM, ""),
                Map.entry(VALUE_PARAM, "")));
        while (commandScanner.hasNext()) {
            var paramName = commandScanner.next().toLowerCase();
            CommandProcessingInfrastructure.ensureParameterParsable(commandScanner, requiredParams, paramName);
            switch (paramName) {
                case ID_PARAM:
                    requiredParams.put(paramName, commandScanner.next());
                    break;
                case VALUE_PARAM:
                    var booleanValue = commandScanner.next();
                    if (!booleanValue.equalsIgnoreCase("true") && !booleanValue.equalsIgnoreCase("false"))
                        throw new RuntimeException("Wrong value format of parameter " + paramName);
                    requiredParams.put(paramName, booleanValue);
                    break;
                default:
                    throw new RuntimeException("Unknown parameter " + paramName);
            }
        }
        CommandProcessingInfrastructure.ensureRequiredParameters(requiredParams);
        return requiredParams;
    }
}
