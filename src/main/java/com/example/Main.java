package com.example;

import com.example.api.ElpriserAPI;

import java.sql.SQLOutput;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
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
        ElpriserAPI.Prisklass prisklass = ElpriserAPI.Prisklass.SE1;
        if(Arrays.asList(args).contains("--zone")){
            int prisklassIndex = Arrays.asList(args).indexOf("--zone");
            try {
            prisklass = ElpriserAPI.Prisklass.valueOf(args[prisklassIndex+1]);

            }catch (IllegalArgumentException e){
                System.out.println("Invalid zone");
            }
        };
        LocalDate date = LocalDate.now();
        date = LocalDate.of(2025,9,4);
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


            List<ElpriserAPI.Elpris> tommorowElPriser = new ArrayList<>(elpriserAPI.getPriser(date.plusDays(1), prisklass));
            elPriser.addAll(tommorowElPriser);


        displayMinMeanMaxPrices(elPriser);

        //If CLI contains sorted, sorts price.
        if (Arrays.asList(args).contains("--sorted")){
            sortsPrice(elPriser);
        }

        if (Arrays.asList(args).contains("--charging")){
            int findIndex = Arrays.asList(args).indexOf("--charging");
            int chargingWindow = Integer.parseInt(args[findIndex+1].split("h")[0]);



        double sum = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
            int startingHour = 0;
            String startingHoursString = "";

        for (int i = 0; i < chargingWindow; i++) {
            sum += elPriser.get(i).sekPerKWh();
            startingHour = i-chargingWindow+1;
        }

        double maxSum = sum;
            if (elPriser.size() == chargingWindow) {
                double lowestSek = elPriser.getFirst().sekPerKWh();
                int lowestIndex = 0;
                // Only one possible window
                sum = 0;
                for (int i = 0; i < elPriser.size(); i++) {
                    sum += elPriser.get(i).sekPerKWh();
                    if (elPriser.get(i).sekPerKWh() < lowestSek){
                        lowestIndex = i;
                        lowestSek = elPriser.get(i).sekPerKWh();
                    }
                }
                startingHoursString = elPriser.get(lowestIndex).timeStart().format(formatter);


                System.out.println("Lägsta totalprisfönster: " + formatToComma(sum / chargingWindow) + " öre/kWh");
                System.out.println("Starta laddning kl " + elPriser.get(0).timeStart());

            }else {
                for (int i = chargingWindow; i < elPriser.size(); i++) {
                    sum += elPriser.get(i).sekPerKWh() -  elPriser.get(i-chargingWindow).sekPerKWh();
                    if (maxSum > sum){
                        maxSum = sum;
                        startingHoursString = elPriser.get(i-chargingWindow+1).timeStart().format(formatter);
                        startingHour = i - chargingWindow + 1;
                    }
                }
            }

        //  01, 01, 01:00
            System.out.println("Max sum: " + maxSum);
            System.out.println("Påbörja laddning ");
            System.out.println("kl " + LocalTime.of(startingHour,0));
            System.out.println("0"+startingHour);
            System.out.println("Medelpris för fönster: " + formatToComma(maxSum/chargingWindow) + " öre");
            System.out.println("Starting hour: " + LocalTime.of(Integer.parseInt(startingHoursString),0));
        }
    }
}

    public static void sortsPrice(List<ElpriserAPI.Elpris> todayElPriser){
        //Below is sorted prices//
        List<ElpriserAPI.Elpris> copyOfList = new ArrayList<>(todayElPriser);
        copyOfList.sort(Comparator.comparingDouble((ElpriserAPI.Elpris pris) -> pris.sekPerKWh()).reversed());


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
            for (int i = 0; i < copyOfList.size();i++){
                ElpriserAPI.Elpris todayElpris = copyOfList.get(i);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//                SimpleDateFormat dateFormat = new SimpleDateFormat( "MMM d, yyyy");
                String startTime = todayElpris.timeStart().format(formatter);
                String endTime = todayElpris.timeEnd().format(formatter);
                String yearFormatDate = todayElpris.timeStart().format(dateFormatter);

                String formattedDate = startTime + "-" + endTime;
                String formattedPrice = formatToComma(todayElpris.sekPerKWh());

                String formattedElpris = yearFormatDate + "    " + formattedDate + "         " + formattedPrice + " öre";
                System.out.println(formattedElpris);
                System.out.println(todayElpris);
            }
        }
    }

//    TODO: Write method for calculating Min, Max and Mean price from Hashmap newElPriser
//        Sort them and choose lowest/max number orePerKWh?
    public static void displayMinMeanMaxPrices(List<ElpriserAPI.Elpris> todayElPriser){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");

        List<ElpriserAPI.Elpris> copyOfList = new ArrayList<>(todayElPriser);
        record newList (ZonedDateTime hours, double price){};
        List<newList> hourlyPrices = new ArrayList<>();
        //Sums each quarter in an hour, appends each summed hour to list hourlyPrices.
        int currentHourPosition = 0;
        if ( copyOfList.size() == 96){
            for (int i = 0; i < 24;i++){
                double sumOfHourForEveryQuarter = 0;
                for (int j = 0; j < 4; j++) {

                    sumOfHourForEveryQuarter += todayElPriser.get(i*4+j).sekPerKWh();
                }
                hourlyPrices.add(new newList(copyOfList.get(currentHourPosition).timeStart(),sumOfHourForEveryQuarter/4));
                currentHourPosition = (i+1)*4;

            }
            double meanPrice = 0;
            for (newList pricesPerHour: hourlyPrices){
                meanPrice += pricesPerHour.price;
            }
            System.out.println("Lägsta pris " + formatter.format(hourlyPrices.getFirst().hours) + "-" +  formatter.format(hourlyPrices.getFirst().hours.plusHours(1)) + " " +  formatToComma(hourlyPrices.getFirst().price));
            System.out.println("Högsta pris mellan: " + formatter.format(hourlyPrices.getLast().hours) + "-" + formatter.format(hourlyPrices.getLast().hours.plusHours(1)) + " " + formatToComma(hourlyPrices.getLast().price));
            System.out.println("Medelpris: " + formatToComma(meanPrice/24) + " öre");
        }else {
        copyOfList.sort(Comparator.comparingDouble(pris-> pris.sekPerKWh()));


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

        System.out.println("Lägsta pris " + hourWithLowestPrice + " " + lowestPrice);
        System.out.println("Högsta pris mellan: " + hourWithHighestPrice + " " + highestPrice);
        System.out.println("Medelpris: " + meanPriceToString);
        }

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



