package ru.Perminov_Ilya.NauJava.Infrastructure.CLI;

import ru.Perminov_Ilya.NauJava.Services.PlaneService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleClient implements CommandLineRunner {
    private final CommandProcessor commandProcessor;
    public ConsoleClient(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Welcome to my beautiful plane database imitator!");
        System.out.println("If you wanna quit, just type exit!");
        System.out.println("Here you go!");
        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            System.out.print(">");
            var commandLine = scanner.nextLine();
            if (commandLine.isEmpty())
            {
                System.out.println("Probably you forget to enter your command! Try again!");
                continue;
            }
            if (commandLine.equals("exit"))
                break;
            System.out.println(commandProcessor.processCommand(commandLine));
        }
        System.out.println("Thank you for using my app! Goodbye!");
        System.exit(0);
    }
}
