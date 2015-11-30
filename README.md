# Mobile-Devices---Project 
Account used to test the functions:
email - test1@email.com
password - testpassword

To return to the previous activity, press the back button on the emulator.  Alternatively, press Esc.

This project requires the use of geocoding to find the current location.  To set the location in the emulator, type 'telnet localhost 5554' in the Android Studio terminal.  Afterwards, type 'geo fix [longitude] [latitude]' to set the location.

Each time a reservation is made from the search activity, a login is required, even if the user is already logged in.  This is because error code 401 is returned if the loggedIn variable for the search activity is set to that of the main activity.  Also, a search activity is somehow created twice.