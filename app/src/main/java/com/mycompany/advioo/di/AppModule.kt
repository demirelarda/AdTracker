package com.mycompany.advioo.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mycompany.advioo.R
import com.mycompany.advioo.api.CityAPI
import com.mycompany.advioo.models.user.User
import com.mycompany.advioo.repo.CityRepository
import com.mycompany.advioo.repo.CityRepositoryInterface
import com.mycompany.advioo.repo.UserRepository
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.util.Util.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofitAPI(): CityAPI{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(CityAPI::class.java)
    }

    @Singleton
    @Provides
    fun injectNormalCityRepo(api:CityAPI) = CityRepository(api) as CityRepositoryInterface

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context) = Glide.with(context)
        .setDefaultRequestOptions(
            RequestOptions().placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
        )


    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    @Singleton
    @Provides
    fun provideNormalUserRepository(db: FirebaseFirestore) = UserRepository(db) as UserRepositoryInterface


    @Singleton
    @Provides
    fun provideUserObject(): User {
        return User(
            id = "",
            firstName = "",
            lastName = "",
            email = "",
            city = "",
            addressFullName = "",
            addressRow1 = "",
            addressRow2 = "",
            zipCode = "",
            regDate = Timestamp.now(),
            carBrand = "",
            carYear = "",
            carModel = "",
            carCondition = "",
            workCity = "",
            licensePlate ="",
            allowedContact = false,
            avgKmDriven = "",
            rideShareDriver = false
        )
    }





}