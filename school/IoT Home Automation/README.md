IoT Home Automation

Built a system for a model house using Node-Red, Arduino, Raspberry Pi, and 7 different sensors. Further Description forthcoming


**Sensor Breakdown**

a.) Light Control - Pressing the touch sensor will turn on the Blue LED on the ESP’s breadboard. Additionally, pressing the toggle on Node Red will turn on/off the Yellow LED, which represents the door being locked

b.) Temperature Control - The DHT sensor connected to the ESP reads temperature from Node Red and displays it on a graph

c.) Humidity Sensor - this comes from the DHT sensor and is also displayed on Node Red

d.) Motion Sensor - the motion sensor connected to the Arduino will make the Red LED flash twice and then a countdown timer is activated and the buzzer rings.

e.) Door Lock - The green LED connected to the ESP and to Node Red signifies if the door is locked. On being locked and off being unlocked.

f.) Open and close curtains - The moving the joystick left and right will turn the motor clockwise or counter clockwise so that the curtains are moving back and forth. This is on the Arduino

g.) Touch Sensor - The touch sensor is connected to the ESP and it will turn on the blue LED if it detects your finger

h.) Sound sensor - This sensor is connected to the ESP and the small LED on the device will light up green if sound is detected like a snap. Getting any readings from the device in a program proved to be futile.

i.) 7 segment - a countdown from 3 is shown when a motion event at the front door is detected. The 7-segment is connected to the arduino

j.) Joystick - The joystick is connected to the Arduino and will move the motor if you move it left and right.

k.) Miscellaneous interesting feature - Photoresistor - Shining a light at the photoresistor or bringing it outside in daylight will trigger the RED led to be lit up.


**House Diagram**


![alt text](https://raw.githubusercontent.com/prestondcarroll/projects/master/school/IoT%20Home%20Automation/House_Diagram.png)



**Online Remote Dashboard**

![alt text](https://raw.githubusercontent.com/prestondcarroll/projects/master/school/IoT%20Home%20Automation/dashboard.png)



**Overview of House**

![alt text](https://raw.githubusercontent.com/prestondcarroll/projects/master/school/IoT%20Home%20Automation/House_Overview.jpg)
