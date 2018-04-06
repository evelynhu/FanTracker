package com.mii.android.tracking;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VisibleState that = (VisibleState) o;

            if (firstCompletelyVisible != that.firstCompletelyVisible) return false;
            return lastCompletelyVisible == that.lastCompletelyVisible;

        }

        @Override
        public int hashCode() {
            int result = firstCompletelyVisible;
            result = 31 * result + lastCompletelyVisible;
            return result;
        }

        @Override
        public String toString() {
            return "VisibleState{" +
                    "first=" + firstCompletelyVisible +
                    ", last=" + lastCompletelyVisible +
                    '}';
        }

        public int getFirstCompletelyVisible() {
            return this.firstCompletelyVisible;
        }

        public int getLastCompletelyVisible() {
            return this.lastCompletelyVisible;
        }
    }
}
