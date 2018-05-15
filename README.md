# Baking App

This project is an exercise as part of the **Android Developer Nanodegree**, by **Udacity**. The app allows a user to select a recipe and see video-guided steps for how to complete it.

The recipes are in a `JSON` format, that contains the recipe's instructions, ingredients, videos and images, stored on a remote server.

The data is queried through internet, with the **Retrofit** library, and saved in the local **SQLite** database. We have implemented the `Content Provider` to handle the database.

After storing the data, the app will query the database, and will work mainly with this data, in the form of strings and `JSON` strings stored in memory and passed though the activities. This makes the database a little redundant, but the database is important for future developments of the app.

We have used `Fragments` to adapt the screens to the devices, so the same `Fragments` can be reused in different layouts. The communication between `Activities` and `Fragments` was made with the help of `Listeners`. The state of the `Fragments` is saved and reloaded to handle device screen rotation.

The movies and thumbnails are fetched with the **ExoPlayer** and **Picasso** libraries. We have set a `Fragment` exclusively for one **ExoPlayer** `container`.  The app is able to handle some errors, like when there is no internet response or when the data is incomplete, showing some error messages.

Finally, the app has a `widget` that shows the recipe ingredients for the recipe that was selected in the app. If we close the app, when reading the recipe details screen, the widget will show the ingredients for that recipe. The widget will be cleared if we close the app while not in the recipe detail screen. 🍰


We have used the following libraries and technologies:

**ExoPlayer** (https://github.com/google/ExoPlayer)

**Picasso** (http://square.github.io/picasso/)

**SQLite**  (https://www.sqlite.org/index.html)

**Retrofit** (http://square.github.io/retrofit/)

**ButterKnife** (http://jakewharton.github.io/butterknife/)



 _This app is for learning purposes._ 📚


# Credits

These are some useful links in addition to **Udacity**, the https://developer.android.com/guide, and https://developer.android.com/training, that were queried in this project:

https://developer.android.com/guide/components/fragments

https://codelabs.developers.google.com/codelabs/exoplayer-intro/#0

https://github.com/google/ExoPlayer

https://developer.android.com/guide/topics/ui/layout/cardview

https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java

https://stackoverflow.com/questions/21225213/set-the-width-of-relativelayout-1-3-the-width-of-the-screen

https://stackoverflow.com/questions/40209550/reading-a-json-array-in-java

https://stackoverflow.com/questions/49153215/failed-to-find-style-cardview-style-in-current-theme

https://stackoverflow.com/questions/33237235/remove-all-fragments-from-container

http://www.vogella.com/tutorials/Retrofit/article.html

https://github.com/codepath/android_guides/wiki/Consuming-APIs-with-Retrofit

http://www.jsonschema2pojo.org/

https://stackoverflow.com/questions/3053761/reload-activity-in-android

https://stackoverflow.com/questions/8371274/how-to-parse-json-array-with-gson

_Testing:_

https://developer.android.com/training/testing/espresso/

https://codelabs.developers.google.com/codelabs/android-testing

https://developer.android.com/reference/android/support/test/espresso/contrib/RecyclerViewActions

https://spin.atomicobject.com/2016/04/15/espresso-testing-recyclerviews/

https://medium.com/google-developers/adapterviews-and-espresso-f4172aa853cf

https://academy.realm.io/posts/chiu-ki-chan-advanced-android-espresso-testing/

https://github.com/chiuki/espresso-samples

_Widgets:_

https://www.youtube.com/watch?v=eKANzCs2pWM&feature=youtu.be

https://www.youtube.com/watch?v=QAbQgLGKd3Y&list=PL6gx4Cwl9DGBsvRxJJOzG4r4k_zLKrnxl

https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
