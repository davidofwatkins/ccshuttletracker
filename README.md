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


## Getting Started with Eclipse

Shuttle Tracker runs most easily on [Eclipse](https://www.eclipse.org/) with ADT (Android Development Tools). For more information about installing the Android SDK, please [see here](https://developer.android.com/sdk/installing/index.html). Once the SDK is installed and updated, follow these steps to set up your project:

1. Ensure that [Android Support v4](http://developer.android.com/tools/support-library/features.html#v4), [Android Support v7 AppCompat](http://developer.android.com/tools/support-library/features.html#v7-appcompat), and [Google Play Services](https://developer.android.com/google/play-services/index.html?hl=en) are each installed in the [Android SDK Manager](http://developer.android.com/tools/help/sdk-manager.html).
1. Clone the Shuttle Tracker somewhere outside of your Eclipse Workspace.
1. Import the Shuttle Tracker project as well as the Android dependencies into your Eclipse Workspace:
	1. In Eclipse, click File > Import > Android > Existing Code into Workspace
	1. Select the Shuttle Tracker project directory
	1. Select the AppCompat v7 library folder from the Android SDK: `<android-sdk>\extras\android\compatibility<or support>\v7\appcompat`
	1. Select the Play Services library folder from the Android SDK: `<android-sdk>/extras/google/google_play_services/libproject/google-play-services_lib/` (for more information, [see here](http://developer.android.com/google/play-services/setup.html).)
	1. **Important:** be sure to check "Copy projects into workspace"
	1. Click Finish
1. Add the support v4 library: right-click the project > Android Tools > Add Support Library
1. Right-click the project and choose "Properties." Click "Android" from the left-hand menu, and ensure the proper build target is selected (usually the latest version with Google APIs).
1. In the same menu, ensure that both the Google Play Services and AppCompat v7 libraries are selected under "Library." If not, click "Add" and select them.
1. Create your `apikeys.xml` file and place it in `/res/values/` (see below).
1. If you plan to use Crashlytics, you can install and configure it as an Eclipse plugin from [here](https://www.crashlytics.com/onboard). If not, simply remove the Crashlytics `import` statement and `if (!debugging) { Crashlytics.start(this); }` line from `Map.java`.

### API Keys

CC Shuttle Tracker uses the [Google Maps API v2](https://developers.google.com/maps/documentation/android/) and [Crashlytics](https://www.crashlytics.com/), both of which require an API key. To add your API keys, define them in `res/values/apikeys.xml` as such:

	<?xml version="1.0" encoding="utf-8"?>
	<resources>
	    <string name="googlemaps_key">MY_KEY_HERE</string>
	    <string name="crashlytics_key">MY_KEY_HERE</string>
	    <!-- Set debug to true to turn off Crashlytics: -->
    	<bool name="debug">false</bool>
	</resources>

If `debug` is true, Crashlytics will be deactivated. To protect your keys, `.gitignore` is set to ignore this file.

## Changelog

### v2.2

- Updated Google Maps to v2
- Replaced Action Bar Sherlock with AppCompat v7


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
