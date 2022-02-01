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
- [Retrofit](https://square.github.io/retrofit/)
- [GSON](https://github.com/google/gson)
