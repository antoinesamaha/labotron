package com.neofoc.app;

import com.neofoc.app.impl.AlegriaSimulator;
import com.neofoc.app.impl.InfinitySimulator;

public class SimulatorMain implements Constants {

    public static void main(String[] args) {
        // Choose which simulator to run:

        //InfinitySimulator_old simulator = new InfinitySimulator_old();
        AlegriaSimulator simulator = new AlegriaSimulator();
        //InfinitySimulator simulator = new InfinitySimulator();
        //GemPremier3500Simulator simulator = new GemPremier3500Simulator();
        //Abl9Simulator simulator = new Abl9Simulator();

        simulator.simulate();
    }

}
