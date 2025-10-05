package com.neofoc.app.modules.labotron.focObjects;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FBoolean;
import com.foc.property.FString;

import java.awt.*;

/**
 * @author 01Barmaja
 */
public class FocTestLabelMap extends FocObject {

    public final static int VIEW_BROWSE_ALL = 2;
    public final static int VIEW_BROWSE_ALL_NO_EDIT = 3;

    public FocTestLabelMap(FocConstructor constr) {
        super(constr);
        newFocProperties();
    }

    public boolean isCalculated() {
        return getPropertyBoolean("calculated");
    }

    public static String getLisTestLabel(int testCode) {
        String res = "";
        FocList list = new FocList(new FocLinkSimple(Globals.getApp().getFocDescByName("FocTestLabelMap")));
        for (int i = 0; i < list.size(); i++) {
            FocTestLabelMap tlm = (FocTestLabelMap) list.getFocObject(i);
            if (tlm.getInstrumentTestCode().equals(testCode)) {
                res = String.valueOf(tlm.getLisTestLabel());
                break;
            }
        }
        return res;
    }

    public void dispose() {
        super.dispose();
    }

    public void logException(Exception e) {
        com.foc.Globals.logException(e);
    }

    public int getDayPriority() {
        return getPropertyInteger("day_priority");
    }

    public void setDayPriority(int proprity) {
        setPropertyInteger("day_priority", proprity);
    }

    public int getNightPriority() {
        return getPropertyInteger("night_priority");
    }

    public void setNightPriority(int proprity) {
        setPropertyInteger("night_priority", proprity);
    }

    public int getHolidayPriority() {
        return getPropertyInteger("holiday_priority");
    }

    public void setHolidayPriority(int proprity) {
        setPropertyInteger("holiday_priority", proprity);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */

    /* (non-Javadoc)
     * @see b01.l3.MessageListener#messageReceived(b01.l3.data.L3Message)
     */

    // ooooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo
    // LISTENERS
    // oooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo

    // ooooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo
    // GET SET
    // oooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo

    public String getLisTestLabel() {
        return getPropertyString("lis_test_label");
    }

    public void setLisTestLabel(String name) {
        setPropertyString("lis_test_label", name);
    }

    public FocInstrument getInstrument() {
        return (FocInstrument) getPropertyObject("instrument");
    }

    public String getInstrumentTestCode() {
        return getPropertyString("instrument_test_code");
    }

    public void setCode(String cd) {
        setPropertyString("instrument_test_code", cd);
    }

    public String getDescription() {
        return getPropertyString("test_description");
    }

    public void setDescription(String description) {
        setPropertyString("test_description", description);
    }

    @Override
    public void duplicationModification(FocObject source) {
        setPropertyObject("instrument", null);
        setPropertyInteger("day_priority", source.getPropertyInteger("day_priority") + 1);
        setPropertyInteger("night_priority", source.getPropertyInteger("night_priority") + 1);
        setPropertyInteger("holiday_priority", source.getPropertyInteger("holiday_priority") + 1);
        //list.sort();
        //TestLabelMap.rearrangePriorityList(list);
    }
}

