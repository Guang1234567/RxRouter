# RxRouter
- Thanks to Rxjava, **RxRouter** is very easy to used. see [Usage](#usage).
- The router of android for Activity, Fragment, js and everything.
- Using as UrlRouter with `WebView`

# Download

[ ![Download](https://api.bintray.com/packages/ggg1234567/maven/rxrouter/images/download.svg) ](https://bintray.com/ggg1234567/maven/rxrouter/_latestVersion)

```gradle
implementation 'com.github.router:rxrouter:<newest_verion>'
```
# Usage

1. create Router instance.

```java

public final class ActivityNavigator {

    public final static class NAVI_TO_SECOND {
        public static final String ALIAS_01 = "rxrouter://www.mycompany.com/ui/second_activity_1";
        public static final String ALIAS_02 = "https://www.mycompany.com/ui/second_activity_2";

        public static final String ALIAS_DO_STH_03 = "https://www.mycompany.com/ui/doSth";

        public static final String PARAM_AAA = "param_aaa";
    }

    private RxRouters mRouters;

    private List<Activity> mActivitys;

    private final PublishSubject<Object> mKillSwitch = PublishSubject.create();

    public ActivityNavigator(Application application) {
        initLifecycle(application);
        initRouter(application);
    }

    private void initRouter(final Application application) {
        mRouters = new RxRouters(AndroidSchedulers.mainThread(),
                null,
                new ObservableTransformer<ActivityRouter.Query, ActivityRouter.Query>() {
                    @Override
                    public ObservableSource<ActivityRouter.Query> apply(Observable<ActivityRouter.Query> upstream) {
                        return upstream.takeUntil(mKillSwitch);
                    }
                });
        mRouters.activityRouter()
                .createQuery(NAVI_TO_SECOND.ALIAS_01, SecondActivity.class, application)
                .subscribe(new Consumer<ActivityRouter.Query>() {
                    @Override
                    public void accept(ActivityRouter.Query query) throws Exception {
                        query.run();
                    }
                });
        mRouters.activityRouter()
                .createQuery(NAVI_TO_SECOND.ALIAS_02, SecondActivity.class, application)
                .subscribe(new Consumer<ActivityRouter.Query>() {
                    @Override
                    public void accept(ActivityRouter.Query query) throws Exception {
                        query.run();
                    }
                });
        mRouters.simpleRouter()
                .createQuery(NAVI_TO_SECOND.ALIAS_DO_STH_03)
                .subscribe(new Consumer<Bundle>() {
                    @Override
                    public void accept(Bundle args) throws Exception {
                        Toast.makeText(application, String.valueOf(args), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initLifecycle(Application application) {
        mActivitys = new LinkedList<>();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivitys.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (!mActivitys.isEmpty()) {
                    mActivitys.remove(activity);
                }
            }
        });
    }

    @MainThread
    private void doFinishAllActivity() {
        if (mActivitys.isEmpty()) {
            return;
        }
        Iterator<Activity> iterator = mActivitys.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            activity.finish();
            iterator.remove();
        }

        mKillSwitch.onNext("kill");
    }

    public void finishAllActivity() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            doFinishAllActivity();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    doFinishAllActivity();
                }
            });
        }
    }

    public void naviTo(String alias, Bundle args) {
        mRouters.route(alias, args);
    }
}
```

2. How to route?

- route to activity

```java

// Step 1 : define some string constant for "alias"
public final static class NAVI_TO_SECOND {
    public static final String ALIAS_01 = "rxrouter://www.mycompany.com/ui/second_activity_1";
    public static final String ALIAS_02 = "https://www.mycompany.com/ui/second_activity_2";

    public static final String PARAM_AAA = "param_aaa";
}


// Step 2 : init and register "alias" into RxRouters instance
mRouters.activityRouter()
                .createQuery(NAVI_TO_SECOND.ALIAS_01, SecondActivity.class, application)
                .subscribe(new Consumer<ActivityRouter.Query>() {
                    @Override
                    public void accept(ActivityRouter.Query query) throws Exception {
                        query.run();
                    }
                });



// Step 3 : click some widget to action!!! 
final TextView tv = findViewById(R.id.text);
        RxView.clicks(tv)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Bundle args = new Bundle();
                        args.putString(NAVI_TO_SECOND.PARAM_AAA, "value_bbbbb8");
                        mNavigator.naviTo(NAVI_TO_SECOND.ALIAS_02, args);
                    }
                });

```

- route to do anything (eg: using as "url router" with [JsBridge](https://github.com/lzyzsd/JsBridge))


```java
public final static class NAVI_TO_DO_STH {

    public static final String ALIAS_GPS_CURRENT_POS = "https://www.mycompany.com/ui/gps_current_pos";

}

public class GpsPlugin implements ObservableTransformer<Bundle, PointF> {

    private final Application mApplication;

    //private final GpsGoogleApi mGpsGoogleApi;

    public GpsPlugin(Application application/*, GpsGoogleApi api*/) {
        mApplication = application;
        //mGpsGoogleApi = api;
    }

    @Override
    public ObservableSource<PointF> apply(Observable<Bundle> upstream) {
        return upstream.map(new Function<Bundle, PointF>() {
            @Override
            public PointF apply(Bundle bundle) throws Exception {
                // simulate the position.
                // maybe you will use google map api here in dev.
                // eg: mGpsGoogleApi.getCurrentPos();
                return new PointF(0.0f, 1.1f);
            }
        });
    }
}

mRouters.simpleRouter()
                .createQuery(NAVI_TO_DO_STH.ALIAS_GPS_CURRENT_POS)
                .compose(new GpsPlugin(application))
                .subscribe(new Consumer<PointF>() {
                    @Override
                    public void accept(PointF currentPos) throws Exception {
                        Toast.makeText(application, "My current position : " + String.valueOf(currentPos), Toast.LENGTH_SHORT).show();
                    }
                });
                

final TextView tv = findViewById(R.id.text);
        RxView.clicks(tv)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Bundle args = new Bundle();
                        mNavigator.naviTo(NAVI_TO_DO_STH.ALIAS_GPS_CURRENT_POS, args);
                    }
                });        
```
