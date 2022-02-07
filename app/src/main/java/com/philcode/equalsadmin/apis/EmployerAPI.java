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

    @POST("create-emp")
    Call<Employer> createEmployer(@Body Employer employer);

    @DELETE("delete-emp/{uid}")
    Call<Employer> deleteEmployer(@Path("uid") String uid);
}
