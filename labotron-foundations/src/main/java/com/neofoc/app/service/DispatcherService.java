package com.neofoc.app.service;

import com.neofoc.app.modules.labotron.focObjects.FocTestLabelMap;

public interface DispatcherService {
    FocTestLabelMap getInstrumentForTest(String lisTest, String suggestedInstrumentCode);
}
