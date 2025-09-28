package com.example;

import com.example.api.ElpriserAPI;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        inputHelp(args[0]);

        ElpriserAPI elpriserAPI = new ElpriserAPI(true);

    }

    public static void inputHelp(String help){
        if (help.equals("--help")){
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
