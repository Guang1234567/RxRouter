# RxRouter
- Thanks to Rxjava, It is very easy to use. see [Usage](#Usage).
- The router of android for Activity, Fragment, js and everything.

# Usage

1. create Router instance.

```java

final Router router = new Router();
        
```

2. How to route?

- route to activity

```java

final Uri uri001 = new Uri.Builder().scheme("router").authority("www.mycompany.com").appendPath("second_activity").build();
router.to(uri001, SecondActivity.class).subscribe();


btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // router to activity
                Intent args = new Intent();
                args.putExtra("key_aaa", "value_bbb");
                router.route(v.getContext(), uri001, args);
            }
        });

```

- route to do anything


```java

final Object uri002 = new Uri.Builder().scheme("router").authority("www.mycompany.com").appendPath("call_some_method").build();
router.to(uri002).subscribe(new Consumer<Object>() {
    @Override
    public void accept(Object request) throws Exception {
        showToast(request);
    }
});



btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        router.route(uri002);
    }
});

```
