package com.philcode.equalsadmin.apis;

import com.philcode.equalsadmin.models.Candidate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PwdAPI {

    @POST("create-pwd")
    Call<Candidate> createCandidate(@Body Candidate candidate);

    @DELETE("delete-pwd/{uid}")
    Call<Void> deleteCandidate(@Path("uid") String uid);
}
