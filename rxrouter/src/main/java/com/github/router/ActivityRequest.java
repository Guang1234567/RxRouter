package com.github.router;

import android.content.Context;
import android.content.Intent;

/**
 * @author Guang1234567
 * @date 2018/2/27 11:15
 */

public class ActivityRequest<T> extends Request<T> {
    private Context mFrom;
    private Intent mArgs;

    public ActivityRequest(Context from, T to, Intent args) {
        super(to);
        mFrom = from;
        mArgs = args;
    }

    public Context getFrom() {
        return mFrom;
    }

    public Intent getArgs() {
        return mArgs;
    }
}
