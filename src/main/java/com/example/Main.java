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
        date = LocalDate.of(2025,9,4);
        List<ElpriserAPI.Elpris> todayElPriser = new ArrayList<>(elpriserAPI.getPriser(date, prisklass));

//        TODO: Denna ska komma in om timme för LocalTime.now() >= 13
//            Hämta dagens timma m.h.a: LocalTime.now().getHour())
//        List<ElpriserAPI.Elpris> tomorrowElPriser = new ArrayList<>(elpriserAPI.getPriser(date.plusDays(1), prisklass));

        //Alist for every hour in a day in format HH (prepends 0 if hour < 10).
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }

        todayElPriser.sort(Comparator.comparingDouble(pris->pris.sekPerKWh()));
        //A list for hourly prices
        List<Double> hourlyPrices = new ArrayList<>();
        //Sums each quarter in an hour, appends each summed hour to list hourlyPrices.
        if ( todayElPriser.size() == 96){

        for (int i = 0; i < 24;i++){
            double sumOfHourForEveryQuarter = 0;
            for (int j = 0; j < 4; j++) {
                int index = (i == 0) ? 0 : i * 4 - 1;
                sumOfHourForEveryQuarter += todayElPriser.get(index).sekPerKWh();
            }
            hourlyPrices.add(sumOfHourForEveryQuarter);
        }

        }
        else {
            for (int i = 0; i < todayElPriser.size();i++){
                ElpriserAPI.Elpris todayElpris = todayElPriser.get(i);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");

                String startTime = todayElpris.timeStart().format(formatter);
                String endTime = todayElpris.timeEnd().format(formatter);

                String formattedDate = startTime + "-" + endTime;
                String formattedPrice = formatToComma(todayElpris.sekPerKWh());

                String formattedElpris = formattedDate + " " + formattedPrice + " öre";
                System.out.println(formattedElpris);
                System.out.println(todayElpris);
            }
        }

        System.out.println(hourlyPrices);
        Collections.sort(hourlyPrices);
        System.out.println(hourlyPrices);

//        displayMinMaxMeanPrice(hours,hourlyPrices);

//
    }

//    TODO: Write method for calculating Min, Max and Mean price from Hashmap newElPriser
//        Sort them and choose lowest/max number orePerKWh?
    public static void displayMinMaxMeanPrice(List<String> hours, List<Double> hourlyPrices){
        String hourWhenLowestPrice = hours.getFirst();
        double lowestPrice = hourlyPrices.getFirst();
        String hourWhenHighestPrice = hours.getFirst();
        double highestPrice = hourlyPrices.getFirst();

        for (int i = 0; i < 24; i++) {
            if (lowestPrice > hourlyPrices.get(i)){
                lowestPrice = hourlyPrices.get(i);
                hourWhenLowestPrice = hours.get(i);
            }
            if (highestPrice < hourlyPrices.get(i)){
                highestPrice = hourlyPrices.get(i);
                hourWhenHighestPrice = hours.get(i);
            }
        }
        double meanPrice = 0;
        for (Double everyHour : hourlyPrices){
            meanPrice += everyHour;
        }

        System.out.println(hours);
        System.out.println(hourlyPrices);
        System.out.println("lowestPrice " +lowestPrice);
        System.out.println("hourWhenLowestPrice " + hourWhenLowestPrice);
        System.out.println("highestPrice " + highestPrice);
        System.out.println("hourWhenHighestPrice " + hourWhenHighestPrice);
        System.out.println("mean price " + meanPrice/96);
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
    private static String formatToComma(double sekPerKWh) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.of("sv", "SE"));
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(sekPerKWh*100);
    }
}


