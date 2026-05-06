package com.app;

import com.data_management.DataStorage;
import com.cardioGenerator.HealthDataSimulator;

public class Main { 
    public static void main(String[] args) throws Exception{ 
        if (args.length > 0 && args[0].equals("DataStorage")) { 
            DataStorage.main(new String[]{}); 
        } else { 
            HealthDataSimulator.main(args); 
        } 
    } 
} 