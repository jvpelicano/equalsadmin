package com.philcode.equalsadmin.apis;

import com.philcode.equalsadmin.models.Candidate;
import com.philcode.equalsadmin.models.Employer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PwdAPI {

    @POST("create-emp")
    Call<Candidate> createCandidate(@Body Employer employer);

    @DELETE("delete-emp/{uid}")
    Call<Candidate> deleteCandidate(@Path("uid") String uid);
}
