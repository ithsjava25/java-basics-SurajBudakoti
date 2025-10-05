package com.example;

import com.example.api.ElpriserAPI;

import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        checkIfUserInputHelp(args);
        checkIfArgumentsPresent(args);
        checkZoneValidity(args);
        checkMissingZoneArgument(args);

        //This creates Object called elpriseraPI of the class Elpripser API.
        ElpriserAPI elpriserAPI = new ElpriserAPI();
        ElpriserAPI.Prisklass prisklass = ElpriserAPI.Prisklass.SE2;
        LocalDate date = LocalDate.now();
        if (Arrays.asList(args).contains("--date")){
            int findIndex = Arrays.asList(args).indexOf("--date");
            try{
            date = LocalDate.parse(args[findIndex+1]);
            }
            catch (DateTimeParseException e){
                System.out.println("Invalid date");
            }
        }

        //This creates a List, of type ElpriserAPI.Elpriser, called elPriser. From this list  you can get prices and timestamps.
        List<ElpriserAPI.Elpris> elPriser = new ArrayList<>(elpriserAPI.getPriser(date, prisklass));
        if (elPriser.isEmpty()){
            System.out.println("No data");
        }else{

        if (LocalTime.now().getHour() >= 13 && !Arrays.asList(args).contains("--sorted")){
            List<ElpriserAPI.Elpris> tommorowElPriser = new ArrayList<>(elpriserAPI.getPriser(date.plusDays(1), prisklass));
            elPriser.addAll(tommorowElPriser);
        }

        System.out.println(elPriser);

        displayMinMeanMaxPrices(elPriser);

        //If CLI contains sorted, sorts price.
        if (Arrays.asList(args).contains("--sorted")){
            sortsPrice(elPriser);
        }

        if (Arrays.asList(args).contains("--charging")){
            int findIndex = Arrays.asList(args).indexOf("--charging");
            int chargingWindow = Integer.parseInt(args[findIndex+1].split("h")[0]);



        double sum = 0;

            int startingHour = 0;

        for (int i = 0; i < chargingWindow; i++) {
            sum += elPriser.get(i).sekPerKWh();
            startingHour = i-chargingWindow+1;
        }

        double maxSum = sum;

        for (int i = chargingWindow; i < elPriser.size(); i++) {
            sum += elPriser.get(i).sekPerKWh() -  elPriser.get(i-chargingWindow).sekPerKWh();
            if (maxSum > sum){
                    maxSum = sum;
                    startingHour = i-chargingWindow+1;
            }
        }
        //  01, 01, 01:00
            System.out.println("Max sum: " + maxSum);
            System.out.println("Påbörja laddning ");
            System.out.println("kl " + LocalTime.of(startingHour,0));
            System.out.println("0"+startingHour);
            System.out.println("Medelpris för fönster: " + formatToComma(maxSum/chargingWindow) + " öre");
            System.out.println("Starting hour: " + startingHour);
        }
    }
}

    public static void sortsPrice(List<ElpriserAPI.Elpris> todayElPriser){
        //Below is sorted prices//
        todayElPriser.sort(Comparator.comparingDouble(pris-> pris.sekPerKWh()));
        //A list for hourly prices
        List<Double> hourlyPrices = new ArrayList<>();
        //Sums each quarter in an hour, appends each summed hour to list hourlyPrices.
        if ( todayElPriser.size() == 96){
            for (int i = 0; i < 24;i++){
                double sumOfHourForEveryQuarter = 0;
                for (int j = 0; j < 4; j++) {

                    sumOfHourForEveryQuarter += todayElPriser.get(i*4+j).sekPerKWh();
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
    }

//    TODO: Write method for calculating Min, Max and Mean price from Hashmap newElPriser
//        Sort them and choose lowest/max number orePerKWh?
    public static void displayMinMeanMaxPrices(List<ElpriserAPI.Elpris> todayElPriser){
        List<ElpriserAPI.Elpris> copyOfList = new ArrayList<>(todayElPriser);
        copyOfList.sort(Comparator.comparingDouble(pris-> pris.sekPerKWh()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");

        ElpriserAPI.Elpris firstElementInSortedList = copyOfList.getFirst();
        ElpriserAPI.Elpris lastElementInSortedList = copyOfList.getLast();

        String lowestPrice = formatToComma(firstElementInSortedList.sekPerKWh()) + " öre";
        String hourWithLowestPrice = firstElementInSortedList.timeStart().format(formatter) + "-" + firstElementInSortedList.timeEnd().format(formatter);

        String highestPrice = formatToComma(lastElementInSortedList.sekPerKWh()) + " öre";
        String hourWithHighestPrice = lastElementInSortedList.timeStart().format(formatter) + "-" + lastElementInSortedList.timeEnd().format(formatter);

        double  meanPriceDouble = 0;
        for (ElpriserAPI.Elpris everyElement: copyOfList){
            meanPriceDouble += everyElement.sekPerKWh();
        }
        String meanPriceToString = formatToComma(meanPriceDouble/copyOfList.size());

        System.out.println("Lägsta pris mellan: " + hourWithLowestPrice + " " + lowestPrice);
        System.out.println("Högsta pris mellan: " + hourWithHighestPrice + " " + highestPrice);
        System.out.println("Medelpris: " + meanPriceToString);
    }

    public static void checkIfUserInputHelp(String[] args){
            if (Arrays.asList(args).contains("--help")){
                System.out.println(helpFlag());
            }
        }

    public static String helpFlag(){
        return ("--zone\n" + "SE1\n" + "SE2\n" + "SE3\n" + "SE4\n" + "--date\n" + "--sorted\n" + "--charging\n");
    }

    public static void checkIfArgumentsPresent(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: " + helpFlag());

        }
    }

    public static void checkMissingZoneArgument(String[] args) {
        if (!Arrays.asList(args).contains("--zone")) {
            System.out.println("\"zone\", \"required\"");
        }
    }

    public static void checkZoneValidity(String[] args){
    if (!(Arrays.asList(args).contains("SE1") || Arrays.asList(args).contains("SE2") || Arrays.asList(args).contains("SE3") || Arrays.asList(args).contains("SE4"))){
            System.out.println("\"invalid zone\", \"ogiltig zon\", \"fel zon\"");
        }
    }

////formatera om double till öre
    private static String formatToComma(double sekPerKWh) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.of("sv", "SE"));
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(sekPerKWh*100);
    }
}



