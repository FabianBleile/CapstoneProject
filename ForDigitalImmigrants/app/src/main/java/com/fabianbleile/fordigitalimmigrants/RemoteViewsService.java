package com.fabianbleile.fordigitalimmigrants;

import android.content.Context;
import android.content.Intent;

public class RemoteViewsService  extends android.widget.RemoteViewsService {
    public RemoteViewsService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyRemoteViewsFactory(this.getApplicationContext(), intent) {
        };
    }
}
