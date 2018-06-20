package com.beesightsoft.training6.factory;

import com.beesightsoft.training6.domain.http.HttpActivity;
import com.beesightsoft.training6.domain.login.LoginActivity;

import dagger.Component;

@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(LoginActivity loginActivity);
    void inject(HttpActivity httpActivity);

}
