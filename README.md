# NPR Tech News
Completed news reader app for AWD 1112 Unit 03.

Fetches the latest NPR Tech News from this API endpoint:
<br>https://feeds.npr.org/1019/feed.json

Source: [NPR now supports JSON Feed!](https://npr.codes/npr-now-supports-json-feed-1c8af29d0ce7)

## NPRTechNews1
Initial version of news reader.
- [Layout](https://developer.android.com/training/constraint-layout)
- [Permissions](https://developer.android.com/training/basics/network-ops/connecting)
- [IO classes: URL, HttpURLConnection, InputStream,  BufferedReader, InputStreamReader, Exceptions](https://docs.oracle.com/javase/tutorial/essential/io/streams.html)
- [try-with-resources statement](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
- *Does not work due to making network requests on main thread.*

## NPRTechNews2
Using AsyncTask to offload network request to a background thread.
- [AsyncTask](https://developer.android.com/reference/android/os/AsyncTask)
- FetchResult
- FetchTask
- [Processes and threads overview](https://developer.android.com/guide/components/processes-and-threads)
- [Better performance through threading](https://developer.android.com/topic/performance/threads)
- [Exceed the Android Speed Limit](https://medium.com/androiddevelopers/exceed-the-android-speed-limit-b73a0692abc1)

## NPRTechNews3
Checking connectivity with ConnectivityManager.
- [Reading network state](https://developer.android.com/training/basics/network-ops/reading-network-state)
- [ConnectivityManager](https://developer.android.com/reference/android/net/ConnectivityManager)
- isConnected()
- API Deprication: getActiveNetworkInfo() and NetworkInfo
- API Deprication: instantaneous state vs network events

## NPRTechNews4
Listening to network events with ConnectivityManager.NetworkCallback.
- [Reading network state](https://developer.android.com/training/basics/network-ops/reading-network-state)
- [ConnectivityManager.NetworkCallback](https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback)
- [ConnectivityManager.registerDefaultNetworkCallback()](https://developer.android.com/reference/android/net/ConnectivityManager#registerDefaultNetworkCallback(android.net.ConnectivityManager.NetworkCallback))
- [Vector Icons](https://developer.android.com/guide/topics/graphics/vector-drawable-resources)
- [Activity.runOnUiThread()](https://developer.android.com/reference/android/app/Activity#runOnUiThread(java.lang.Runnable))

## NPRTechNews5
Using Thread primitives to replace the deprecated AsyncTask class.
- [Thread](https://developer.android.com/reference/java/lang/Thread)
- [Activity.runOnUiThread()](https://developer.android.com/reference/android/app/Activity#runOnUiThread(java.lang.Runnable))
- Task cancellation

## NPRTechNews6
Using Retrofit to make HTTP requests and GSON to parse the response.
- [Retrofit - Official Tutorial](https://square.github.io/retrofit/)
- [Retrofit - GitHub Repository](https://github.com/square/retrofit)
- [GSON - User Guide](https://github.com/google/gson/blob/master/UserGuide.md)
- [GSON - GitHub Repository](https://github.com/google/gson)
- [JSON Language Specification](https://www.json.org)

## NPRTechNews7
Migrating project to follow modern arhcitecure patterns using ViewModel and LiveData.
- [Guide to app architecture](https://developer.android.com/jetpack/guide)
- [ViewModel Overview](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [LiveData Overview](https://developer.android.com/topic/libraries/architecture/livedata)
- [Save UI States](https://developer.android.com/topic/libraries/architecture/saving-states)

## NPRTechNews8
Displaying the list of news stories using a RecyclerView.
- [Create dynamic lists with RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [ArrayList class](https://developer.android.com/reference/java/util/ArrayList)
- [Generics in Java](https://docs.oracle.com/javase/tutorial/java/generics/index.html)

## NPRTechNews9
Displaying the header image for the news stories using Picasso.
- [Picasso - Official Tutorial](https://square.github.io/picasso/)
- [Picasso - GitHub Repository](https://github.com/square/picasso)
- [Picasso - Slides](https://docs.google.com/presentation/d/1h26EoArWQ2lQ8jQjwpvvGbqW2bvwtVhj/edit?usp=sharing&ouid=114456564937978629252&rtpof=true&sd=true)
- [The Complete Guide to Dimensions in Android](https://www.bradcypert.com/the-complete-guide-to-dimensions-in-android/)
