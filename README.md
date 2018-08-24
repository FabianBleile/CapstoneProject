# CapstoneProject

NFC Share Contacts
Description 

	Easy way to exchange your number and(!) social media accounts with people you just met 	via NFC. Handle all your friends data fast and easy. Decide what information you want to 	send and which you'd better keep this time. This App is for everybody who wants exchange 	more than numbers or maybe less super fast.  Store these special contacts apart from your 	real contact list or add them – that's up to you Access the social media directly from the app.


Intended User

	Travellers, social active people, nfc-technology-lovers
  
Features

	Saves information 
	Sends and receives data per NFC

User Interface Mocks



Key Considerations

How will your app handle data persistence? 

	SharedPreferences for Settings and „personal information“
	Room for received data

Describe any edge or corner cases in the UX.

Navigate

	from widget to MainActivity
	from MainActivity (ReiceiveFragment) to DetailActivity and back

Libraries

	Room for data persistance
	Gson for object to string to send it via nfc
	Firebase and Google Services for AdMob and Location

Described how Google Play Services or other external services are implemented.

	Location, to get the last known location. Sharing this makes it easier to handle contacts
	adMob in form of an intertitial ad launching on data transfer complete

Next Steps: Required Tasks

Task 1: Project Setup

	Configure Libraries

Task 2: Implement UI for Each Activity and Fragment

	Build UI for MainActivity
	Build UI for Fragments (SettingsScreen, SendScreen, ReceiveScreen, ChatScreen(?))
	Build UI for BottomNavigationMenu
	Build UI for DetailActivity

Task 3: Implement SharedPreferences for SettingsScreen

	make SettingsScreen responsive
	handle Sharedpreferences for peersonal contact


Task 4: Make nfc data transfer working

	create object to send
	handle sending and receiving device


Task 5: Widget

	create configure activity


Task 6: DetailActivity

	list received contacts and equip the activity with implicit intents to quickly use the received data



Library versions

	Android Studio 03.01.03
	Gradle 03.01.03
	Google services 4.0.1
	Gson 02.08.05
	Play services location 15.0.1
	Firebase ads 15.0.1
	room 01.01.01
	Lifecycle  01.01.01

App keeps all strings in strings.xml and enables RTL layout switching in the manifest
Async task is used to run tasks asyncronously
Room is used coming with view model and live data
