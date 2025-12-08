/*
 * Created on Jun 14, 2006
 */
package com.neofoc.app.modules.labotron.focObjects;

import com.foc.Globals;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.focDataSourceDB.db.SQLUpdate;
import com.foc.gui.FPanel;
import com.foc.list.FocList;
import com.foc.property.FBoolean;
import com.foc.property.FDouble;
import com.foc.property.FString;
import com.foc.util.FocMath;
import com.neofoc.app.modules.labotron.LabTest_FocObject;

/**
 * @author 01Barmaja
 */
public class FocLabTest extends LabTest_FocObject {

	public static final String TABLE_NAME          = "L3TEST"     ;

	public static final String FNAME_SAMPLE_PREFIX = "SAMPLE_"    ;
	public static final String FNAME_LABEL         = "label"      ;
	public static final String FNAME_VALUE         = "value"      ;
	public static final String FNAME_VALUE_NOTES   = "notes"      ;
	public static final String FNAME_RESULT_OK     = "result_ok"  ;
	public static final String FNAME_STATUS        = "status"     ;
	public static final String FNAME_BLOCKED       = "blocked"    ;
	public static final String FNAME_MESSAGE       = "message"    ;
	public static final String FNAME_UNIT          = "unit_label" ;
	public static final String FNAME_ALARM         = "alarm"      ;
	public static final String FNAME_PRIORITY             = "priority"             ;
	public static final String FNAME_VERIFICATION_PENDING = "verification_pending" ;

	public static final int LEN_TEST_LABEL       = 15;
	public static final int LEN_TEST_DESCRIPTION = 25;
	public static final int LEN_VALUE_NOTES      = 40;
	public static final int LEN_VALUE            = 10;
	public static final int LEN_VALUE_DECIMAL    = 4 ;
	public static final int LEN_UNIT_LABEL       = 10;
	public static final int LEN_MESSAGE          = 100;

	public static final int TEST_STATUS_AVAILABLE_IN_L3 = 0;
	//public static final int SAMPLE_STATUS_READY_TO_BE_SENT = 1;
	public static final int TEST_STATUS_SENDING_TO_INSTRUMENT = 2;
	public static final int TEST_STATUS_ANALYSING = 3;
	public static final int TEST_STATUS_RESULT_AVAILABLE = 4;
	public static final int TEST_STATUS_COMMITED_TO_LIS = 5;
	public static final int TEST_STATUS_NOT_IN_USE = 6;
	public static final int TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED = 7;
	public static final int TEST_STATUS_NOT_IN_LIS_WHEN_COMMITING_RESULT = 8;
	public static final int TEST_STATUS_MULTIPLE_LIS_ENTRIES_UPON_COMMIT = 9;
	public static final int TEST_STATUS_ERROR_WIHLE_COMMIT_TO_LIS        = 10;
	public static final int TEST_STATUS_NOT_ASSIGNED = 99;

	private double roundingPrecision = 0;

	public static final int TEST_RESULT_LESS_THAN = -1;
	public static final int TEST_RESULT_EMPTY_ALARM = 0;
	public static final int TEST_RESULT_GREATER_THAN = 1;

	public static final double VALUE_NULL = -99999;

	public FocLabTest(String label) {
		this(new FocConstructor(Globals.getApp().getFocDescByName("lab_test"), null, null));
		initFocProperties(label);
	}

	public FocLabTest(FocConstructor constr) {
		super(constr);
		initFocProperties("");
	}

	private void initFocProperties(String label) {
		setPropertyString("label", label);
	}

	public void dispose() {
		super.dispose();
	}

	public void setBlocked(boolean blocked) {
		setPropertyBoolean("blocked", blocked);
	}

	public boolean isBlocked() {
		return getPropertyBoolean("blocked");
	}

	public void setNotificationMessage(String message) {
//		if (message.length() > FocLabTestDesc.LEN_MESSAGE) {
//			message = message.substring(0, L3TestDesc.LEN_MESSAGE - 1);
//		}
//		setPropertyString(L3TestDesc.FLD_MESSAGE, message);
	}

	public String getNotificationMessage() {
		return getPropertyString("message");
	}

