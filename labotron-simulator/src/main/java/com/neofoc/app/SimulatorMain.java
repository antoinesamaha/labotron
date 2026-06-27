package com.neofoc.app;

import com.neofoc.app.impl.GenericSimulator;

public class SimulatorMain implements Constants {

    public static void main(String[] args) {
        // --- GenericSimulator (pick one) ---
        // Server, pure receive — Labotron connects in, simulator just ACKs
        //GenericSimulator simulator = new GenericSimulator(9000, GenericSimulator.SocketRole.SERVER, (String[]) null);

        // Server, push results on each connection
        //GenericSimulator simulator = new GenericSimulator(9000, GenericSimulator.SocketRole.SERVER, GenericSimulator.GENERIC_RESULT_FRAMES);

        // Server, send inquiry → receive orders → send results
        //GenericSimulator simulator = new GenericSimulator(9000, GenericSimulator.SocketRole.SERVER, GenericSimulator.GENERIC_INQUIRY_THEN_RESULT_FRAMES);

        // Client, pure receive — simulator dials out, ACKs everything
        //GenericSimulator simulator = new GenericSimulator(9000, GenericSimulator.SocketRole.CLIENT, (String[]) null);

        // GEM3500 Server
        GenericSimulator simulator = new GenericSimulator(1182, GenericSimulator.SocketRole.SERVER, GenericSimulator.GEM_PREMIER_3500_FRAMES);

        // --- Legacy simulators ---
        //AlegriaSimulator simulator = new AlegriaSimulator();
        //InfinitySimulator simulator = new InfinitySimulator();
        //GemPremier3500Simulator simulator = new GemPremier3500Simulator();

        simulator.simulate();
    }

}
