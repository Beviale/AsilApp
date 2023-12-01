package uniba.roadhouse.asilapp.model.dao;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Country {
    private String name;

    public String getName() {
        return name;
    }

    public static class RetrofitInstance {
        private static final String BASE_URL = "https://restcountries.com/v2/";

        private static Retrofit retrofit;

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
}