OPEN IR SECURITY - README.MD FILE - V1

---------------------------------
How to run the app:


1. Create a user account.

When the app is first opened, it will prompt you to either log in with an existing account, or create a new account.
After creating a new user account for the first time, the app will send you an email containing your unique user ID. 
This ID will be required during the set-up of all new IR devices attached to your account. 

2. Attach a new device to your account.

Plug the OpenIRSecurity hardware into your computer using the included microUSB. Run the installer script, making sure the 
setup script is in the same directory as the ESP code. 

This script will install the OpenIRSecurity program on your ESP. Setup will require the user ID emailed to you during
the signup process, as well as your device name, local WiFi username and password. Once the script has executed successfully, 
open your application and go to the “Add Sensor” page. Name your new device the same name that you gave in the ESP script.
Triggers should appear automatically on your home screen. 


FAQ:

1. My device isn't showing up in the app. 

Make sure you have named your device in the app the same thing as is written in the ESP file. Also check to make sure
your wifi name and password are accurate, and the userID string matches. 



