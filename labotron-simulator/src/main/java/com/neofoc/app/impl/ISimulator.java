package com.neofoc.app.impl;

import com.neofoc.app.Phase;

public interface ISimulator {
    void simulate();
    Phase getPhase();
    void setPhase(Phase phase);
    void sleep(long millis);
}
