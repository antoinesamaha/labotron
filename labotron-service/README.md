# Messages Exchanged with LIS

## LIS Sending Sample to Labotron

```
{
    "sampleId": "<string>",
    "sampleType": "<string>",
    "patientId": "<string>",
    "firstName": "<string>",
    "lastName": "<string>",
    "middleInitial": "<string>",
    "dateOfBirth": "<ISO8601_date_string>",
    "sex": "<string>",
    "currentDateTime": "<ISO8601_date_string>",
    "collectionDate": "<ISO8601_date_string>",
    "origin": "<string>",
    "tests": [
        {
            "testId": "<string> : actualTestId",
            "testCode": "<string>",
            "testDesc": "<string>",
            "status": "<string>",
            "analyzerCode": "<string> Optional",
            "actualAnalyzerCode": "<string>",
            "alarm": "<boolean>",
            "result": "<number|string>",
            "notes": "<string>",
            "unit": "<string>",
            "message": "<string>",
            "priority": "<string>",
            "verificationPending": "<boolean>"
        }
    ]
}
```

### Example message
```
{
    "sampleId": "111",
    "sampleType": "Urin",
    "patientId": "999",
    "firstName": "Samir",
    "lastName": "Salloum",
    "middleInitial": "Melhem",
    "dateOfBirth": "23/05/2002",
    "sex": "Male",
    "currentDateTime": "08/08/2025 15:00:00",
    "collectionDate": "08/08/2025",
    "origin": "lab",
    "tests": [
        {
            "testCode": "HbA1c",
            "testDesc": "HbA1c test"
        },
        {
            "testCode": "HbA1c_2",
            "testDesc": "HbA1c test"
        },
        {
            "testCode": "INF01",
            "testDesc": "Infinity test"
        }     
    ]
}
```

## Labotron returning messages to LIS

```
{
    "sampleId": "<string>",        
    "testId": "<string> : actualTestId",
    "status": "<string>",
    "actualAnalyzerCode": "<string>",
    "alarm": "<boolean>",
    "result": "<number|string>",
    "notes": "<string>",
    "unit": "<string>",
    "message": "<string>",
    "verificationPending": "<boolean>"
}
```