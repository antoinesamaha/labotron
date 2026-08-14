package com.neofoc.app;

import com.neofoc.app.impl.ABL9Simulator;
import com.neofoc.app.impl.GenericSimulator;

public class SimulatorMain implements Constants {

    public static void main(String[] args) {
        // MAGLUMI Server
        GenericSimulator simulator = new GenericSimulator(6100, GenericSimulator.SocketRole.CLIENT, GenericSimulator.MAGLUMI_FRAMES);

        // GEM3500 Server
        //GenericSimulator simulator = new GenericSimulator(1182, GenericSimulator.SocketRole.SERVER, GenericSimulator.GEM_PREMIER_3500_FRAMES);

        //ABL9Simulator simulator = new ABL9Simulator();

        // --- Legacy simulators ---
        //AlegriaSimulator simulator = new AlegriaSimulator();
        //InfinitySimulator simulator = new InfinitySimulator();
        //GemPremier3500Simulator simulator = new GemPremier3500Simulator();

        simulator.simulate();
    }

}
