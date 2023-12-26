package uniba.roadhouse.asilapp.controller.other;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import uniba.roadhouse.asilapp.model.dao.Country;

/**
 * Interfaccia per l'utilizzo delle API di RestCountry.
 */
public interface CountryService {
    @GET("all")
    Call<List<Country>> getAllCountries();
}