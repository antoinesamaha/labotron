package com.neofoc.app;

import com.neofoc.app.impl.AlegriaSimulator;
import com.neofoc.app.impl.InfinitySimulator;

public class SimulatorMain implements Constants {

    public static void main(String[] args) {
        //InfinitySimulator simulator = new InfinitySimulator();
        AlegriaSimulator simulator = new AlegriaSimulator();
        simulator.simulate();
    }

}
