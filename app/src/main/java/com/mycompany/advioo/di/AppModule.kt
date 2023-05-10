package com.mycompany.advioo.di

import android.app.Application
import android.content.Context
import android.location.LocationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mycompany.advioo.R
import com.mycompany.advioo.api.CityAPI
import com.mycompany.advioo.api.PinfoAPI
import com.mycompany.advioo.api.TimeAPI
import com.mycompany.advioo.dao.CampaignApplicationDao
import com.mycompany.advioo.dao.DriverDao
import com.mycompany.advioo.db.UserDatabase
import com.mycompany.advioo.models.user.Driver
import com.mycompany.advioo.models.user.UserCity
import com.mycompany.advioo.repo.*
import com.mycompany.advioo.repo.local.LocalDriverRepository
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import com.mycompany.advioo.util.HaversineCalculateDistance
import com.mycompany.advioo.util.Util.BASE_URL
import com.mycompany.advioo.util.Util.PINFO_BASE_URL
import com.mycompany.advioo.util.Util.TIME_API_BASE_URL
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

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

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
    fun providePinfoAPI() : PinfoAPI{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(PINFO_BASE_URL)
            .build()
            .create(PinfoAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideTimeAPI() : TimeAPI{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(TIME_API_BASE_URL)
            .build()
            .create(TimeAPI::class.java)
    }

    @Singleton
    @Provides
    fun injectNormalCityRepo(api:CityAPI) = CityRepository(api) as CityRepositoryInterface


    @Singleton
    @Provides
    fun injectNormalPinfoRepo(api:PinfoAPI) = PinfoRepository(api) as PinfoRepositoryInterface

    @Singleton
    @Provides
    fun injectNormalTimeRepo(api:TimeAPI) = TimeRepository(api) as TimeRepositoryInterface

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context): RequestManager {
        val requestOptions = RequestOptions()
            .placeholder(android.R.drawable.progress_horizontal)
            .error(R.drawable.iv_rounded_bg)

        return Glide.with(context)
            .setDefaultRequestOptions(requestOptions)
    }


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
    fun provideNormalCampaignRepository(db: FirebaseFirestore) = CampaignRepository(db) as CampaignRepositoryInterface

    @Singleton
    @Provides
    fun provideNormalInstallerRepository(db: FirebaseFirestore) = InstallerRepository(db) as InstallerRepositoryInterface

    @Singleton
    @Provides
    fun provideNormalCampaignEnrollmentRepository(db: FirebaseFirestore) = CampaignEnrollmentRepository(db) as CampaignEnrollmentRepositoryInterface

    @Singleton
    @Provides
    fun provideNormalCampaignApplicationRepository(db: FirebaseFirestore) = CampaignApplicationRepository(db) as CampaignApplicationRepositoryInterface


    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): UserDatabase {
        return UserDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideLocalDriverDao(userDatabase: UserDatabase): DriverDao {
        return userDatabase.driverDao()
    }

    @Singleton
    @Provides
    fun provideLocalCampaignApplicationDao(userDatabase: UserDatabase) : CampaignApplicationDao{
        return  userDatabase.campaignApplicationDao()
    }

    @Singleton
    @Provides
    fun provideLocalDriverRepository(driverDao: DriverDao,campaignApplicationDao: CampaignApplicationDao): LocalDriverRepositoryInterface {
        return LocalDriverRepository(driverDao,campaignApplicationDao)
    }



    @Singleton
    @Provides
    fun provideHaversine() : HaversineCalculateDistance{
        return HaversineCalculateDistance()
    }

    @Singleton
    @Provides
    fun provideLocationManager(context: Context): LocationManager {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Singleton
    @Provides
    fun provideUserCityObject() : UserCity{
        return UserCity(
            stateId = "",
            cityId = "",
            stateName = "",
            cityName = "",
        )
    }


    @Singleton
    @Provides
    fun provideUserObject(userCity: UserCity): Driver {
        return Driver(
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
            rideShareDriver = false,
            userCity = userCity
        )
    }





}