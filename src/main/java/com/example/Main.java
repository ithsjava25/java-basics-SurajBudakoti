package com.example;

import com.example.api.ElpriserAPI;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
        List<ElpriserAPI.Elpris> todayElPriser = new ArrayList<>(elpriserAPI.getPriser(date, prisklass));

//        TODO: Denna ska komma in om timme för LocalTime.now() >= 13
//            Hämta dagens timma m.h.a: LocalTime.now().getHour())
//        List<ElpriserAPI.Elpris> tomorrowElPriser = new ArrayList<>(elpriserAPI.getPriser(date.plusDays(1), prisklass));

        //Created a list for every hour.
        List<Integer> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i);
        }
        for (ElpriserAPI.Elpris varjePris : todayElPriser){
            System.out.println("Varje pris från API: " + varjePris.sekPerKWh());
        }
        //Created list for prices every hour
        List<Double> hourlyPrices = new ArrayList<>();
        double lowestPrice = 0;
        double hourWhenLowestPrice = 0;
        double highestPrice = 0;
        double hourWhenHighestPrice = 0;

        if (todayElPriser.size() == 96){
            for (Integer everyHour: hours){
            double sumOfHourForEveryQuarter = 0;
                for (int i = 0; i < 4; i++) {
                    sumOfHourForEveryQuarter += (todayElPriser.get(everyHour*4+i).sekPerKWh());
                }
                hourlyPrices.add(sumOfHourForEveryQuarter);
            System.out.printf("sumOfHourForEveryQuarter: "+sumOfHourForEveryQuarter);
            }
        }

        List<String> betweenHours = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH");
       for (ElpriserAPI.Elpris tider: todayElPriser){
           String formattedTime = tider.timeStart().format(timeFormatter);

           int addHour = (Integer.parseInt(formattedTime)+1);
           String addHourToString = String.format("%02d", addHour);
           if (addHour == 24){
               addHourToString = "00";
           }
           betweenHours.add(formattedTime + "-" + addHourToString );
       }

//        List<Double> orePerkWh = new ArrayList<>();
//
//        //Formats sekPerkWh to orePerkWh and puts them into its own list.
//        for (ElpriserAPI.Elpris listOfElpriser: todayElPriser) {
//
//            //Converts to orePerKWh as string
//            Double convertedSekToOre = listOfElpriser.sekPerKWh()*100;
//
//            //Adds to list called orePerKWh after converting to double
//            orePerkWh.add(convertedSekToOre);
//        }
//        System.out.println(orePerkWh);
//
//        if (orePerkWh.size() == 96){
//            for (int i = 0; i < 24; i++) {
//
//
//            }
//        }
//        System.out.println("Class : "  + orePerkWh.get(1).getClass());
//      TODO: Add prices and times to HashMap newElPriser
//       HashMap <String, Double> newElPriser = new HashMap<>();
//            for (int i = 0; i < todayElPriser.size(); i++) {
//                newElPriser.put(betweenHours.get(i), orePerkWh.get(i));
//                System.out.println(betweenHours.get(i) + " " + orePerkWh.get(i));
//            }
//
//      TODO: Method for sort newElPriser according to price
//        System.out.println(newElPriser);

//    TODO: Before printing out final prices, formatOre on prices to print out with "," instead of "."
    }



//

//        priser.sort(Comparator.comparingDouble(pris->pris.sekPerKWh()));
//        System.out.println(priser);
//
//        displayMinMaxPrice(priser);




//    TODO: Write method for calculating Min, Max and Mean price from Hashmap newElPriser
//        Sort them and choose lowest/max number orePerKWh?
//    public static void displayMinMaxPrice(){
//
//        System.out.println("Lägsta pris: " + formatToComma(lowestPrice));
//        System.out.println("Högsta pris: " + formatToComma(highestPrice));
//        System.out.println("Medelpris : " + formatToComma(meanPrice/priser.size()));
//    };
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
    private static String formatToComma(double sekPerKWh) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.of("sv", "SE"));
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(sekPerKWh);
    }
}


