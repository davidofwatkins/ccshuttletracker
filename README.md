# Champlain College Shuttle Tracker for Android

**Created and Maintained by:** [David Watkins](http://davidofwatkins.com/) ([@dwat91](https://twitter.com/dwat91), [+David Watkins](https://plus.google.com/104494880066441442910))

###Summary:
The Champlain College Shuttle Tracker displays locations of shuttles and buses at Champlain College that are responsible for transportation between campus, Spinner Place, Quarry Hill, and the Gilbane/Lakeside parking lots. It is an unofficial, mobile, and on-the-go alternative to the desktop web tracker, available at [shuttle.champlain.edu](http://shuttle.champlain.edu).

Available on the Play Store, [here](https://play.google.com/store/apps/details?id=dwat.ccshuttletracker):

[![CC Shuttle Tracker](https://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=dwat.ccshuttletracker)

## Project Dependencies

CC Shuttle Tracker requires the following dependencies:

* [Android Support v4](http://developer.android.com/tools/support-library/features.html#v4)
* [Android Support v7 AppCompat](http://developer.android.com/tools/support-library/features.html#v7-appcompat)
* [Google Play Services](https://developer.android.com/google/play-services/index.html?hl=en)
* [Crashlytics](https://www.crashlytics.com/)

If you are using [Android Studio](http://developer.android.com/sdk/installing/studio.html), include the following in your `build.gradle` file and click _Sync Project with Gradle Files_ in the toolbar:

	dependencies {
		compile 'com.android.support:support-v4:18.0.+'
	    compile 'com.android.support:appcompat-v7:18.0.+'
	    compile 'com.google.android.gms:play-services:4.0.30'
	}

**Note:** these version numbers should be updated when new versions are released.

Please also install and configure the [Crashlytics plugin for your IDE of choice](https://www.crashlytics.com/onboard).

### API Keys

CC Shuttle Tracker relies on the [Google Maps API v2](https://developers.google.com/maps/documentation/android/) and [Crashlytics](https://www.crashlytics.com/), both of which require an API key. To add your API keys, define them in `res/values/apikeys.xml` as such:

	<?xml version="1.0" encoding="utf-8"?>
	<resources>
	    <string name="googlemaps_key">MY_KEY_HERE</string>
	    <string name="crashlytics_key">MY_KEY_HERE</string>
	    <!-- Set debug to true to turn off Crashlytics: -->
    	<bool name="debug">false</bool>
	</resources>

If `debug` is true, Crashlytics will be deactivated. To protect your keys, `.gitignore` is set to ignore this file.

## Changelog

### v2.1.1

- Updated shuttle schedules
- Updated ActionBarSherlock to v4.4
- Updated Android Support Library
- Fixed occasional crash on refresh or startup
- Fixed broken link for "View Online" button on Schedules page
- Other minor enhancements

### v2.1

- Added automatic "scroll to current time" on shuttle schedule
- Updated ActionBarSherlock to v4.0
- Updated v4 support package to revision 9
- Updated schedule data
- Added "Center to Burlington" button

### v2.0:

- Swipable Schedule tabs
- Added ability to change map mode (setSatellite(), etc.)
- Added tap-to-zoom
- Added "refresh" button
- Added "satelllite view" button
- Updated schedule data
- Added new high-resolution icons
- Added Balloon pop-ups when tapping on busses and stops (https://groups.google.com/group/android-developers/browse_thread/thread/3a33e5a2cd3d6b0d)

### v1.5:

- Icon sizes now adjust depending on map zoom level
- Added bus stop icons
- Moved shuttle schedule information to a drawer at the bottom of the map
- Restylized schedule data
- Fixed problems with multiple "No Connection" and "No Shuttle" messages
- Increased shuttle location update frequency
- Removed 3rd party proxy for "View Online" link to shuttle data on champlain.edu
- Added "About" page for more information about CC Shuttle Tracker
