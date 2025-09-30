package com.example;

import com.example.api.ElpriserAPI;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        checkNoArgumentsPresent(args);
        checkMissingZoneArgument(args);
        System.out.println(userInputHelp());
        ElpriserAPI elpriserAPI = new ElpriserAPI();
    }

    public static String userInputHelp() {
        return ("--zone\n" + "SE1\n" + "SE2\n" + "SE3\n" + "SE4\n" + "--date\n" + "--sorted\n" + "--charging\n");
    }

    public static void checkNoArgumentsPresent(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: " + userInputHelp());
        }
    }

    public static void checkMissingZoneArgument(String[] args) {
        if (!Arrays.asList(args).contains("zone")) {
            System.out.println("\"zone\", \"required\"");
        }
    }
}
