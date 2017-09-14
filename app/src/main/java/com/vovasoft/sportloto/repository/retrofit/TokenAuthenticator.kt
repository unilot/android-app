package com.vovasoft.sportloto.repository.retrofit

import android.content.Context
import com.vovasoft.sportloto.BuildConfig
import com.vovasoft.sportloto.Preferences
import com.vovasoft.sportloto.R
import com.vovasoft.sportloto.repository.retrofit.requests.AuthorizationRequest
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/***************************************************************************
 * Created by arseniy on 15/09/2017.
 ****************************************************************************/
class TokenAuthenticator(private val context: Context) : Authenticator {
    /**
     * Returns a request that includes a credential to satisfy an authentication challenge in `response`. Returns null if the challenge cannot be satisfied.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val webservice = retrofit.create(WebService::class.java)
        val authorizationModel = webservice.authorize(AuthorizationRequest(
                context.getString(R.string.client_id),
                context.getString(R.string.client_secret),
                context.getString(R.string.grant_type))).execute().body()

        authorizationModel?.let {
            Preferences.instance.token = it.token
            return response.request().newBuilder().header("Authorization", "Bearer ${Preferences.instance.token}").build()
        }

        return null
    }
}