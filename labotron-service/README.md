# Deployment
Create a folder labotron under opt

```
cd /opt
sudo mkdir labotron
sudo chown administrator:administrator labotron
```

Install java 21

```
sudo apt update 
sudo apt istall -y openjdk-21-jdk
```

Install maven

```
wget https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz
sudo tar -xvzf apache-maven-3.8.8-bin.tar.gz -C /opt
nano ~/.bashrc
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
source ~/.bashrc
```

Install the comm.jar
```
cd /labotron/labotron-service/src/main/resources/jar
./installJar.sh
```

You might need to give execution rights to the user
```
sudo chmod u+x installJar.sh
```

Build the backend image
```
cd /opt/labotron/labotron-service
docker build -t labotron-service:latest .
```

Installing Flutter

Prerequisits
```
sudo apt update -y
sudo apt install -y git curl unzip xz-utils zip libglu1-mesa
```

Installing Flutter itself
get on the home directory then run the following commands
```
git clone https://github.com/flutter/flutter.git -b stable
export PATH="$PATH:$HOME/flutter/bin"
flutter doctor
```

For a permanent config of the PATH variable, add the export line to the ~/.bashrc file.
```
nano ~/.bashrc
OR
vi ~/.bashrc
```

```
export PATH="$PATH:$HOME/flutter/bin"
```



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