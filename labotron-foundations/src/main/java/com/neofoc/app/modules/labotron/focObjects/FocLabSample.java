package com.neofoc.app.modules.labotron.focObjects;

import com.foc.Globals;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.focDataSourceDB.db.SQLSelectExistance;
import com.foc.focDataSourceDB.db.SQLSelectFindReferenceForWhereExpression;
import com.foc.gui.FPanel;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListIterator;
import com.foc.property.*;
import com.neofoc.app.modules.labotron.LabSample_FocObject;

import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 01Barmaja
 */
public class FocLabSample extends LabSample_FocObject {
    public static final int LIQUID_TYPE_EMPTY = -1;
    public static final int LIQUID_TYPE_SERUM = 1;
    public static final int LIQUID_TYPE_URIN = 2;
    public static final int LIQUID_TYPE_CSF = 3;
    public static final int LIQUID_TYPE_BODY_FLUID = 4;
    public static final int LIQUID_TYPE_STOOL = 5;
    public static final int LIQUID_TYPE_SUPERNATENT = 6;
    public static final int LIQUID_TYPE_OTHERS = 7;

    public static final String LIQUID_TYPE_EMPTY_TITLE = "-";
    public static final String LIQUID_TYPE_SERUM_TITLE = "Serum";
    public static final String LIQUID_TYPE_URIN_TITLE = "Urin";
    public static final String LIQUID_TYPE_CSF_TITLE = "CSF";
    public static final String LIQUID_TYPE_BODY_FLUID_TITLE = "Body fluid";
    public static final String LIQUID_TYPE_STOOL_TITLE = "Stool";
    public static final String LIQUID_TYPE_SUPERNATENT_TITLE = "Suprnt";
    public static final String LIQUID_TYPE_OTHERS_TITLE = "Others";

    public static final Color availableINL3Color = Color.CYAN;
    public static final Color resultAvailableColor = Color.LIGHT_GRAY;

    private String graph = null;

    private String rackNumber = "";
    private String tubePosition = "";

    private void initFocProperties(String id) {
        setSampleId(id);
        setLiquidType(-1);
        setEntryDateTime(LocalDateTime.now());
    }

    public FocLabSample(String id) {
        super();
        initFocProperties(id);
    }

    public FocLabSample(FocConstructor constr) {
        super(constr);
        initFocProperties("");
        setEntryDateTime(LocalDateTime.now());
    }

