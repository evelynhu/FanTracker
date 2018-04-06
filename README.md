Fan Tracker
=======

Sample android application project to demo Facebook Audience Network and detecting user observing behavior.

## App

The app is a simulation of an fan page management application with two main screens: a page of posts and a draft editor.

![App screenshot](/doc/app.png)

### Use cases

1. Publish a new post would add the element to the fan page.
2. A small notification will be shown every time that someone is viewing a post or an Ad.
3. When someone is viewing a post, the number of viewers of the post must be updated accordingly.
4. "About" screen can be opened by menu item click.
5. Native Ad will be loaded when there are more than 4 posts.
6. Other ad type can be loaded from clicking menu item.

![Post new status](/doc/post_new_status.mp4)

![Loading effect](/doc/loading_effect.mp4)

![Backend realtime data](/doc/backend_realtime_data.mp4)

![Audience network demo](/doc/audience_network_demo.mp4.mp4)

## Concepts

### Detecting List Items Observed by User
App tracking solution for the following task: identify which item of the RecyclerView list was viewed and perceived by the user. Perceived in this context means that user held the item in the viewport for at least 2 seconds. The image below illustrates this with an example.

**Full article**: [Detecting List Items Observed by User](https://proandroiddev.com/detecting-list-items-perceived-by-user-8f164dfb1d05)

![Item was pervered by the user](/doc/item_was_perveived_by_the user_small.png)

## Project Structure

![Project Structure](/doc/project_structure.png)


## Additional setup

**Audience Network demo**: If you are using the default Google Android emulator, you'll add the hash ID of testing device. The hashed ID is printed to logcat when you first make a request to load an ad on a device. Genymotion and physical devices do not need this step. If you would like to test with real ads, please consult our Testing Guide. [More information.](https://developers.facebook.com/docs/audience-network)

**Firebase demo**:

* Add Firebase to you Android Project: [follow instructions](https://firebase.google.com/docs/android/setup) to get your google-services.json configuration file and add it to the project.
* You need to connect firebase in Android Studio: [follow instructions](https://developer.android.com/studio/write/firebase.html)



## Version
1.0
