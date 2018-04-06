package com.mii.android.ad;

public class AdStore {
    private SimpleAdFactory  mFactory;

    public AdStore(SimpleAdFactory factory) {
        mFactory = factory;
    }

    public IAdAction getAdAction(int adType) {
        IAdAction adAction;
        adAction = mFactory.createAd(adType);
        return adAction;
    }

    // Usage :
    //    AdStore store = new AdStore(new SimpleAdFactory(getContext(), "YOUR_PLACEMENT_ID", mBannerAdContainer);
    //    IAdAction adAction = store.getAdAction(type);
    //    adAction.create();
    //    adAction.load();
}