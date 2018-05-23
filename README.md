# RxRouter
- Thanks to Rxjava, **RxRouter** is very easy to used. see [Usage](#usage).
- The router of android for Activity, Fragment, js and everything.

# Download

[ ![Download](https://api.bintray.com/packages/ggg1234567/maven/rxrouter/images/download.svg) ](https://bintray.com/ggg1234567/maven/rxrouter/_latestVersion)

```gradle
implementation 'com.github.router:rxrouter:<newest_verion>'
```
# Usage

1. create Router instance.

```java

final Router router = new Router();
        
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
RxRouters mRouters = new RxRouters(application);
mRouters.registerAlias(SecondActivity.class, NAVI_TO_SECOND.ALIAS_01);
mRouters.registerAlias(SecondActivity.class, NAVI_TO_SECOND.ALIAS_02);



// Step 3 : click some widget to action!!! 
final TextView tv = findViewById(R.id.text);
RxView.clicks(tv)
        .map(new Function<Object, Pair<String, Bundle>>() {
            @Override
            public Pair<String, Bundle> apply(Object o) throws Exception {
                Bundle args = new Bundle();
                args.putString(NAVI_TO_SECOND.PARAM_AAA, "value_bbbbb8");
                return new Pair<>(NAVI_TO_SECOND.ALIAS_02, args);
            }
        })
        .subscribe(mRouters.asAliasConsumer());

```

- route to do anything


```java

public static final String ALIAS_DO_STH_03 = "https://www.mycompany.com/ui/doSth";


RxRouters mRouters = new RxRouters(application);
mRouters.asAliasObservable(NAVI_TO_SECOND.ALIAS_DO_STH_03)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pair<String, Bundle>>() {
                    @Override
                    public void accept(Pair<String, Bundle> pair) throws Exception {
                        // do sth here
                        Toast.makeText(application, String.valueOf(pair), Toast.LENGTH_SHORT).show();                    }
                });

                
final TextView tv = findViewById(R.id.text);
RxView.clicks(tv)
        .map(new Function<Object, Pair<String, Bundle>>() {
            @Override
            public Pair<String, Bundle> apply(Object o) throws Exception {
                Bundle args = new Bundle();
                args.putString(NAVI_TO_SECOND.PARAM_AAA, "value_bbbbb8");
                return new Pair<>(NAVI_TO_SECOND.ALIAS_DO_STH_03, args);
            }
        })
        .subscribe(mRouters.asAliasConsumer());                
```
