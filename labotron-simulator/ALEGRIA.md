# Original JSON
```json
{
    "sampleId": "5586677",
    "sampleType": "Urin",
    "patientId": "7897897",
    "firstName": "Kamil",
    "lastName": "Mansour",
    "middleInitial": "Samih",
    "sampleType": "Urin",
    "patientId": "797979",
    "firstName": "Samir",
    "lastName": "Salloum",
    "middleInitial": "Melhem",
    "dateOfBirth": "2005-10-08",
    "sex": "Male",
    "currentDateTime": "2025-11-23",
    "collectionDate": "2025-11-23",
    "origin": "lab",
    "tests": [
        {
            "testCode": "558",
            "testDesc": "Anti SS B test"
        },     
        {
            "testCode": "551",
            "testDesc": "Anti SS A test"
        }
    ]
}
```

# Modified JSON
```json
{
    "P_SAMPLE_ID" : "5647687",
    "CURRENT_DATE_TIME" : "04/12/2025",
    "SAMPLE_TYPE" : "SER",
    "COLLECTION_DATE" : "04/12/2025",
    "PATIENT_NAME" : "Said M Osman",
    "AGE" : "73",
    "GENDER" : "M",
    "PATIENT_ID" : "834121",
    "DATE_OF_BIRTH" : "17/08/1952",
    "ORIGIN" : "6FN",
    "LISTEST" : [
        {
            "SEQ_ID" : "40094876",
            "ACTUAL_TEST_ID" : "71347673",
            "SAMPLE_ID" : "5647687",
            "TEST_CODE" : "6594",
            "TEST_DESC" : "ENAscreen",
            "STATUS" : "0",
            "ANALYZER_CODE" : "",
            "ACTUAL_ANALYZER_CODE" : "",
            "RESULT" : "",
            "UNIT" : "",
            "MESSAGE" : "",
            "ALARM" : "",
            "PRIORITY" : "R",
            "VERIFICATION_PENDING" : "",
            "DATE_READ" : "04/12/2025",
            "RESULT_DATETIME" : "",
            "NOTES" : ""
        },
        {
            "SEQ_ID" : "40094877",
            "ACTUAL_TEST_ID" : "71347672",
            "SAMPLE_ID" : "5647687",
            "TEST_CODE" : "6696",
            "TEST_DESC" : "GBM",
            "STATUS" : "0",
            "ANALYZER_CODE" : "",
            "ACTUAL_ANALYZER_CODE" : "",
            "RESULT" : "",
            "UNIT" : "",
            "MESSAGE" : "",
            "ALARM" : "",
            "PRIORITY" : "R",
            "VERIFICATION_PENDING" : "",
            "DATE_READ" : "04/12/2025",
            "RESULT_DATETIME" : "",
            "NOTES" : ""
        },
        {
            "SEQ_ID" : "40094878",
            "ACTUAL_TEST_ID" : "71347670",
            "SAMPLE_ID" : "5647687",
            "TEST_CODE" : "6586",
            "TEST_DESC" : "ssDNA",
            "STATUS" : "0",
            "ANALYZER_CODE" : "",
            "ACTUAL_ANALYZER_CODE" : "",
            "RESULT" : "",
            "UNIT" : "",
            "MESSAGE" : "",
            "ALARM" : "",
            "PRIORITY" : "R",
            "VERIFICATION_PENDING" : "",
            "DATE_READ" : "04/12/2025",
            "RESULT_DATETIME" : "",
            "NOTES" : ""
        },
        {
            "SEQ_ID" : "40094879",
            "ACTUAL_TEST_ID" : "71347671",
            "SAMPLE_ID" : "5647687",
            "TEST_CODE" : "6685",
            "TEST_DESC" : "Gliadin A",
            "STATUS" : "0",
            "ANALYZER_CODE" : "",
            "ACTUAL_ANALYZER_CODE" : "",
            "RESULT" : "",
            "UNIT" : "",
            "MESSAGE" : "",
            "ALARM" : "",
            "PRIORITY" : "R",
            "VERIFICATION_PENDING" : "",
            "DATE_READ" : "04/12/2025",
            "RESULT_DATETIME" : "",
            "NOTES" : ""
        }
    ]
}
```