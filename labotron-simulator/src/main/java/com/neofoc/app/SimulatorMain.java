package com.neofoc.app;

import com.neofoc.app.impl.AlegriaSimulator;
import com.neofoc.app.impl.GemPremier3500Simulator;
import com.neofoc.app.impl.InfinitySimulator;

public class SimulatorMain implements Constants {

    public static void main(String[] args) {
        // Choose which simulator to run:

        //InfinitySimulator simulator = new InfinitySimulator();
        AlegriaSimulator simulator = new AlegriaSimulator();
        //GemPremier3500Simulator simulator = new GemPremier3500Simulator();

        simulator.simulate();
    }

}
