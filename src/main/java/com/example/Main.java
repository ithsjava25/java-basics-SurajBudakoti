package com.example;

import com.example.api.ElpriserAPI;

import java.time.LocalDate;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        try{
            if (!args[0].equals("--zone")){
                System.out.println("\"zone\", \"required\"");
                userInputHelp(args);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Please input something!");

        }


        ElpriserAPI elpriserAPI = new ElpriserAPI();
        System.out.println(elpriserAPI.getPriser("2024-12-15", ElpriserAPI.Prisklass.SE1).get(2));
//
    }

    public static void userInputHelp(String[] help){
        if (help[0].equals("--help") || help.length == 0) {
            System.out.println(
                    "--zone\n"+
                    "SE1\n"+
                    "SE2\n"+
                    "SE3\n"+
                    "SE4\n"+
                    "--date\n"+
                    "--sorted\n" +
                    "--charging\n");
        }
    }
}