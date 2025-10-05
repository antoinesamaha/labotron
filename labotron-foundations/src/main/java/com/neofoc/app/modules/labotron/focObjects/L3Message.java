package com.neofoc.app.modules.labotron.focObjects;

import com.neofoc.app.modules.labotron.LabSample;

import java.util.ArrayList;
import java.util.Iterator;

public class L3Message {
	private String instrumentCode = null;
	private ArrayList<FocLabSample> sampleList = null;

	public L3Message() {

	}

	public void dispose() {
		if (sampleList != null) {
			Iterator iter = sampleIterator();
			while (iter != null && iter.hasNext()) {
				FocLabSample sam = (FocLabSample) iter.next();
				sam.dispose();
			}
			sampleList.clear();
			sampleList = null;
		}
	}

	public void addSample(FocLabSample sam) {// set the status of the sample to
											// available in db
		if (sampleList == null) {
			sampleList = new ArrayList<FocLabSample>();
		}
		sampleList.add(sam);
	}

	public void removeSample(LabSample sam) {
		if (sampleList != null) {
			sampleList.remove(sam);
		}
	}

	public int getNumberOfSamples() {
		return sampleList != null ? sampleList.size() : 0;
	}

	public Iterator<FocLabSample> sampleIterator() {
		return sampleList != null ? sampleList.iterator() : null;
	}

	public FocLabSample getSample(int position) {
		FocLabSample sample = null;
		if (sampleList != null && position < getNumberOfSamples()) {
			sample = sampleList.get(position);
		}
		return sample;
	}

	public FocLabSample findSample(String sampleID) {
		FocLabSample sample = null;
		for (int i = 0; i < getNumberOfSamples() && sample == null; i++) {
			FocLabSample spl = getSample(i);
			if (spl != null && spl.getId().compareTo(sampleID) == 0) {
				sample = spl;
			}
		}
		return sample;
	}