	public void updateStatus(int status) throws Exception {
		FocDesc focDesc = getThisFocDesc();
		if (focDesc != null) {
			setStatus(status);
			SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
			sqlUpdate.addQueryField(focDesc.getFieldIDByName(FNAME_STATUS));
			sqlUpdate.execute();
		}
		// backup();
	}

	public void updateBlocked(Boolean blocked) {
//		FocDesc focDesc = getThisFocDesc();
//		if (focDesc != null) {
//			setBlocked(blocked);
//			SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
//			sqlUpdate.addQueryField(L3TestDesc.FLD_BLOCKED);
//			sqlUpdate.execute();
//		}
	}

	public void updateNotificationMessage(String message) {
//		FocDesc focDesc = getThisFocDesc();
//		if (focDesc != null) {
//			setNotificationMessage(message);
//			SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
//			sqlUpdate.addQueryField(L3TestDesc.FLD_MESSAGE);
//			sqlUpdate.execute();
//		}
	}

	public StringBuffer toStringBuffer() {
		StringBuffer b = new StringBuffer();
		b.append(" isOk=" + isResultOk() + " Label=" + getLabel() + " Value=" + getValue() + " Notes=" + getValueNotes());
		return b;
	}

	public String getLabel() {
		return getPropertyString("label");
	}

	public void setLabel(String value) {
		setPropertyString("label", value);
	}

	public String getValueNotes() {
		return getPropertyString("value_notes");
	}

	public void setValueNotes(String value) {
		setPropertyString("value_notes", value);
	}

	public boolean isResultOk() {
		return getPropertyBoolean("result_ok");
	}

	public void setResultOk(boolean resultOk) {
		setPropertyBoolean("result_ok", resultOk);
	}

	public double getValue() {
		return getPropertyDouble("value");
	}

	public void setValue(double value) {
  	FDouble val = (FDouble) getFocPropertyByName("value");
    if(val != null){
      if(getRoundingPrecision() != 0){
      	Globals.logString("Rounding value = "+value+" to precision = "+getRoundingPrecision());
      	value = FocMath.round(value, getRoundingPrecision());
      	Globals.logString("         Rounding result = "+value);
      }
      val.setDouble(value);
    }
  }
	
	public double getRoundingPrecision() {
		return roundingPrecision;
	}

	public void setRoundingPrecision(double roundingPrecision) {
		this.roundingPrecision = roundingPrecision;
	}

	public void setAlarm(int alarm) {
		//setPropertyMultiChoiceByName(getThisFocDesc().getFieldByName("alarm"), alarm);
	}

	public int getAlarm() {
//		return getPropertyMultiChoice(L3TestDesc.FLD_ALARM);
		return 0;
	}

	public void setPriority(String priority) {
		setPropertyString("priority", priority);
	}

	public String getPriority() {
		return getPropertyString("priority");
	}

	public void setVerificationPendingFlag(boolean verifPending) {
		setPropertyBoolean("verification_pending", verifPending);
	}

	public boolean isVerificationPendingFlag() {
		return getPropertyBoolean("verification_pending");
	}

	public String getUnitLabel() {
		FString unit = (FString) getFocPropertyByName("unit_label");
		return (unit != null) ? unit.getString() : "";
	}

	public void setUnitLabel(String unitLabel) {
		FString ul = (FString) getFocPropertyByName("unit_label");
		if (ul != null) {
			ul.setString(unitLabel);
		}
	}

	public void copy(FocLabTest t) {
		setLabel(t.getLabel());
		setValue(t.getValue());
		setValueNotes(t.getValueNotes());
		setResultOk(t.isResultOk());
		setUnitLabel(t.getUnitLabel());
		setPropertyString("message", t.getPropertyString("message"));
	}

	public void copyAndBackup(FocLabTest t) {
		copy(t);
		backup();
	}

	public void remove() {
//		SQLFilter deleteFilter = new SQLFilter(this, SQLFilter.FILTER_ON_SELECTED);
//		StringBuffer deleteCondition = new StringBuffer("REF = " + getReference().getInteger());
//		deleteFilter.addAdditionalWhere(deleteCondition);
//		SQLDelete delete = new SQLDelete(getFocDesc(), deleteFilter);
//		delete.execute();
	}

}
