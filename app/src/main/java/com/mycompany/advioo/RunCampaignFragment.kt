package com.mycompany.advioo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.advioo.databinding.FragmentRunCampaignBinding
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.MyPair
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.models.pinfo.Nredrate
import com.mycompany.advioo.models.pinfo.Phour
import com.mycompany.advioo.models.pinfo.Pinfo
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.services.LocationTrackingService
import com.mycompany.advioo.services.TripData
import com.mycompany.advioo.util.Status
import com.mycompany.advioo.viewmodels.RunCampaignViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject


private const val LOCATION_AND_NOTIFICATION_PERMISSION_REQUEST_CODE = 1
private const val BATTERY_OPTIMIZATION_REQUEST_CODE = 2
private var pInfoList : ArrayList<Pinfo> = ArrayList()
private var pHours : ArrayList<Phour> = ArrayList()
private var nDownRate : ArrayList<Nredrate> = ArrayList()
private var lastDistanceInKm : Double = 0.0
private var lastPayment: Double = 0.0
private var campaignApplication = CampaignApplication()
private lateinit var snackbar: Snackbar

//TODO: REFRESH (REMOVE) KM AFTER BACK BUTTON PRESSED (OR FRAGMENT DESTROYED -> NOT SURE. THIS MIGHT EFFECT RE-ENTERING AFTER TURNING OFF THE SCREEN)


class RunCampaignFragment : Fragment() {

    private var _binding: FragmentRunCampaignBinding? = null
    private val binding get() = _binding!!
    private lateinit var tripData: TripData
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    lateinit var runCampaignViewModel: RunCampaignViewModel
    val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var tripId: String
    private lateinit var sessionId: String
    private var locationCounter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunCampaignBinding.inflate(inflater, container, false)



        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionId = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
        println("run campaign onView Created")

        if(requireActivity().intent.hasExtra("campaignApplication")){
            campaignApplication = requireActivity().intent.getParcelableExtra<CampaignApplication>("campaignApplication")!!
        }

