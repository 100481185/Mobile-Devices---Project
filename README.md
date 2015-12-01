# Mobile-Devices---Project 
---About---

This app advertises local events and promotions.  Companies can publish their events and promotions and the app can identify them within the local area.  Users can then make reservations, and pay through their phone if necessary.  The app generates a code that will be later used as a ticket.  Users can also rate and briefly comment their experience through the app, which can be later used to recommend events and prommotions to other users and to their own account.  Moreover, users can specify their preferences with respect to the categories of the events and promotions.  These preferences, the user's location, and the user's previous experiences can be used to sort the events and promotions that are presented to the user.  The app must keep the user's current and previous reservations, which can also be consulted at any time and can be transferred to other users through NFC.  Lastly, the app allows the users to search for events and promotions by name and category.


---Login Information---

Account used to test the functions:

email - test1@email.com

password - testpassword


---App side---

To return to the previous activity, press the back button or Esc. on the emulator.

This project requires the use of geocoding to find the current location.  To set the location in the emulator, type 'telnet localhost 5554' in the Android Studio terminal.  Afterwards, type 'geo fix [longitude] [latitude]' to set the location.

Each time a reservation is made from the search activity, a login is required, even if the user is already logged in.  This is because error code 401 is returned if the loggedIn variable for the search activity is set to that of the main activity.  Also, a search activity is somehow created twice.

When inputting the email for the login activity, make sure that there are no spaces included as the event log activity will not work otherwise.


---Online side---

link: http://localize-seprojects.rhcloud.com/

This website was developed using Ruby on Rails, and the functionality is similar to that of the app.  This website contains the promotion information, and the list of reservations made by the user.


---Unused features---

The one feature that was going to be implemented was the use of NFC to transfer tickets from one user to another.  However, this feature was not implemented as its priority is low compared to other features.


---Incomplete features---

The following features were planned to be implemented in the app, but were not finished due to time constraints:

1. Cancel reservations; the events are removed from the event log in the app, but not from the source file in the server

2. Sort events by distance between event location and user's current location

3. Filter events by user preferences (display only events with categories chosen by the user)


---Constraints---

1. User's reservations are not updated as new reservations are added
