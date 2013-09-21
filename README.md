# Champlain College Shuttle Tracker for Android

**Created and Maintained by:** [David Watkins](http://davidofwatkins.com/) ([@dwat91](https://twitter.com/dwat91), [+David Watkins](https://plus.google.com/104494880066441442910))

###Summary:
The Champlain College Shuttle Tracker displays locations of shuttles and buses at Champlain College that are responsible for transportation between campus, Spinner Place, Quarry Hill, and the Gilbane/Lakeside parking lots. It is an unofficial, mobile, and on-the-go alternative to the desktop web tracker, available at [shuttle.champlain.edu](http://shuttle.champlain.edu).

Available on the Play Store: <https://play.google.com/store/apps/details?id=dwat.ccshuttletracker>

## Project Setup in Eclipse

After importing the project into Eclipse, it is necessary to link project dependencies (ActionBarSherlock and android-mapviewballoons). To do this, you must import them as projects into the Eclipse workspace, as follows:

1. Right-click in Eclipse's Package Explorer and choose New > Other > Android > Android Project from Existing Code and click Next.
1. Next to Root Directory, choose "Browse" and navigate to <Project-Folder>/libs/ActionBarSherlock/ or <Project-Folder>/libs/android-mapviewballoons/ and click ok. This will create a project in Eclipse for the given dependency.
1. Right click the CC Shuttle Tracker project and choose properties > Android.
1. Click "Add" under the Library section.
1. Choose the freshly-created dependency project from the list.
1. Click ok.

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
