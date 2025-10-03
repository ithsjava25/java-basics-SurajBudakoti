package com.example;

import com.example.api.ElpriserAPI;

import java.lang.classfile.constantpool.DoubleEntry;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {
    public static void main(String[] args) {

//        checkIfUserInputHelp(args);
//        checkIfArgumentsPresent(args);
//        checkMissingZoneArgument(args);
//        checkZoneValidity(args);

        ElpriserAPI elpriserAPI = new ElpriserAPI();
        ElpriserAPI.Prisklass prisklass = ElpriserAPI.Prisklass.SE2;
        LocalDate date = LocalDate.now();
        date = LocalDate.of(2025, 9,4);
        List<ElpriserAPI.Elpris> priser = new ArrayList<>(elpriserAPI.getPriser(date, prisklass));
        List<String> startTid = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");

        //TODO: Fix format XX:XX
       for (ElpriserAPI.Elpris tider: priser){
           String formattedString = tider.timeStart().format(formatter);
           startTid.add(formattedString + "-" + (Integer.parseInt(formattedString)+1));
           }
        System.out.println("Formatterad string " + startTid);

        List<Double> orePerkWh = new ArrayList<>();

        //Formats sekPerkWh to orePerkWh and puts them into its own list.
        for (ElpriserAPI.Elpris listOfElpriser: priser) {

            //Converts to orePerKWh as string
            Double convertSekToOre = listOfElpriser.sekPerKWh()*100;

            //Adds to list called orePerKWh after converting to double
            orePerkWh.add(convertSekToOre);
        }
        System.out.println(orePerkWh);

       HashMap <String, Double> newElPriser = new HashMap<>();

            for (int i = 0; i < priser.size(); i++) {
                newElPriser.put(startTid.get(i), orePerkWh.get(i));
                System.out.println(startTid.get(i) + " " + orePerkWh.get(i));
            }
//      TODO: Sort newElPriser according to price
        System.out.println(newElPriser);
    }


//

//        priser.sort(Comparator.comparingDouble(pris->pris.sekPerKWh()));
//        System.out.println(priser);
//
//        displayMinMaxPrice(priser);




    public static void displayMinMaxPrice(List<ElpriserAPI.Elpris> priser){
        priser.sort(Comparator.comparingDouble(pris->pris.sekPerKWh()));
        double lowestPrice = priser.getFirst().sekPerKWh();
        for (ElpriserAPI.Elpris lowest:priser){
            if (lowest.sekPerKWh() < lowestPrice){
                lowestPrice = lowest.sekPerKWh();
            }
        }

        double highestPrice = priser.getFirst().sekPerKWh();
        for (ElpriserAPI.Elpris highest:priser){
            if (highest.sekPerKWh() > highestPrice){
                highestPrice = highest.sekPerKWh();
            }
        }
        double meanPrice = 0;
        for (ElpriserAPI.Elpris mean: priser){
            meanPrice += mean.sekPerKWh();
        }

        System.out.println("Lägsta pris: " + formatOre(lowestPrice));
        System.out.println("Högsta pris: " + formatOre(highestPrice));
        System.out.println("Medelpris : " + formatOre(meanPrice/priser.size()));
    };
//    public static void checkIfUserInputHelp(String[] args){
//            if (Arrays.asList(args).contains("--help")){
//                userInputHelp();
//            }
//        };
//    public static String userInputHelp() {
//        return ("--zone\n" + "SE1\n" + "SE2\n" + "SE3\n" + "SE4\n" + "--date\n" + "--sorted\n" + "--charging\n");
//    }
//
//    public static void checkIfArgumentsPresent(String[] args) {
//        if (args.length == 0) {
//            System.out.println("Usage: " + userInputHelp());
//        }
//    }
//
//    public static void checkMissingZoneArgument(String[] args) {
//        if (!Arrays.asList(args).contains("--zone")) {
//            System.out.println("\"zone\", \"required\"");
//        }
//    }
//
////    checkkZoneValidity works but cancels checkMissingZoneArgument & showHelp_withHelpFlag()
//    public static void checkZoneValidity(String[] args){
//    if (!(args[1].equals("SE1") || args[1].equals("SE2") || args[1].equals("SE3") || args[1].equals("SE4"))){
//            System.out.println("\"invalid zone\", \"ogiltig zon\", \"fel zon\"");
//        }
//    }
//
////formatera om double till öre
    private static String formatOre(double sekPerKWh) {
        double ore = sekPerKWh * 100.0;
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.of("sv", "SE"));
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(ore);
    }
}