    public void dispose() {
        super.dispose();
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getGraph() {
        return graph;
    }

    public void setSampleAsNonDatabaseResident() {
        setDbResident(false);
        FocList testList = getPropertyList("test_list");
        testList.setLoaded(true);
    }

    public void setSampleTestListAsLoaded() {
        FocList testList = getPropertyList("test_list");
        testList.setLoaded(true);
    }

    public void setLiquidType(String liquidType) {
        switch (liquidType){
            case LIQUID_TYPE_EMPTY_TITLE:
                setLiquidType(LIQUID_TYPE_EMPTY);
                break;
            case LIQUID_TYPE_SERUM_TITLE:
                setLiquidType(LIQUID_TYPE_SERUM);
                break;
            case LIQUID_TYPE_URIN_TITLE:
                setLiquidType(LIQUID_TYPE_URIN);
                break;
            case LIQUID_TYPE_CSF_TITLE:
                setLiquidType(LIQUID_TYPE_CSF);
                break;
            case LIQUID_TYPE_BODY_FLUID_TITLE:
                setLiquidType(LIQUID_TYPE_BODY_FLUID);
                break;
            case LIQUID_TYPE_STOOL_TITLE:
                setLiquidType(LIQUID_TYPE_STOOL);
                break;
            case LIQUID_TYPE_SUPERNATENT_TITLE:
                setLiquidType(LIQUID_TYPE_SUPERNATENT);
                break;
            case LIQUID_TYPE_OTHERS_TITLE:
                setLiquidType(LIQUID_TYPE_OTHERS);
                break;
            default:
                Globals.logString("FocLabSample.setLiquidType: Unknown liquid type: " + liquidType);
        }
    }

    public static long findReferenceForSampleId(String sampleId) {
        SQLSelectFindReferenceForWhereExpression selectExistance = new SQLSelectFindReferenceForWhereExpression(FocLabSample.getFocDesc(), "sample_id" + "=" + sampleId);
        selectExistance.execute();
        return selectExistance.getReference();
    }

    public static FocLabSample loadForSampleId(String sampleId) {
        FocList list = new FocList(new FocLinkSimple(getFocDesc()));
        list.getFilter().putAdditionalWhere("SAMPLE_ID", "sample_id" + "=" + sampleId);
        list.loadIfNotLoadedFromDB();
        return list != null ? (FocLabSample) list.getFocObject(0) : null;
    }

    public FocList getTestListWithoutLoad() {
        return getPropertyList("test_list");
    }

    public String getSampleId(){
        return getPropertyString("sample_id");
    }

    public void setSampleId(String sampleId){
        setPropertyString("sample_id", sampleId);
    }

    public FocList getTestList() {
        FList list = (FList) getFocPropertyByName("lab_test_LIST");
        FocList focList = (list != null) ? list.getList() : null;
        if (focList != null) {
            focList.loadIfNotLoadedFromDB();
        }
        return focList;
    }

    public FocLabTest findTest(String testlabel) {
        FocLabTest foundTest = null;
        FocList testList = getTestList();
        if (testList != null) {
            for (int i = 0; i < testList.size() && foundTest == null; i++) {
                FocLabTest test = (FocLabTest) testList.getFocObject(i);
                if (test.getLabel().compareTo(testlabel) == 0) {
                    foundTest = test;
                }
            }
        }
        return foundTest;
    }

    public FocLabTest addTest() {
        FocLabTest test = null;
        FocList focList = getTestList();
        if (focList != null) {
            test = (FocLabTest) focList.newEmptyItem();
            focList.add(test);
        }
        return test;
    }

    public void updateStatusForTests(final int status) {
        getTestList().iterate(new FocListIterator() {
            @Override
            public boolean treatElement(FocListElement element, FocObject focObj) {
                FocLabTest test = (FocLabTest) focObj;
                test.updateStatus(status);
                return false;
            }
        });
    }

    public void updateBlockedForTests(final Boolean blocked) {
        getTestList().iterate(new FocListIterator() {
            @Override
            public boolean treatElement(FocListElement element, FocObject focObj) {
                FocLabTest test = (FocLabTest) focObj;
                test.updateBlocked(blocked);
                return false;
            }
        });
    }

    public void updateNotificationMessageForTests(final String message) {
        getTestList().iterate(new FocListIterator() {
            @Override
            public boolean treatElement(FocListElement element, FocObject focObj) {
                FocLabTest test = (FocLabTest) focObj;
                test.updateNotificationMessage(message);
                return false;
            }
        });
    }

    public void copyWithoutTests(FocLabSample sample) {
        setSampleId(sample.getSampleId());
        setPatientId(sample.getPatientId());
        setOrigin(sample.getOrigin());
        setLastName(sample.getLastName());
        setFirstName(sample.getFirstName());
        setMiddleName(sample.getMiddleName());
        setSex(sample.getSex());
        setAge(sample.getAge());
        setLiquidType(sample.getLiquidType());
        setEntryDateTime(sample.getEntryDateTime());
        setDateOfBirth(sample.getDateOfBirth());
    }

    public void copy(FocLabSample sample) {
        copyWithoutTests(sample);
        if (sample.getReference().getInteger() > 0) {
            setReference(sample.getReference().getInteger());
        }
        setLiquidType(sample.getLiquidType());
        FocList testList = sample.getTestList();
        for (int i = 0; i < testList.size(); i++) {
            FocLabTest curTest = (FocLabTest) testList.getFocObject(i);
            if (getTestList().searchByPropertyStringValue(getThisFocDesc().getFieldIDByName("label"), curTest.getLabel()) == null) {
                FocLabTest test = this.addTest();
                test.copyAndBackup(curTest);
            }
        }
        setGraph(sample.getGraph());
    }

    public void copyTestsFrom(FocLabSample sample) {
        HashMap<String, FocLabTest> newTestListMap = new HashMap<String, FocLabTest>();
        //setLiquidType(sample.getLiquidType());
        FocList newTestList = sample.getTestList();
        for (int i = 0; i < newTestList.size(); i++) {
            newTestListMap.put(((FocLabTest) newTestList.getFocObject(i)).getLabel(), (FocLabTest) newTestList.getFocObject(i));
        }
        FocList oldTestList = getTestList();
        for (int i = 0; i < oldTestList.size(); i++) {
            FocLabTest testToUpgrade = (FocLabTest) oldTestList.getFocObject(i);
            if (newTestListMap.containsKey(testToUpgrade.getLabel())) {
                FocLabTest newTest = newTestListMap.get(testToUpgrade.getLabel());
                testToUpgrade.copyAndBackup(newTest);
            }
        }
        setGraph(sample.getGraph());
    }

    public void refreshSampleFrom(FocLabSample sample) {
        //setReference(sample.getReference().getInteger());
        copyTestsFrom(sample);
        if (sample.getOkToBeSent()) {
            setOkToBeSent(sample.getOkToBeSent());
        }
        if (sample.getResultConfirmed()) {
            setResultConfirmed(sample.getResultConfirmed());
        }
        if (getReference() != null) backup();
    }

    public void addTest2(FocLabTest t) {
        FocLabTest test = null;
        FocList focList = getTestList();
        if (focList != null) {
            test = (FocLabTest) focList.newEmptyItem();
            focList.add(test);
            test.copy(t);
        }
        test.setCreated(true);
    }

    public void removeTest(FocLabTest test) {
        FocList focList = getTestList();
        if (focList != null) {
            focList.remove(test);
        }
    }

    public void remove() {
//        SQLFilter deleteFilter = new SQLFilter(this, SQLFilter.FILTER_ON_SELECTED);
//        StringBuffer deleteCondition = new StringBuffer("REF = " + getReference().getInteger());
//        deleteFilter.putAdditionalWhere("L3SAMPLE_DELETE", deleteCondition.toString());
//        SQLDelete delete = new SQLDelete(getFocDesc(), deleteFilter);
//        delete.execute();
//        removeRelativeTests();
    }

    public void removeRelativeTests() {
        FocList testList = getTestList();
        for (int i = 0; i < testList.size(); i++) {
            FocLabTest test = (FocLabTest) testList.getFocObject(i);
            test.remove();
        }
    }

    @SuppressWarnings("unchecked")
    public Iterator<FocLabTest> testIterator() {
        Iterator<FocLabTest> iter = null;
        FocList focList = getTestList();
        if (focList != null) {
            iter = (Iterator<FocLabTest>) focList.focObjectIterator();
        }
        return iter;
    }

    public StringBuffer toStringBuffer() {
        StringBuffer buff = new StringBuffer();
        buff.append(getId() + " liq:" + getLiquidType() + "PatientId=" + getPatientId() + " FirstName=" + getFirstName() + " MidName=" + getMiddleName() + " LastName=" + getLastName() + " Sex" + getSex() + " Origin=" + getOrigin() + "\n");
        int i = 0;
        Iterator iter = testIterator();
        while (iter != null && iter.hasNext()) {
            FocLabTest test = (FocLabTest) iter.next();
            buff.append("Test[" + i + "]=" + test.toStringBuffer());
            buff.append("\n");
            i++;
        }
        return buff;
    }

    public void setMiddleInitial(String middleInitial) {
        if (middleInitial == null || middleInitial.compareTo("null") == 0) {
            super.setMiddleName("");
        } else {
            super.setMiddleName(middleInitial);
        }
    }

//    public boolean isResultConfirmed() {
//        FBoolean confirmed = (FBoolean) getFocPropertyByName("result_confirmed");
//        return (confirmed != null) ? confirmed.getBoolean() : null;
//    }
//
//    public void setResultConfirmed(boolean confirmed) {
//        FBoolean c = (FBoolean) getFocPropertyByName("result_confirmed");
//        if (c != null) {
//            c.setBoolean(confirmed);
//        }
//    }
//
//    public boolean isOkToBeSent() {
//        FBoolean ok = (FBoolean) getFocPropertyByName("ok_to_be_sent");
//        return (ok != null) ? ok.getBoolean() : null;
//    }
//
//    public void setOkToBeSent(boolean ok) {
//        FBoolean o = (FBoolean) getFocPropertyByName("ok_to_be_sent");
//        if (o != null) {
//            o.setBoolean(ok);
//        }
//    }

//    public Date getEntryDate() {
//        FDateTime date = (FDateTime) getFocPropertyByName("entry_date");
//        return (date != null) ? date.getDate() : null;
//    }
//
//    public void setEntryDate(Date d) {
//        FDateTime date = (FDateTime) getFocPropertyByName("entry_date");
//        if (date != null) {
//            date.setDate(d);
//        }
//    }

//    public Date getDateOfBirth() {
//        FDateTime date = (FDateTime) getFocPropertyByName("date_of_birth");
//        return (date != null) ? date.getDate() : null;
//    }

//    public void setDateOfBirth(Date d) {
//        FDateTime date = (FDateTime) getFocPropertyByName("date_of_birth");
//        if (date != null) {
//            date.setDate(d);
//        }
//    }

    //ooooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo
    // LISTENERS
    // oooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo
    private static FPropertyListener blockedListener = null;

    public FPropertyListener getBlockedListener() {
        if (blockedListener == null) {
            blockedListener = new FPropertyListener() {
                public void dispose() {
                }

                public void propertyModified(FProperty property) {
                }
            };
        }
        return blockedListener;
    }

}
