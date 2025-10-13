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
    "sampleId": "5565802",
    "sampleType": "Urin",
    "patientId": "999",
    "firstName": "Samir",
    "lastName": "Salloum",
    "middleInitial": "Melhem",
    "sampleType": "Urin",
    "patientId": "999",
    "firstName": "Samir",
    "lastName": "Salloum",
    "middleInitial": "Melhem",
    "dateOfBirth": "2005-10-08",
    "sex": "Male",
    "currentDateTime": "2025-10-08 15:00:00",
    "collectionDate": "2025-10-08 15:00:00",
    "origin": "lab",
    "tests": [
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