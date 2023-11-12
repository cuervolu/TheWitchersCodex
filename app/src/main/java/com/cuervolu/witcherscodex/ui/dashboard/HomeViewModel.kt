package com.cuervolu.witcherscodex.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuervolu.witcherscodex.BuildConfig
import com.cuervolu.witcherscodex.ContextModule
import com.cuervolu.witcherscodex.data.network.ArticleService
import com.cuervolu.witcherscodex.data.network.BestiaryService
import com.cuervolu.witcherscodex.data.network.TwitchService
import com.cuervolu.witcherscodex.data.response.Datum
import com.cuervolu.witcherscodex.domain.models.Article
import com.cuervolu.witcherscodex.domain.models.Bestiary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleService: ArticleService,
    private val bestiaryService: BestiaryService,
) : ViewModel() {
    // Define LiveData u ObservableFields para los datos que necesita el fragmento.
    private val _streams = MutableLiveData<List<Datum>>()
    val streams: LiveData<List<Datum>> = _streams
    private val _trendingBeasts = MutableLiveData<List<Bestiary>>()
    val trendingBeast: LiveData<List<Bestiary>> = _trendingBeasts
    private val _featuredArticles = MutableLiveData<List<Article>>()
    val featuredArticles: LiveData<List<Article>> = _featuredArticles

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    private val _navigateToBestiary = MutableLiveData<Boolean>()
    val navigateToBestiary: LiveData<Boolean> = _navigateToBestiary

    private val _navigateToWeapon = MutableLiveData<Boolean>()
    val navigateToWeapon: LiveData<Boolean> = _navigateToWeapon
    private val _navigateToCharacters = MutableLiveData<Boolean>()
    val navigateToCharacters: LiveData<Boolean> = _navigateToCharacters

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    // Implementa métodos para cargar los datos necesarios desde el repositorio.
    fun loadFeaturedArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            articleService.getArticles(
                onSuccess = { articles ->
                    // Actualiza _featuredArticles con los artículos obtenidos
                    _featuredArticles.postValue(articles)

                },
                onError = {
                    _showErrorDialog.value = true
                }
            )
        }
    }

    fun onBestiarySelected() {
        _navigateToBestiary.value = true
    }

    fun onBestiaryNavigated() {
        _navigateToBestiary.value = false
    }

    fun onWeaponsSelected() {
        _navigateToWeapon.value = true
    }

    fun onWeaponsNavigated() {
        _navigateToWeapon.value = false
    }


    fun onCharactersSelected() {
        _navigateToCharacters.value = true
    }

    fun onCharactersNavigated() {
        _navigateToCharacters.value = false
    }


    fun loadTrendingBeastEntries() {
        CoroutineScope(Dispatchers.IO).launch {
            bestiaryService.getLastFiveBestiaryEntries(
                onSuccess = { entries ->
                    _trendingBeasts.postValue(entries)
                },
                onError = {
                    _showErrorDialog.value = true
                }
            )
        }
    }

    fun searchStreamsByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getStreams().create(TwitchService::class.java).getStreamsByGame(query)
            val streams = call.body()
            if (call.isSuccessful && streams != null) {
                _streams.postValue(streams.data)
            } else {
                val errorBody = call.errorBody()
                val errorMessage = errorBody?.string()
                Timber.e("Error: $errorMessage")
            }
        }
    }

    private fun getStreams(): Retrofit {
        val clientId = BuildConfig.CLIENT_ID
        val authorization = BuildConfig.Authorization
        Timber.d("Client ID: $clientId")
        Timber.d("Authorization: $authorization")

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val originalRequest: Request = chain.request()
                val requestBuilder: Request.Builder = originalRequest.newBuilder()
                    .header("Client-ID", clientId)
                    .header("Authorization", "Bearer $authorization")
                    .method(originalRequest.method, originalRequest.body)
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            })
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    // Método para indicar que se ha seleccionado el fragmento de inicio
    fun onHomeSelected() {
        _navigateToHome.value = true
    }


}