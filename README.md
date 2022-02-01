# NPR Tech News
Completed news reader app for AWD 1112 Unit 03.

Fetches the latest NPR Tech News from this API endpoint:
https://feeds.npr.org/1019/feed.json

Source:
https://npr.codes/npr-now-supports-json-feed-1c8af29d0ce7

## NPRTechNews1
Initial version of news reader.
* Layout
* Permissions
* IO classes: URL, HttpURLConnection, InputStream,  BufferedReader, InputStreamReader, Exceptions
* Does not work due to making network requests on main thread.

## NPRTechNews2
Using AsyncTask to offload network request to a background thread.
* AsyncTask
* FetchResult
* FetchTask
* Best practices and common pitfalls.

## NPRTechNews3
Checking connectivity with ConnectivityManager.
* ConnectivityManager
* isConnected()
* API Deprication: getActiveNetworkInfo() and NetworkInfo
* API Deprication: instantaneous state vs network events

## NPRTechNews4
Listening to network events with ConnectivityManager.NetworkCallback.
* ConnectivityManager.NetworkCallback
* ConnectivityManager.registerDefaultNetworkCallback()
* Vector Icons
* Activity.runOnUiThread()

## NPRTechNews5
Using Thread primitives to replace the deprecated AsyncTask class.
* Thread
* Activity.runOnUiThread()
* Task cancellation

## NPRTechNews6
Using Retrofit to make HTTP requests and GSON to parse the response.
* [Retrofit](https://square.github.io/retrofit/)
* [GSON](https://github.com/google/gson)
