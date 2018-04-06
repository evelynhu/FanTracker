package com.mii.android.ui.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;

import com.facebook.ads.AdSettings;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mii.android.R;
import com.mii.android.ad.AdStore;
import com.mii.android.ad.IAdAction;
import com.mii.android.ad.SimpleAdFactory;
import com.mii.android.model.Post;
import com.mii.android.model.Database;
import com.mii.android.tracking.ThrottleTrackingBus;
import com.mii.android.tracking.ThrottleTrackingBus.VisibleState;
import com.mii.android.ui.base.BaseFragment;
import com.mii.android.util.AdType;
import com.mii.android.util.Constant;
import com.mii.android.util.RxUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;


public class PageFragment extends BaseFragment {

    private static final String TAG = PageFragment.class.getName();

    private boolean ENABLE_SHIMMER = true;

    private ArrayList<Integer> mAdTypes = new ArrayList<>();

    @BindView(R.id.page_emptyView)
    View mEmptyView;

    @BindView(R.id.page_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;

    @BindView(R.id.banner_container)
    LinearLayout mBannerAdContainer;

    private ThrottleTrackingBus mTrackingBus;

    private LinearLayoutManager mLinearLayoutManager;

    private static FirebaseDatabase mDatabase;

    private static DatabaseReference mPostReference;

    private static ChildEventListener mChildEventListener;

    private List<Object> mRecyclerViewItems;

    PageAdapter mAdapter;

    private Unbinder mUnbinder;

    List<IAdAction> mAdList = new ArrayList<>();

    public static PageFragment newInstance(ArrayList<Integer> adTyps) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("AdTypes", adTyps);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
        mRecyclerViewItems = new ArrayList<>();
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_post;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        AdSettings.addTestDevice(Constant.TEST_DEVICE_HASH);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("AdTypes", mAdTypes);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Integer> adTypes = savedInstanceState.getIntegerArrayList("AdTypes");
            if (adTypes != null) {
                mAdTypes = adTypes;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        ArrayList<Integer> adTypes = bundle.getIntegerArrayList("AdTypes");
        if (adTypes != null) {
            mAdTypes = adTypes;
        }

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new PageAdapter(getContext(), mRecyclerViewItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //At this point the layout is complete and the
                //dimensions of recyclerView and any child views are known.
                if (mRecyclerView.getChildCount() > 0) {
                    afterPageLoading();
                }
            }
        });
        initShimmer();
        createAd();
    }

    private void createAd() {
        for (int type : mAdTypes) {
            if (type == AdType.NATIVE) { // Loading native ad after recycler view is ready
                continue;
            }
            AdStore store = new AdStore(new SimpleAdFactory(getContext(), Constant.PLACEMENT_ID, mBannerAdContainer, mRecyclerViewItems, mAdapter));
            IAdAction adAction = store.getAdAction(type);
            mAdList.add(adAction);
            adAction.create();
        }
    }

    private void initShimmer() {
        if (ENABLE_SHIMMER) {
            mShimmerViewContainer.setVisibility(View.VISIBLE);
        } else {
            mShimmerViewContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.initDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
        mTrackingBus = new ThrottleTrackingBus(this::onTrackViewResponse, RxUtil.logError());
        mRecyclerView.addOnScrollListener(detectingItemObservedByUser);
        mLinearLayoutManager.scrollToPosition(mLinearLayoutManager.findLastCompletelyVisibleItemPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();
        mTrackingBus.unsubscribe();
        mRecyclerView.removeOnScrollListener(detectingItemObservedByUser);
    }

    @Override
    public void onDestroyView() {
        destroyAd();
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void destroyAd() {
        for (IAdAction ad : mAdList) {
            if (ad != null) {
                ad.destroy();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPostReference != null) {
            mPostReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
            mPostReference = null;
        }
    }

    void onTrackViewResponse(VisibleState visibleState) {
        Log.d("Track", "Received to be tracked: " + visibleState.toString());
        if (visibleState.getFirstCompletelyVisible() < 0) {
            return;
        }
        int position = visibleState.getFirstCompletelyVisible();
        if (mAdapter.getItemViewType(position) == PageAdapter.POST) {

            Post post = (Post) mAdapter.getItem(position);
            Log.d("Track", "current view: " + post.getShortCaption());
            this.updatePost(post.getRef(), post.getViewer() + 1, post.getShortCaption());
        } else if (mAdapter.getItemViewType(position) == PageAdapter.NATIVE_AD) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getContext(), "One person is viewing this ad ",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private RecyclerView.OnScrollListener detectingItemObservedByUser = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                final VisibleState visibleStateFinal = new VisibleState(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition(), mLinearLayoutManager.findLastCompletelyVisibleItemPosition());
                mTrackingBus.postViewEvent(visibleStateFinal);
                String state = newState == RecyclerView.SCROLL_STATE_IDLE ? "stopped..." : "settling...";
                Log.i("scroll", "[onScrollStateChanged] scrolling " + state );
            }
        }
    };


    private void initDatabase() {
        mDatabase = Database.getDatabase();
        mPostReference = mDatabase.getReference(Post.REF_POST);
        mPostReference.keepSynced(true);
        this.addNativeAdListener();
        this.observeAllPosts();
    }

    @SuppressLint("CheckResult")
    private void addNativeAdListener() {
        RxFirebaseDatabase.observeSingleValueEvent(mPostReference.orderByChild("timeStamp"), DataSnapshotMapper.mapOf(Post.class))
                .subscribe(postsAsMapItem -> {
                    initNativeAd();
                }, throwable -> {
                });
    }

    private void initNativeAd() {
        AdStore store = new AdStore(new SimpleAdFactory(getContext(), Constant.PLACEMENT_ID, mBannerAdContainer, mRecyclerViewItems, mAdapter));
        IAdAction adAction = store.getAdAction(AdType.NATIVE);
        mAdList.add(adAction);
        adAction.create();
    }

    private void observeAllPosts() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String postKey = dataSnapshot.getKey();
                if (mAdapter.containsKey(postKey)) {
                    Log.d(TAG, "duplicated onChildAdded event");
                    return;
                }

                Post post = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "[onChildAdded] " + postKey + " : " + post.getCaption());

                post.setRef(postKey);
                mRecyclerViewItems.add(post);
                mAdapter.addKey(postKey);
                mAdapter.notifyDataSetChanged();
                mLinearLayoutManager.smoothScrollToPosition(mRecyclerView, null, mAdapter.getItemCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                String postKey = dataSnapshot.getKey();
                Post newPost = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "[onChildChanged] " + postKey + " : " + newPost.getCaption());

                newPost.setRef(postKey);
                int previousIndex = mAdapter.getPostionFromKey(postKey);
                mRecyclerViewItems.set(previousIndex, newPost);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String postKey = dataSnapshot.getKey();
                if (!mAdapter.containsKey(postKey)) {
                    Log.d(TAG, "duplicated onChildRemoved event");
                    return;
                }
                Post removedPost = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "[onChildRemoved] " + postKey + " : " + removedPost.getCaption());

                mRecyclerViewItems.remove(removedPost);
                mAdapter.removeKey(postKey);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "[onChildMoved] " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "[onCancelled] ", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load poist.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mPostReference.addChildEventListener(mChildEventListener);
    }

    private void afterPageLoading() {
        if (mShimmerViewContainer != null) {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        }
        if (mRecyclerViewItems.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updatePost(String postId, long viewer, String shortCaption) { // TODO prevent conflict
        Log.i("Track", "updatePost : " + postId + " : " + shortCaption + ", " + viewer);

        if (postId == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Log.i("Track", "show toast : " + shortCaption);
                Toast.makeText(getContext(), "One person is viewing this post " + shortCaption,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mPostReference.child(postId).child("viewer").setValue(viewer);
    }
}
