package com.philcode.equalsadmin.apis;

import com.philcode.equalsadmin.models.Employer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EmployerAPI {

    @GET("test")
    Call<Employer> getEmployer();

    @POST("create-employer")
    Call<Employer> createEmployer(@Body Employer employer);

    @DELETE("/delete-employer/{uid}")
    Call<Void> deleteEmployer(@Path("uid") String uid);
}
