package com.neofoc.app.drivers.astm;

public class AstmParams {
	private int resultFrame_ComponentPositionForResultType = 7;
	private int testCodeLength = 3;
	private boolean ignoreLastTestCodeDigit = false;
	private boolean checkResultFrameTestCodeWithOrderFrameTestCode = true;
	private boolean concatenatedFrames = false;
	private boolean putYForAGE = true;

	private boolean readComment1      = false;
	private boolean readComment2      = false;
	private boolean readComment3      = false;
	private boolean readResultComment = false;
	
	public boolean treatHigherLessAlarmSeparately = true;
	private int sampleID_FieldPosition     = -1;
	private int sampleID_ComponentPosition = -1;
	private boolean sendCommentFrameFromHost = false;
	private boolean sendPatientIdToInstrument = false;
	private boolean sendProfileInsteadOfTestID = false;//Good for Pentra - CBC and DIFF...
	private boolean sendPatientAgeAndSex       = false;//Good for Pentra - CBC and DIFF...
	private boolean sendPatientDateOfBirth     = false;
	private boolean yieldOnEnqCollision           = false;
	private boolean instrumentIsAstmMaster        = false;
	private boolean acceptToStartAtFrame1Directly = false;
	private boolean takeAllFramesFromBufferNotJustTheLast = false;
	private boolean releaseWhenReceivedENQ = false;
	//20160129-B
	private boolean doNotSendOrdersBecauseOneWay = false;
	//20160129-E
	private boolean usePatientIdAsSampleId = false;
	
	//20160129-B
	public boolean isDoNotSendOrdersBecauseOneWay() {
		return doNotSendOrdersBecauseOneWay;
	}

	public void setDoNotSendOrdersBecauseOneWay(boolean doNotSendOrdersBecauseOneWay) {
		this.doNotSendOrdersBecauseOneWay = doNotSendOrdersBecauseOneWay;
	}
	//20160129-E

	public boolean isReleaseWhenReceivedENQ(){
		return releaseWhenReceivedENQ;
	}
	
	public void setReleaseWhenReceivedENQ(boolean releaseWhenReceivedENQ){
		this.releaseWhenReceivedENQ = releaseWhenReceivedENQ;
	}
	
	public boolean isYieldOnEnqCollision() {
		return yieldOnEnqCollision;
	}

	public void setYieldOnEnqCollision(boolean yieldOnEnqCollision) {
		this.yieldOnEnqCollision = yieldOnEnqCollision;
	}

	public boolean isInstrumentIsAstmMaster() {
		return instrumentIsAstmMaster;
	}

	public void setInstrumentIsAstmMaster(boolean instrumentIsAstmMaster) {
		this.instrumentIsAstmMaster = instrumentIsAstmMaster;
	}

	public boolean isSendPatientAgeAndSex() {
		return sendPatientAgeAndSex;
	}

	public void setSendPatientAgeAndSex(boolean sendPatientAgeAndSex) {
		this.sendPatientAgeAndSex = sendPatientAgeAndSex;
	}

	public boolean isSendProfileInsteadOfTestID() {
		return sendProfileInsteadOfTestID;
	}

	public void setSendProfileInsteadOfTestID(boolean sendProfileInsteadOfTestID) {
		this.sendProfileInsteadOfTestID = sendProfileInsteadOfTestID;
	}

	public boolean isSendPatientIdToInstrument() {
		return sendPatientIdToInstrument;
	}

	public void setSendPatientIdToInstrument(boolean sendPatientIdToInstrument) {
		this.sendPatientIdToInstrument = sendPatientIdToInstrument;
	}

	public boolean isSendCommentFrameFromHost() {
		return sendCommentFrameFromHost;
	}

	public void setSendCommentFrameFromHost(boolean sendCommentFrameFromHost) {
		this.sendCommentFrameFromHost = sendCommentFrameFromHost;
	}

	public int getSampleID_FieldPosition() {
		return sampleID_FieldPosition;
	}

	public void setSampleID_FieldPosition(int sampleIDFieldPosition) {
		sampleID_FieldPosition = sampleIDFieldPosition;
	}

	public int getSampleID_ComponentPosition() {
		return sampleID_ComponentPosition;
	}

