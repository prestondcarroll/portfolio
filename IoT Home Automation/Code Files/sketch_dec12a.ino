#include <Servo.h>


int A = 13;
int B = 12;
int C = 11;
int D = 10;
int E = 9;
int F = 8;
int G = 7;

int buzzer = 6;
int motion_sensor = 5;
int joystick = A0; //VRx
int photoresistor = A1;
int motor = 4;

int joy_click = 3;
int RED_led = 2;

int LRMID = 0;
int MID = 10;
int LR = 0;
int state = LOW;

int var1 = 0; // for motion sensor reads
int var2 = 0; //for joystick click reads

int pos = 0; // for servo

int photo_sensor_val = 0;


Servo servo; //servo object


//move the servo 90 degrees
void move90Degrees(){
  // moves servo from 0 degrees to 90 degrees
  for (; pos <= 90; pos += 1) {
    // in steps of 1 degree
    servo.write(pos); // tell servo to go to position in variable 'pos'
    delay(15); // waits 15ms for the servo to reach the position
  }
}


//move the servo 90 degrees
void moveBack(){
  // moves servo from 0 degrees to 90 degrees
  for (; pos >= 0; pos -= 1) {
    // in steps of 1 degree
    servo.write(pos); // tell servo to go to position in variable 'pos'
    delay(15); // waits 15ms for the servo to reach the position
  }
}




void setup() {
  // put your setup code here, to run once:

  Serial.begin(115200);
  pinMode(A, OUTPUT);
  pinMode(B, OUTPUT);
  pinMode(C, OUTPUT);
  pinMode(D, OUTPUT);
  pinMode(E, OUTPUT);
  pinMode(F, OUTPUT);
  pinMode(G, OUTPUT);
  pinMode(RED_led, OUTPUT);

  pinMode(buzzer, OUTPUT);
  pinMode(motor, OUTPUT);

  pinMode(motion_sensor, INPUT);
  pinMode(joystick, INPUT);
  pinMode(photoresistor, INPUT);
  pinMode(joy_click, INPUT);

  

  LRMID = analogRead(joystick);

  servo.attach(motor);
  servo.write(pos);
  delay(1000);
}

void loop() {
  // put your main code here, to run repeatedly:

  //motion sensor code
  var1 = digitalRead(motion_sensor);
  if(var1 == HIGH){ //activation code
    Serial.println("Motion Detected");
    digitalWrite(RED_led, HIGH);
    delay(100);
    digitalWrite(RED_led, LOW);
    delay(100);
    digitalWrite(RED_led, HIGH);
    delay(100);
    digitalWrite(RED_led, LOW);
    delay(2000);
    if(state == LOW){
      state = HIGH;
    }
  } 
  else{ //non-activation code
    if(state == HIGH){
      state = LOW;
    }
  noTone(buzzer);
  }



  
  //7-segment and buzzer code
  var2 = digitalRead(motion_sensor);
  Serial.print("var2 is");
  Serial.println(var2);
  delay(10);
  if(var2 == HIGH){
    Serial.println("Clicked");
    //3
    digitalWrite(A, HIGH);
    digitalWrite(B, HIGH);
    digitalWrite(C, HIGH);
    digitalWrite(D, HIGH);
    digitalWrite(G, HIGH);
    delay (1000);
    digitalWrite(C, LOW);

    //2
    digitalWrite(A, HIGH);
    digitalWrite(B, HIGH);
    digitalWrite(G, HIGH);
    digitalWrite(E, HIGH);
    digitalWrite(D, HIGH);
    delay(1000);
    digitalWrite(A, LOW);
    digitalWrite(G, LOW);
    digitalWrite(E, LOW);
    digitalWrite(D, LOW);

    //1
    digitalWrite(B, HIGH);
    digitalWrite(C, HIGH);
    delay(1000);

    //0
    digitalWrite(A, HIGH);
    digitalWrite(B, HIGH);
    digitalWrite(C, HIGH);
    digitalWrite(D, HIGH);
    digitalWrite(E, HIGH);
    digitalWrite(F, HIGH);
    delay(1000);
    digitalWrite(A, LOW);
    digitalWrite(B, LOW);
    digitalWrite(C, LOW);
    digitalWrite(D, LOW);
    digitalWrite(E, LOW);
    digitalWrite(F, LOW);

    tone(buzzer, 500);
    delay(500);
    noTone(buzzer);  

    var2= LOW;
  }


  //joy stick to motor code
  LR = analogRead(joystick);
  if (LR > LRMID + MID) { //right
    Serial.print("pos is ");
    Serial.println(pos);
    if(pos < 360){
      Serial.println("Right!");
      pos++;
      servo.write(pos); // tell servo to go to position in variable 'pos'
      delay(15); // waits 15ms for the servo to reach the position 
    }
  }

  if (LR < LRMID - MID) { //left
      Serial.print("pos is ");
      Serial.println(pos);
      if(pos > 0){
        Serial.println("Left!");
        pos--;
        servo.write(pos); // tell servo to go to position in variable 'pos'
        delay(15); // waits 15ms for the servo to reach the position 
    }
  }


  //photo resistor code
  photo_sensor_val = analogRead(photoresistor);
  if(photo_sensor_val < 130){
    digitalWrite(RED_led, HIGH);
  }
  else {
    digitalWrite(RED_led, LOW);
  }

  //delay(30);
 


  

}
