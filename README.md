# College Near Me

#### By Jeffrey Ruder

## Description

The [College Scorecard](https://collegescorecard.ed.gov/) is an effective website and a step forward in providing transparent information about colleges.

College Near Me aims to highlight the most accurate and relevant parts of the College Scorecard Data set while adding geolocation and other native app functionality. Proximity plays a greater role in the college sections process for prospective college students than the media generally acknowledges. This means highlighting nearby colleges would be worthwhile for uses.

## Features

* Login with Google or Facebook
* View colleges near your location, or search for colleges near any zip code
* Save colleges to review later
* View information about the colleges and see their location in a Google MapView
* Share individual colleges or your saved list
* Intents to visit college price calculators and websites

## Technologies

Android, Java, XML.

## Setup

Ensure you have Android Studio installed.

Clone this repository into your AndroidStudioProjects directory.

You will need to obtain API keys for [Data.gov](https://api.data.gov/) and [Google Maps](https://developers.google.com/maps/documentation/android-api/). You will also need a [Facebook App ID](https://developers.facebook.com/apps).

Then add the Facebook App ID as a string resource in `strings.xml` and add the API keys to your `gradle.properties` file:

```
DataGovKey = "Your Key Here"
GoogleMapsKey = "Your Key Here"
```

Use AVD Manager to run the project on a virtual device, or connect an Android phone with USB debugging enabled to run on a real device.

## License

This software is licensed under the MIT license.

Copyright (c) 2016 Jeffrey Ruder

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
