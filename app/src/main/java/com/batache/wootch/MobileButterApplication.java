package com.batache.wootch;

import butter.droid.base.BaseApplicationModule;
import butter.droid.base.ButterApplication;

public class MobileButterApplication extends ButterApplication {

    private ApplicationComponent component;

    public static MobileButterApplication getAppContext() {
        return (MobileButterApplication) ButterApplication.getAppContext();
    }

    @Override
    public void onCreate() {
        component = DaggerApplicationComponent.builder()
                .baseApplicationModule(new BaseApplicationModule(this))
                .build();
        component.inject(this);

        super.onCreate();
    }

    public ApplicationComponent getComponent() {
        return component;
    }

}
