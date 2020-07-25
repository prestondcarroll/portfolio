#include <FirebaseArduino.h>
#include <FirebaseObject.h>
#include <ESP8266WiFi.h>        // Include the Wi-Fi library

const char* ssid     = "AndroidAP";         // The SSID (name) of the Wi-Fi network you want to connect to
const char* password = "vjkg4634";     // The password of the Wi-Fi network

String USER_ID = "0fad44VRv4apZSB475auROv1mKM2";
String DEVICE_NAME  = "j";

String TRIGGER_PATH = "users/" + USER_ID + "/devices/" + DEVICE_NAME + "/triggerEvents";
String ARMED_PATH = "users/" + USER_ID + "/devices/" + DEVICE_NAME + "/isArmed";

#define FIREBASE_HOST "test-e0c09.firebaseio.com"
#define FIREBASE_AUTH "6KyXqvrDki0V72iqwYQycLQqwvMPGL2oqw1wlxY3"

boolean isArmed;

int value = 1;
int sensorPin = D5;

void setup() {
  Serial.begin(115200);         // Start the Serial communication to send messages to the computer

  pinMode(sensorPin, INPUT);
  
  delay(10);
  Serial.println('\n');
  
  WiFi.begin(ssid, password);             // Connect to the network
  Serial.print("Connecting to ");
  Serial.print(ssid); Serial.println(" ...");

  int i = 0;
  while (WiFi.status() != WL_CONNECTED) { // Wait for the Wi-Fi to connect
    delay(1000);
    Serial.print(++i); Serial.print(' ');
  }

  Serial.println('\n');
  Serial.println("Connection established!");  
  Serial.print("IP address:\t");
  Serial.println(WiFi.localIP());         // Send the IP address of the ESP8266 to the computer
  delay(1000);

  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  if(Firebase.success()){
    Serial.println("Firebase Connection Success");
  }
}

//todo check if device exists 
void loop() {
    isArmed = Firebase.getBool(ARMED_PATH);
    if(isArmed){
        value = digitalRead(sensorPin);
        if(value == 1){
          Serial.println("Motion Detected");
          StaticJsonBuffer<50> jsonBuffer;
          JsonObject& timeStampObject = jsonBuffer.createObject();
          timeStampObject[".sv"] = "timestamp";
          Firebase.push(TRIGGER_PATH, timeStampObject);
          if (Firebase.failed()) { // Check for errors 
            Serial.print("setting /number failed:");
            Serial.println(Firebase.error());
            delay(4000);
            return;
          }  
        Serial.println("Device Online");
        delay(3000);      
        }
    } else {
      Serial.println("Decive is offline");
      delay(5000);
    }
    
}
