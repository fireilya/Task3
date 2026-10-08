package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CommandProcessingInfrastructure {
    public static String getStringParameterValue(final Scanner commandScanner)
    {
        var valueToken = commandScanner.next();
        if (valueToken.startsWith("\"") || valueToken.startsWith("'")) {
            var sb = new StringBuilder();
            var closeQuote = valueToken.substring(0, 1);
            boolean isParsingSuccess = false;
            sb.append(valueToken);
            while (commandScanner.hasNext() && !isParsingSuccess) {
                sb.append(' ');
                var nextPartOfValueToken = commandScanner.next();
                sb.append(nextPartOfValueToken);
                isParsingSuccess = nextPartOfValueToken.endsWith(closeQuote);
            }
            if (!isParsingSuccess)
                return "";
            return sb.toString();
        }
        return valueToken;
    }

    public static void ensureRequiredParameters(final HashMap<String, String> requiredParameters)
    {
        var missingParameters = new ArrayList<String>(requiredParameters.size());
        for (var key : requiredParameters.keySet())
        {
            if (requiredParameters.get(key).isEmpty())
                missingParameters.add(key);
        }
        if (missingParameters.isEmpty())
            return;
        var errorMessage = new StringBuilder();
        errorMessage.append("Missing required parameters:\n");
        for (var param : missingParameters)
            errorMessage.append(param).append("\n");
        throw new RuntimeException(errorMessage.toString());
    }

    public static void ensureParameterParsable(
            final Scanner commandScanner,
            final HashMap<String, String> requiredParameters,
            final String parameterName)
    {
        if (requiredParameters.containsKey(parameterName) && !requiredParameters.get(parameterName).isEmpty())
            throw new RuntimeException("reassignment of parameter " + parameterName);
        if (!commandScanner.hasNext())
            throw new RuntimeException("empty value for " + parameterName + " parameter!");
    }
}
