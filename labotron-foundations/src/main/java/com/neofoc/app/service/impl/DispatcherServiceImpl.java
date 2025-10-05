/*
 * Created on Jun 14, 2006
 */
package com.neofoc.app.service.impl;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListIterator;
import com.neofoc.app.modules.labotron.focObjects.FocTestLabelMap;
import com.neofoc.app.service.DispatcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
public class DispatcherServiceImpl implements DispatcherService {

    private boolean isBuildingMap;
    private HashMap<String, OneTestDispatcher> mapToOneTestDispatcher = null;

    public DispatcherServiceImpl() throws Exception {
    }

    public void dispose() {
        dispose_Map();
    }

    public void dispose_Map() {
        if (mapToOneTestDispatcher != null) {
            Iterator iter = mapToOneTestDispatcher.values().iterator();
            while (iter != null && iter.hasNext()) {
                OneTestDispatcher oneTestDisp = (OneTestDispatcher) iter.next();
                if (oneTestDisp != null) {
                    oneTestDisp.dispose();
                }
            }
            mapToOneTestDispatcher.clear();
        }
        mapToOneTestDispatcher = null;
    }

    public void rebuildTest2InstrumentMap() {
        dispose_Map();
        buildTest2InstrumentMapIfNeeded();
        recomputeCurrentStatus();
    }

    public void buildTest2InstrumentMapIfNeeded() {
        if (!isBuildingMap && (mapToOneTestDispatcher == null || mapToOneTestDispatcher.size() == 0)) {
            isBuildingMap = true;
            mapToOneTestDispatcher = new HashMap<String, OneTestDispatcher>();

            FocDesc desc = Globals.getApp().getFocDescByName("test_label_map");
            FocList testMapList = desc.getFocList();
            testMapList.loadIfNotLoadedFromDB();
            testMapList.iterate(new FocListIterator() {
                @Override
                public boolean treatElement(FocListElement element, FocObject focObj) {
                    FocTestLabelMap testLabelMap = (FocTestLabelMap) focObj;
                    pushOneTestDispatcher(testLabelMap);
                    return false;
                }
            });

            recomputeCurrentStatus();
            isBuildingMap = false;
        }
    }

    public void recomputeCurrentStatus() {
        recomputeCurrentInstrumentForTests();
    }

    public void recomputeCurrentInstrumentForTests() {
        if (mapToOneTestDispatcher != null) {
            Iterator iter = mapToOneTestDispatcher.values().iterator();
            while (iter != null && iter.hasNext()) {
                OneTestDispatcher oneTestDisp = (OneTestDispatcher) iter.next();
                if (oneTestDisp != null) {
                    oneTestDisp.sortAccordingToStatus();
                }
            }
        }
    }

    public Map<String, OneTestDispatcher> getTestToDispatecherMap() {
        buildTest2InstrumentMapIfNeeded();
        return mapToOneTestDispatcher;
    }

    public OneTestDispatcher getOneTestDispatcher(FocTestLabelMap testLabelMap) {
        OneTestDispatcher disp1 = null;
        Map<String, OneTestDispatcher> map = getTestToDispatecherMap();
        disp1 = map.get(testLabelMap.getLisTestLabel());

        return disp1;
    }

    public OneTestDispatcher pushOneTestDispatcher(FocTestLabelMap testLabelMap) {
        OneTestDispatcher disp1 = getOneTestDispatcher(testLabelMap);

        if (disp1 == null) {
            disp1 = new OneTestDispatcher(this);
            Map<String, OneTestDispatcher> map = getTestToDispatecherMap();
            map.put(testLabelMap.getLisTestLabel(), disp1);
        }

        disp1.addTestLabelMap(testLabelMap);
        return disp1;
    }

    @Override
    public FocTestLabelMap getInstrumentForTest(String lisTest, String suggestedInstrumentCode) {
        FocTestLabelMap testMap = null;

        Map<String, OneTestDispatcher> map = getTestToDispatecherMap();
        OneTestDispatcher oneTestDispatcher = map.get(lisTest);

        if (suggestedInstrumentCode != null) {
            testMap = oneTestDispatcher.findTestLabelMapForInstrument(suggestedInstrumentCode);
            if (testMap != null && !testMap.getInstrument().isOnHold()) {
                return testMap;
            }
        }
        if (oneTestDispatcher != null) {
            testMap = oneTestDispatcher.getFirstActive();
        }
        return testMap;
    }

}