# Champlain College Shuttle Tracker <sup>for Android</sup>

**Created and Maintained by:** [David Watkins](http://davidofwatkins.com/) ([@dwat91](https://twitter.com/dwat91), [+David Watkins](https://plus.google.com/104494880066441442910))


![Champlain Tracker](assets/ccbus_large.png)

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

Champlain Tracker runs most easily on [Eclipse](https://www.eclipse.org/) with ADT (Android Development Tools). For more information about installing the Android SDK, please [see here](https://developer.android.com/sdk/installing/index.html). Once the SDK and ADT are installed and updated, follow these steps to set up your project:

1. Ensure that [Android Support v4](http://developer.android.com/tools/support-library/features.html#v4), [Android Support v7 AppCompat](http://developer.android.com/tools/support-library/features.html#v7-appcompat), and [Google Play Services](https://developer.android.com/google/play-services/index.html?hl=en) are each installed in the [Android SDK Manager](http://developer.android.com/tools/help/sdk-manager.html).
1. Clone the Champlain Tracker repo somewhere outside of your Eclipse Workspace.
1. Import the Champlain Tracker project as well as the Android dependencies into your Eclipse Workspace:
	1. In Eclipse, click File > Import > Android > Existing Code into Workspace
	1. Select the Champlain Tracker project directory
	1. Select the AppCompat v7 library folder from the Android SDK: `<android-sdk>\extras\android\compatibility<or support>\v7\appcompat`
	1. Select the Play Services library folder from the Android SDK: `<android-sdk>/extras/google/google_play_services/libproject/google-play-services_lib/` (for more information, [see here](http://developer.android.com/google/play-services/setup.html).)
	1. **Important:** be sure to check "Copy projects into workspace"
	1. Click Finish
1. Add the support v4 library: right-click the project > Android Tools > Add Support Library
1. Right-click the project and choose "Properties." Click "Android" from the left-hand menu, and ensure the proper build target is selected (usually the latest version with Google APIs).
1. In the same menu, ensure that both the Google Play Services and AppCompat v7 libraries are selected under "Library." If not, click "Add" and select them.
1. Create your `apikeys.xml` file and place it in `/res/values/` (see below).
1. If you plan to use Crashlytics, you can install and configure it as an Eclipse plugin from [here](https://www.crashlytics.com/onboard). If not, simply remove the Crashlytics `import` statement and the `if (!debugging) { Crashlytics.start(this); }` line from `Map.java`.

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

## To-Do List

If you're a part of the Champlain community and would like to contribute to the app, please feel free to fork and pull request. If you'd like a few ideas, here are some features that could improve the app:

- **Smarter Icons**: [shuttle.champlain.edu/shuttledata](http://shuttle.champlain.edu/shuttledata) already provides the direction buses are facing, so it might be nice to visually reflect that on the app. This might involve replacing the existing icons with some sort of "bird's-eye view" icons.
- **Homescreen Widget**: the Champlain Tracker has a pretty straightforward purpose, and users shouldn't have to open the app just for a quick glance at shuttles' locations. A simple map (and/or schedule) widget for the Android homescreen would provide a more direct experience for frequent shuttlers.
- **Tablet Design**: right now, the app was designed for phones with little consideration for tablets. While it runs fine on tablets as-is, the design should be reconsidered to scale well across multiple device sizes.
- **Cloud-Powered Schedules**: currently, the in-app schedules are static HTML pages. To reduce maintenance, it would be nice to have an API that the app could use to download new schedule information from a server. (This may require more coordination with the college, but they might benefit from a more streamlined way to publish schedule data [to their site](http://www.champlain.edu/current-students/campus-services/transportation-and-parking/transportation-services-and-shuttle/shuttle), too.)
- **Time Estimations**: at the end of the day, shuttle passengers don't care as much about where shuttles are than they do about when they will arrive. Champlain Tracker needs a page with a list of shuttles and their aproximate times from each stop.
- **Notifications**: perhaps in combination with the above feature, users could request notifications for when specific buses enter proximity of specific stops. This could be calculated by time, or the app could continually check on bus locations when a notification is requested.

If you plan to contribute, it may be best to discuss your plans in the "issues" tab so there's a record of what features are being worked on.

## Changelog

### v2.2

- Added Crashlytics crash reporting
- Updated the Google Maps API to v2
- Replaced Action Bar Sherlock with AppCompat v7
- Renamed app to "Champlain Tracker"
- Other minor performance improvements and code refactoring
- Removed unnecessary libraries

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
