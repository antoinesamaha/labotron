package com.neofoc.app.modules.labotron.focObjects;

import com.foc.Globals;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListIterator;
import com.foc.property.*;

import java.awt.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 01Barmaja
 */
public class FocLabSample extends FocObject {
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
        setPropertyString("id", id);
        setPropertyMultiChoice("liquid_type", -1);
        setPropertyDate("entry_date", Globals.getApp().getSystemDate());
    }

    public FocLabSample(String id) {
        this(new FocConstructor(Globals.getApp().getFocDescByName("FocLabSample"), null, null));
        newFocProperties();
        initFocProperties(id);
        setEntryDate(Globals.getApp().getSystemDate());
    }

    public FocLabSample(FocConstructor constr) {
        super(constr);
        newFocProperties();
        initFocProperties("");
        setEntryDate(Globals.getApp().getSystemDate());
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

    public boolean existInDB() {
//        SQLSelectExistance selectExistance = new SQLSelectExistance(FocLabSample.getFocDesc(), new StringBuffer(L3SampleDesc.FNAME_ID + "=" + getId()));
//        selectExistance.execute();
//        boolean exists = selectExistance.getExist() == SQLSelectExistance.EXIST_YES;
//        selectExistance.dispose();
//        return exists;
        return false;
    }

    public static FocLabSample newDBSample(String id) {
//        FocLabSample sample = null;
//        FocList list = new FocList(L3SampleDesc.getFocLinkSimple());
//        SQLFilter sqlFilter = list.getFilter();
//        sqlFilter.putAdditionalWhere("SMPL_ID", L3SampleDesc.FNAME_ID + "=" + id);
//        list.loadIfNotLoadedFromDB();
//
//        if (list.size() == 1) {
//            sample = (FocLabSample) list.getFocObject(0);
//            list.remove(sample);
//        }
//        list.dispose();
//        return sample;
        return null;
    }

    public FocList getTestListWithoutLoad() {
        return getPropertyList("test_list");
    }

    public FocList getTestList() {
        FList list = (FList) getFocPropertyByName("test_list");
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
        setId(sample.getId());
        setPatientId(sample.getPatientId());
        setOrigin(sample.getOrigin());
        setLastName(sample.getLastName());
        setFirstName(sample.getFirstName());
        setMiddleInitial(sample.getMiddleInitial());
        setSexe(sample.getSexe());
        setAge(sample.getAge());
        setLiquidType(sample.getLiquidType());
        setEntryDate(sample.getEntryDate());
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
        if (sample.isOkToBeSent()) {
            setOkToBeSent(sample.isOkToBeSent());
        }
        if (sample.isResultConfirmed()) {
            setResultConfirmed(sample.isResultConfirmed());
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
        buff.append(getId() + " liq:" + getLiquidType() + "PatientId=" + getPatientId() + " FirstName=" + getFirstName() + " MidInitial=" + getMiddleInitial() + " LastName=" + getLastName() + " Sexe" + getSexe() + " Origin=" + getOrigin() + "\n");
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

    public long getDateAndTime() {
//        return getPropertyDate("entry_date");
        return -1; // Placeholder, as the original code does not implement this method
    }

    public void setDateAndTime(long dateAndTime) {
//        setPropertyDate(L3SampleDesc.FLD_ENTRY_DATE, new Date(dateAndTime));
    }

    public String getId() {
//        FString id = (FString) getFocProperty(L3SampleDesc.FLD_ID);
//        return (id != null) ? id.getString() : "";
        return "NOT_FILLED";
    }

    public void setId(String id) {
//        FString valId = (FString) getFocProperty(L3SampleDesc.FLD_ID);
//        if (valId != null) {
//            valId.setString(id);
//        }
    }

    public String getPatientId() {
        FString patientId = (FString) getFocPropertyByName("PATIENT_ID");
        return patientId != null ? patientId.getString() : "";
    }

    public void setPatientId(String patientId) {
        FString patientIdProp = (FString) getFocPropertyByName("PATIENT_ID");
        if (patientIdProp != null) {
            patientIdProp.setString(patientId);
        }
    }

    public String getOrigin() {
        FString origin = (FString) getFocPropertyByName("origin");
        return origin != null ? origin.getString() : "";
    }

    public void setOrigin(String origin) {
        FString originProp = (FString) getFocPropertyByName("origin");
        if (originProp != null) {
            originProp.setString(origin);
        }
    }

    public int getAge() {
        return getPropertyInteger("age");
    }

    public void setAge(int age) {
        setPropertyInteger("age", age);
    }

    public String getSexe() {
        FString sexe = (FString) getFocPropertyByName("sexe");
        return sexe != null ? sexe.getString() : "";
    }

    public void setSexe(String sexe) {
        FString sexeProp = (FString) getFocPropertyByName("sexe");
        if (sexeProp != null) {
            sexeProp.setString(sexe);
        }
    }

    public int getLiquidType() {
        FMultipleChoice liqType = (FMultipleChoice) getFocPropertyByName("liquid_type");
        return (liqType != null) ? liqType.getInteger() : null;
    }

    public void setLiquidType(int liquidType) {
        FInt liqType = (FInt) getFocPropertyByName("liquid_type");
        if (liqType != null) {
            liqType.setInteger(liquidType);
        }
    }

    public void setLiquidType(String liquidType) {
        FMultipleChoice liqType = (FMultipleChoice) getFocPropertyByName("liquid_type");
        if (liqType != null) {
            liqType.setString(liquidType);
        }
    }

    public String getFirstName() {
        FString fName = (FString) getFocPropertyByName("first_name");
        return (fName != null) ? fName.getString() : "";
    }

    public void setFirstName(String firstName) {
        FString fName = (FString) getFocPropertyByName("first_name");
        if (fName != null) {
            fName.setString(firstName);
        }
    }

    public String getLastName() {
        FString lName = (FString) getFocPropertyByName("last_name");
        return (lName != null) ? lName.getString() : "";
    }

    public void setLastName(String lastName) {
        FString lName = (FString) getFocPropertyByName("last_name");
        if (lName != null) {
            lName.setString(lastName);
        }
    }

    public String getMiddleInitial() {
        FString mid = (FString) getFocPropertyByName("middle_initial");
        return (mid != null) ? mid.getString() : "";
    }

    public void setMiddleInitial(String middleInitial) {
        FString mid = (FString) getFocPropertyByName("middle_initial");
        if (mid != null) {
            if (middleInitial == null || middleInitial.compareTo("null") == 0) {
                middleInitial = "";
            }
            mid.setString(middleInitial);
        }
    }

    public boolean isResultConfirmed() {
        FBoolean confirmed = (FBoolean) getFocPropertyByName("result_confirmed");
        return (confirmed != null) ? confirmed.getBoolean() : null;
    }

    public void setResultConfirmed(boolean confirmed) {
        FBoolean c = (FBoolean) getFocPropertyByName("result_confirmed");
        if (c != null) {
            c.setBoolean(confirmed);
        }
    }

    public boolean isOkToBeSent() {
        FBoolean ok = (FBoolean) getFocPropertyByName("ok_to_be_sent");
        return (ok != null) ? ok.getBoolean() : null;
    }

    public void setOkToBeSent(boolean ok) {
        FBoolean o = (FBoolean) getFocPropertyByName("ok_to_be_sent");
        if (o != null) {
            o.setBoolean(ok);
        }
    }

    public Date getEntryDate() {
        FDateTime date = (FDateTime) getFocPropertyByName("entry_date");
        return (date != null) ? date.getDate() : null;
    }

    public void setEntryDate(Date d) {
        FDateTime date = (FDateTime) getFocPropertyByName("entry_date");
        if (date != null) {
            date.setDate(d);
        }
    }

    public Date getDateOfBirth() {
        FDateTime date = (FDateTime) getFocPropertyByName("date_of_birth");
        return (date != null) ? date.getDate() : null;
    }

    public void setDateOfBirth(Date d) {
        FDateTime date = (FDateTime) getFocPropertyByName("date_of_birth");
        if (date != null) {
            date.setDate(d);
        }
    }

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
