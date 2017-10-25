# My Calendar
Simple event calendar, with agenda view.

## Requirements
* Android SDK 23
* Android SDK Tools 25.0.8
* Android SDK Build-tools 23.0.2
* Android Support Library 23.2.1

## Build & Test

**Build**

    ./gradlew :app:assembleDebug

**Test & Coverage**

    ./gradlew :app:lintDebug
    ./gradlew :app:testDebug
    ./gradlew :app:jacocoTestCoverage

## Discussions:
Challenges:

Number of items in list view (agenda view) and grid view (calendar view) is essentially unlimited, as time has no limit on past or future
* A naive solution of continuously adding dates on the fly would be inefficient, given the limited resources in mobile devices
* We should come up with some sort of recycling and limit 'active' dates to a fixed number

Choice of widget:

* `android.widget.CalendarView`:
  * included in SDK
  * no event indicator
  * no month change listener
  * no state restoration
  * inconsistent look and feel across API levels
* custom `EventCalendarView`:
  * use a `RecyclerView` with `GridLayoutManager` (or `GridView`) to display a grid of days in month
  * use a `ViewPager` to allow swiping between months, make it 'circular' to minimize pages required
  * override `ViewPager.onMeasure()` to allow its height to wrap content

Widgets arrangement consideration:

To support different device sizes and orientations, the following arrangements are made:

* Portrait: more vertical space
  * Stack calendar view and agenda view
* Landscape: limited vertical space
  * For smaller devices, moderate horizontal space: overlap calendar view and agenda view. In this case the calendar view becomes more of a 'pop-up' picker
  * For larger devices e.g. tablets, more horizontal space: put calendar view and agenda view side-by-side