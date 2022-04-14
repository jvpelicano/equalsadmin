package com.philcode.equalsadmin.apis;
import com.philcode.equalsadmin.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPI {

    @POST("create-user")
    Call<User> createUser(@Body User user);

    @DELETE("/delete-user/{uid}")
    Call<Void> deleteUser(@Path("uid") String uid);
}
