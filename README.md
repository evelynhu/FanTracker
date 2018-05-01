Fan Tracker
=======

Sample android application project to demo Facebook Audience Network and detecting user observing behavior.

## App

The app is a simulation of an fan page management application with two main screens: a page of posts and a draft editor.

![App screenshot](/doc/app.png)

![App Overview](/doc/fan_tracker.mp4)

### Use cases

1. Publish a new post would add the element to the fan page.
2. A small notification will be shown every time that someone is viewing a post or an Ad.
3. When someone is viewing a post, the number of viewers of the post must be updated accordingly.
4. Native Ad will be loaded when there are more than 4 posts.
5. Other ad type can be loaded from clicking menu item.
6. Draft data remain in the fragment after screen rotation

![Post new status](/doc/post_new_status.mp4)

![Loading effect](/doc/loading_effect.mp4)

![Backend realtime data](/doc/backend_realtime_data.mp4)

![Audience network demo](/doc/audience_network_demo.mp4.mp4)

## Concepts

### Detecting List Items Observed by User
*[Reference](https://proandroiddev.com/detecting-list-items-perceived-by-user-8f164dfb1d05)

App tracking solution for the following task: identify which item of the RecyclerView list was viewed and perceived by the user. Perceived in this context means that user held the item in the viewport for at least 250 milliseconds. The image below illustrates this with an example.

![Item was pervered by the user](/doc/item_was_perveived_by_the user.png)

The requirements we need to meet to implement this logic:

1. **Distinct** : skip the event when the visible item set is equal to the one been just processed. The use case is multiple callbacks from the swipe gesture

2. **Timeout** : fire the event only after specific timeout, 250ms in our case

3. **Skip the previous event if a distinct event arrived before the timeout** : the previous tracking event should be skipped if the user didn’t hold the item for the defined timeout and scrolled to another list item

4. **Reset** : reset the state of the logic defined above if the current Activity is stopped. We need this to track view event again when the user comes back.

***implementation >>***

1. **RecyclerView OnScrollListener and LinearLayoutManager to detect visible items**

  To achieve the basic list item visibility detection, we can go with these two methods to be called on every scroll:
```java
int findFirstCompletelyVisibleItemPosition()
int findLastCompletelyVisibleItemPosition()
```
To detect scroll events in the RecyclerView, we need to add a scroll listener, RecyclerView.OnScrollListener, which provides us with onScroll() callback. The annoying thing about this callback is that it is called multiple times during one swipe action done by a user.

2. **Scroll callbacks and RxJava Subscribers to solve this problem in more elegant way**

  we can implement event bus functionality using PublishSubject and post a new event to observe every time the list item appears in the viewport. To achieve the timeout effect and to not track the same item several times, we can use filtering operators available in Rx.

  **distinctUntilChanged** to skip equal events in case of multiple scroll callbacks;
  **throttleWithTimeout** to pass an event with a delay and drop current event if another event arrives before the timeout.

```java
public class ThrottleTrackingBus {

    private static final int THRESHOLD_MS = 250;

    private Subject<VisibleState, VisibleState> publishSubject;
    private Subscription subscription;
    private final Action1<VisibleState> onSuccess;

    public ThrottleTrackingBus(final Action1<VisibleState> onSuccess,
                               final Action1<Throwable> onError) {
        this.onSuccess = onSuccess;
        this.publishSubject = PublishSubject.create();
        this.subscription = publishSubject
                .distinctUntilChanged()
                .throttleWithTimeout(THRESHOLD_MS, TimeUnit.MILLISECONDS)
                .subscribe(this::onCallback, onError);
    }

    public void postViewEvent(final VisibleState visibleState) {
        publishSubject.onNext(visibleState);
    }

    public void unsubscribe() {
        subscription.unsubscribe();
    }

    private void onCallback(VisibleState visibleState) {
        onSuccess.call(visibleState);
    }

    public static class VisibleState {
        final int firstCompletelyVisible;
        final int lastCompletelyVisible;

        public VisibleState(int firstCompletelyVisible,
                            int lastCompletelyVisible) {
            this.firstCompletelyVisible = firstCompletelyVisible;
            this.lastCompletelyVisible = lastCompletelyVisible;
        }
    }
}
```

## Project Structure

![Project Structure](/doc/project_structure.png)


**Using Firebase as realtime database and image storage.**
* **Backend as a service**
* **Focus on app development**
* **Easy to set up**
* **Real-time data sync** :
Firebase is designed with a model-observer scheme which is most useful for interactive apps. Any change in data done on the server is updated to the registered clients in real-time. The data syncs almost instantly on the client devices.
* **Cloud messaging and remote customization of apps enables to update the app variables instantly**.

**Using Simple Factory Design Pattern for organizing all ads**

![App screenshot](/doc/ad_types.png)

* **Loose coupling** : using a factory you could  easiliy switch from one implementation to another
* **Encapsulation** : Sometimes, using a factory improves the readibility of your code and reduces its complexity by encapsulation.

**Using Fresco to display images**

* **Image pipeline** :
Fresco’s image pipeline will load images from the network, local storage, or local resources. To save data and CPU, it has three levels of cache; two in memory and another in internal storage.

![App screenshot](/doc/imagepipeline.png)

* **Decreasing OOM rate** :
using native heap instead of java heap. A decompressed image - an Android Bitmap - takes up a lot of memory. This leads to more frequent runs of the Java garbage collector. This slows apps down. The problem is especially bad without the improvements to the garbage collector made in Android 5.0.

* **It's good for app which need to load lots of images**

* **Drawees** :
Fresco’s Drawee shows a placeholder for you until the image has loaded and then automatically shows the image when it arrives. When the image goes off-screen, it automatically releases its memory.

* **Image Pipeline** :
Fresco’s image pipeline will load images from the network, local storage, or local resources. To save data and CPU, it has three levels of cache; two in memory and another in internal storage.

**Using two fragments for controlling post list page and draft editing page.**

* easy to reuse : for example, in multi-pane user-interface or in different activity.
* unique lifecycle

**Using view pager for switching the fragments**

## Possible Alternative Design for App based in China

* using Parse Server and Amazon S3 storage services



## Additional setup

**Audience Network demo**: If you are using the default Google Android emulator, you'll add the hash ID of testing device. The hashed ID is printed to logcat when you first make a request to load an ad on a device. Genymotion and physical devices do not need this step. If you would like to test with real ads, please consult our Testing Guide. [More information.](https://developers.facebook.com/docs/audience-network)

* Change the [TEST_DEVICE_HASH](/app/src/main/java/com/mii/android/util/Constant.java)

**Firebase demo**:

* Add Firebase to you Android Project: [follow instructions](https://firebase.google.com/docs/android/setup) to get your google-services.json configuration file and add it to the project.
* You need to connect firebase in Android Studio: [follow instructions](https://developer.android.com/studio/write/firebase.html)



## Version
1.0
