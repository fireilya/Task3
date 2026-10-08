package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import java.util.HashMap;
import java.util.Scanner;

public interface CommandHandler {
    String processCommand(final Scanner commandScanner);
}
