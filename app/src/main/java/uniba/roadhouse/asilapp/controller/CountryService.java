package uniba.roadhouse.asilapp.controller;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import uniba.roadhouse.asilapp.model.dao.Country;

public interface CountryService {
    @GET("all")
    Call<List<Country>> getAllCountries();
}