	public void upgradeMessageSamples_ToResultsAvailable(FocInstrument instrument) {
		// if all the samples have the same status and we know it we give it else we give a <0 numbre
//		Savepoint savepoint = Globals.getDBManager().beginTransactionWithSavepoint();
//		try {
//			Iterator sampleIterator = sampleIterator();
//			while (sampleIterator != null && sampleIterator.hasNext()) {
//				FocLabSample memorySample = (FocLabSample) sampleIterator.next();
//				if (memorySample.getId() != null && memorySample.getId().trim().compareTo("") != 0) {
//					memorySample.resetStatusWithPropagation();
//
//					FocList sampleTestList = L3SampleTestJoinDesc.newList();
//
//					FocConstructor constr = new FocConstructor(L3SampleTestJoinFilter.getFocDesc(), null);
//					L3SampleTestJoinFilter filter = (L3SampleTestJoinFilter) constr.newItem();
//					filter.setFocList(sampleTestList);
//					filter.setSampleID(memorySample.getId());
//					filter.setFilterLevel(FocListFilter.LEVEL_DATABASE);
//					filter.setActive(true);
//
//					boolean sampleExisted = sampleTestList.size() > 0;
//					if (!sampleExisted) {
//						Globals.logString("Received from instrument new Sample : " + memorySample.getId());
//						SQLInsert insert = new SQLInsert(FocLabSample.getFocDesc(), memorySample);
//						insert.execute();
//						memorySample.setCreated(false);
//					} else {
//						L3SampleTestJoin join = (L3SampleTestJoin) sampleTestList.getAnyItem();
//
//						FReference fRef = (FReference) join.getFocProperty(FField.REF_FIELD_ID);
//						if (fRef.getInteger() > 0) {
//							memorySample.setReference(fRef.getInteger());
//							Globals.logString("Received from instrument existing sample : " + memorySample.getId());
//						} else {
//							throw new L3Exception("Could not get Sample reference!");
//						}
//					}
//
//					FocList messageList = memorySample.getInstrumentMessageListWithoutLoad();
//					if (messageList != null && messageList.size() > 0) {
//						for (int i = 0; i < messageList.size(); i++) {
//							L3InstrumentMessage instrumentMessage = (L3InstrumentMessage) messageList.getFocObject(i);
//							instrumentMessage.resetStatus();
//
//							FocList listOfInstrumentMessages = L3InstrumentMessageDesc.newList();
//							SQLFilter messagesFilter = listOfInstrumentMessages.getFilter();
//							messagesFilter.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
//							messagesFilter.setObjectTemplate(instrumentMessage);
//							messagesFilter.addSelectedField(L3InstrumentMessageDesc.FLD_INSTRUMENT);
//							messagesFilter.addSelectedField(L3InstrumentMessageDesc.FLD_L3_SAMPLE);
//
//							listOfInstrumentMessages.loadIfNotLoadedFromDB();
//							if (listOfInstrumentMessages.size() > 0) {
//								L3InstrumentMessage foundMessage = (L3InstrumentMessage) listOfInstrumentMessages.getAnyItem();
//								foundMessage.setMessage(instrumentMessage.getMessage());
//								foundMessage.setStatus(L3TestDesc.TEST_STATUS_RESULT_AVAILABLE);
//								foundMessage.validate(false);
//								listOfInstrumentMessages.validate(false);
//							} else {
//								instrumentMessage.setCreated(true);
//								instrumentMessage.setStatus(L3TestDesc.TEST_STATUS_RESULT_AVAILABLE);
//								instrumentMessage.commitStatusToDatabase();
//							}
//
//							listOfInstrumentMessages.removeAll();
//							listOfInstrumentMessages.dispose();
//							listOfInstrumentMessages = null;
//						}
//						messageList.validate(false);
//					}
//
//					FocList testList = memorySample.getTestListWithoutLoad();
//					Iterator iter = testList.focObjectIterator();
//					while (iter != null && iter.hasNext()) {
//						FocLabTest memoryTest = (FocLabTest) iter.next();
//
//						memoryTest.setStatus(L3TestDesc.TEST_STATUS_RESULT_AVAILABLE);
//						memoryTest.setPropertyObject(L3TestDesc.FLD_RECEIVE_INSTRUMENT, instrument);
//
//						L3SampleTestJoin sampleTestJoin = (L3SampleTestJoin) sampleTestList.searchByProperyStringValue(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL,
//								memoryTest.getLabel());
//
//						boolean testExisted = sampleExisted && sampleTestJoin != null;
//						if (testExisted) {
//							FReference fRef = (FReference) sampleTestJoin.getFocProperty(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + FField.REF_FIELD_ID);
//							if (fRef.getInteger() > 0) {
//								memoryTest.setReference(fRef.getInteger());
//								SQLUpdate update = new SQLUpdate(FocLabTest.getFocDesc(), memoryTest);
//
//								memoryTest.setPropertyObject(L3TestDesc.FLD_RECEIVE_INSTRUMENT, instrument);
//								if (sampleTestJoin.getPropertyMultiChoice(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_STATUS) == L3TestDesc.TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED) {
//									memoryTest.setStatus(L3TestDesc.TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED);
//								}
//
//								update.addQueryField(L3TestDesc.FLD_STATUS);
//								if (memoryTest.getNotificationMessage().trim().compareTo("") != 0) {
//									update.addQueryField(L3TestDesc.FLD_MESSAGE);
//								}
//								update.addQueryField(L3TestDesc.FLD_VALUE);
//								update.addQueryField(L3TestDesc.FLD_VALUE_NOTES);
//								update.addQueryField(L3TestDesc.FLD_ALARM);
//								update.addQueryField(L3TestDesc.FLD_VERIFICATION_PENDING);
//								update.addQueryField(L3TestDesc.FLD_RESULT_OK);
//								update.addQueryField(L3TestDesc.FLD_UNIT_LABEL);
//								update.addQueryField(L3TestDesc.FLD_RESULT_OK);
//								update.addQueryField(L3TestDesc.FLD_RECEIVE_INSTRUMENT);
//								update.execute();
//							}
//						} else {
//							memoryTest.setStatus(L3TestDesc.TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED);
//							memoryTest.setNotificationMessage("Test was not in L3 when received");
//							SQLInsert insert = new SQLInsert(FocLabTest.getFocDesc(), memoryTest);
//							insert.execute();
//						}
//					}
//
//					memorySample.saveGraph(instrument);
//
//					sampleTestList.dispose();
//					filter.dispose();
//				} else {
//					Globals.logString(" !! The Sample ID is empty could not treat result !! ");
//				}
//			}
//
//			Globals.getDBManager().commitTransaction();
//		} catch (Exception e) {
//			Globals.logException(e);
//			Globals.getDBManager().rollbackTransaction(savepoint);
//		}
	}

	public StringBuffer toStringBuffer() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("Message:");
		Iterator sIter = sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			FocLabSample sam = (FocLabSample) sIter.next();
			buffer.append(sam.toStringBuffer());
		}

		return buffer;
	}

	public String getInstrumentCode() {
		return instrumentCode;
	}

	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}
}