        snackbar = Snackbar.make(view, "You are out of bounds! Get back to campaign area!", Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSnackBarError))

        runCampaignViewModel = ViewModelProvider(requireActivity()).get(RunCampaignViewModel::class.java)
        runCampaignViewModel.getPinfoFromApi()
        subscribeToObservers()
        startTracking()

        binding.btnEndTripRunCampaign.setOnClickListener {
            stopTracking()
            lastDistanceInKm = 0.0
            runCampaignViewModel.resetPayment()
        }


    }


    private fun startTracking() {
        val startIntent = Intent(requireContext(), LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START
            putParcelableArrayListExtra(LocationTrackingService.EXTRA_LOCATION_LIST, campaignApplication.selectedCampaign.mapBorderLocationList)
        }
        ContextCompat.startForegroundService(requireContext(), startIntent)

    }




    private fun stopTracking() {
        val stopIntent = Intent(requireContext(), LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_STOP
        }
        requireContext().stopService(stopIntent)
        LocationTrackingService.distanceDriven.value = 0.0
        runCampaignViewModel.resetPayment()
        requireActivity().onBackPressed()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        subscribeToObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        subscribeToObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUtcToLocalTime(utcTime: Instant, timeZone: String): ZonedDateTime {
        println("zoneId = ${ZoneId.of(timeZone)}")
        val zoneId = ZoneId.of(timeZone)
        return ZonedDateTime.ofInstant(utcTime, zoneId)
    }





    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers(){
        runCampaignViewModel.pInfo.observe(viewLifecycleOwner, Observer{
            when(it.status){
                Status.SUCCESS->{
                    binding.progressBarRunCampaign.visibility = View.GONE
                    it.data?.let { it1 -> pInfoList.addAll(it1.pinfos) }
                    it.data?.let { it2 -> pHours.addAll(it2.phours) }
                    it.data?.let { it3 -> nDownRate.addAll(it3.nredrate) }
                    binding.rlRunCampaign.visibility = View.VISIBLE
                    it.data?.let { it1 -> runCampaignViewModel.pInfoList.addAll(it1.pinfos) }
                    it.data?.let { it2 -> runCampaignViewModel.pHours.addAll(it2.phours) }
                    it.data?.let { it3 -> runCampaignViewModel.nDownRate.addAll(it3.nredrate) }
                }
                Status.ERROR->{
                    Toast.makeText(requireContext(),getString(R.string.an_error_occurred_network),Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }
                Status.LOADING->{
                    binding.rlRunCampaign.visibility = View.GONE
                    binding.progressBarRunCampaign.visibility = View.VISIBLE
                }
            }
        })

        runCampaignViewModel.payment.observe(viewLifecycleOwner, Observer {
            println("PAYMENT = $it")
            binding.tvPaymentRunCampaign.text = String.format("$%.2f ", it)
        })

        runCampaignViewModel.multiplier.observe(viewLifecycleOwner, Observer {
            //binding.tvMultiplier.text =  String.format("x%.2f ", it)
        })

        runCampaignViewModel.campaignType.observe(viewLifecycleOwner, Observer {
            //binding.tvCampaignType.text = it.toString()
        })


        LocationTrackingService.distanceDriven.observe(viewLifecycleOwner, Observer {distanceDriven->
            println("distance driven observed = $distanceDriven")
            binding.tvDistanceDrivenRunCampaign.text = String.format("%.2f",distanceDriven)+ " " + getString(R.string.km)
            val application : Application = AdviooApplication()
            if (distanceDriven > lastDistanceInKm) {
                val distanceDifference = distanceDriven - lastDistanceInKm
                println("distance difference= $distanceDifference")
                (application as AdviooApplication).trueTime.sync()
                val currentTime = (application as AdviooApplication).trueTime.now()
                val timeZone = campaignApplication.selectedCampaign.campaignTimeZone
                val localTime = convertUtcToLocalTime(currentTime.toInstant(), timeZone)
                println("current gmt time (true time) : $currentTime")
                println("User location current time =  ($timeZone): $localTime")
                println("zoned hour = ${localTime.hour}")
                val isNight = localTime.hour !in 6..18
                println("true time = $currentTime")
                println("is night = $isNight")
                runCampaignViewModel.calculatePayment("L1", distanceDifference, isNight) //TODO: CHANGE THE LEVEL, Set it dynamically
                lastDistanceInKm = distanceDriven
                tripId = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
                val currentPayment = runCampaignViewModel.payment.value ?: 0.0
                val paymentDifference = currentPayment - lastPayment
                val localTripData = UserTripData(
                    tripId = tripId,
                    campaignApplicationId= campaignApplication.applicationId,
                    driverId = firebaseAuth.uid.toString(),
                    campaignId = campaignApplication.selectedCampaign.campaignId,
                    kmDriven = distanceDifference,
                    earnedPayment = paymentDifference,
                    localSaveDate = System.currentTimeMillis(),
                    isUploaded = false,
                )
                lastPayment = currentPayment
                runCampaignViewModel.saveLocalTripData(localTripData)
            }
        })

        LocationTrackingService.isOutOfBounds.observe(viewLifecycleOwner, Observer { isOutOfBounds ->
            if (isOutOfBounds) {
                if (!snackbar.isShown) {
                    snackbar.show()
                }
            } else {
                snackbar.dismiss()
            }
        })

        LocationTrackingService.userLocations.observe(viewLifecycleOwner, Observer{ userLocations ->
            if(userLocations != null) {
                if(userLocations.isNotEmpty()) {
                    locationCounter += userLocations.size
                    //TODO: CONSIDER CHECKING THE PREVIOUS (SAVED) LOCATION LIST DATA AND ADD ON TO IT IF THE SIZE OF IT <8000
                    if(locationCounter >= 8000) {
                        sessionId = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
                        locationCounter = 0
                    }

                    val userTripLocationData = TripLocationData(
                        tripId = sessionId,
                        locationList = userLocations,
                        campaignId= campaignApplication.selectedCampaign.campaignId,
                        date = System.currentTimeMillis(),
                        driverId = firebaseAuth.currentUser!!.uid
                    )

                    runCampaignViewModel.saveTripLocationData(userTripLocationData)
                }
            }
        })





    }





}