package com.utopia.bttendance.service;

import com.utopia.bttendance.model.json.UserJson;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 9..
 */
public class BTService {

    private static final String SERVER_DOMAIN = "http://www.bttendance.com";

    private static RestAdapter mRestAdapter = new RestAdapter.Builder()
            .setServer(SERVER_DOMAIN + "/api")
            .build();

    private static BTAPI mBTAPI = mRestAdapter.create(BTAPI.class);

    public static void signin(String username, String password, final Callback cb) {
        mBTAPI.signin(username, password, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                cb.failure(retrofitError);
            }
        });
    }

    public static void signup(UserJson user, final Callback cb) {
        mBTAPI.signup(user, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                cb.failure(retrofitError);
            }
        });
    }
}