	public void setSampleID_ComponentPosition(int sampleIDComponentPosition) {
		sampleID_ComponentPosition = sampleIDComponentPosition;
	}

	public AstmParams(){
		resultFrame_ComponentPositionForResultType = 7;
		testCodeLength = 3;
	}

	public int getResultFrame_ComponentPositionForResultType() {
		return resultFrame_ComponentPositionForResultType;
	}

	public void setResultFrame_ComponentPositionForResultType(int resultFrame_ComponentPositionForResultType) {
		this.resultFrame_ComponentPositionForResultType = resultFrame_ComponentPositionForResultType;
	}

	public int getTestCodeLength() {
		return testCodeLength;
	}

	public void setTestCodeLength(int testCodeLength) {
		this.testCodeLength = testCodeLength;
	}
	
	public void setIgnoreLastTestCodeDigit(boolean ignoreLastTestCodeDigit) {
		this.ignoreLastTestCodeDigit = ignoreLastTestCodeDigit;		
	}
	
	public boolean isIgnoreLastTestCodeDigit() {
		return ignoreLastTestCodeDigit;
	}
	
	public boolean isCheckResultFrameTestCodeWithOrderFrameTestCode() {
		return checkResultFrameTestCodeWithOrderFrameTestCode;
	}

	public void setCheckResultFrameTestCodeWithOrderFrameTestCode(boolean checkResultFrameTestCodeWithOrderFrameTestCode) {
		this.checkResultFrameTestCodeWithOrderFrameTestCode = checkResultFrameTestCodeWithOrderFrameTestCode;
	}

	public boolean isConcatenatedFrames() {
		return concatenatedFrames;
	}

	public void setConcatenatedFrames(boolean concatenatedFrames) {
		this.concatenatedFrames = concatenatedFrames;
	}

	public boolean isReadComment3() {
		return readComment3;
	}

	public void setReadComment3(boolean readCommentFrames) {
		this.readComment3 = readCommentFrames;
	}

	public boolean isReadComment1() {
		return readComment1;
	}

	public void setReadComment1(boolean readComment1) {
		this.readComment1 = readComment1;
	}

	public boolean isReadComment2() {
		return readComment2;
	}

	public void setReadComment2(boolean readComment2) {
		this.readComment2 = readComment2;
	}

	public boolean isReadResultComment() {
		return readResultComment;
	}

	public void setReadResultComment(boolean readResultComment) {
		this.readResultComment = readResultComment;
	}

	public boolean isReadAnyComment(){
		return isReadComment1() || isReadComment2() || isReadComment3();
	}
	
	public boolean doTreatHigherLessAlarmSeparately(){
		return treatHigherLessAlarmSeparately;
	}
	public void setTreatHigherLessAlarmSeparately(boolean treatHigherLessAlarmSeparately){
		this.treatHigherLessAlarmSeparately = treatHigherLessAlarmSeparately;
	}

	public boolean isSendPatientDateOfBirth() {
		return sendPatientDateOfBirth;
	}

	public void setSendPatientDateOfBirth(boolean sendPatientDateOfBirth) {
		this.sendPatientDateOfBirth = sendPatientDateOfBirth;
	}

	public boolean isAcceptToStartAtFrame1Directly() {
		return acceptToStartAtFrame1Directly;
	}

	public void setAcceptToStartAtFrame1Directly(boolean acceptToStartAtFrame1Directly) {
		this.acceptToStartAtFrame1Directly = acceptToStartAtFrame1Directly;
	}

	public boolean isTakeAllFramesFromBufferNotJustTheLast() {
		return takeAllFramesFromBufferNotJustTheLast;
	}

	public void setTakeAllFramesFromBufferNotJustTheLast(boolean readReceivedFramesFromFirstToEnd) {
		this.takeAllFramesFromBufferNotJustTheLast = readReceivedFramesFromFirstToEnd;
	}
	
	public boolean isUsePatientIdAsSampleId() {
		return usePatientIdAsSampleId;
	}

	public void setUsePatientIdAsSampleId(boolean usePatientIdAsSampleId) {
		this.usePatientIdAsSampleId = usePatientIdAsSampleId;
	}

	public boolean isPutYForAGE() {
		return putYForAGE;
	}

	public void setPutYForAGE(boolean putYForAGE) {
		this.putYForAGE = putYForAGE;
	}
}
