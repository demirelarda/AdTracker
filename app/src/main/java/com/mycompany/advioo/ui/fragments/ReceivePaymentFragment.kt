package com.mycompany.advioo.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentReceivePaymentBinding
import com.mycompany.advioo.models.payment.PaymentRequest
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.viewmodels.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.UUID
import com.bumptech.glide.request.target.Target
import com.mycompany.advioo.util.SnackbarHelper
import java.util.Calendar


@AndroidEntryPoint
class ReceivePaymentFragment : Fragment() {

    private var _binding: FragmentReceivePaymentBinding? = null
    private val binding get() = _binding!!
    private val paymentViewModel: PaymentViewModel by viewModels()
    private var calculatedAmount = 0
    private lateinit var tripDataListInstance: UserTripData //instance for accessing user and campaign details like: tripDataListInstance.campaignApplicationId
    private var hasPhotoTaken: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReceivePaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        subscribeToObservers()
        setupOnClickListeners()

        val bundle = arguments
        if(bundle != null){
            val args = ReceivePaymentFragmentArgs.fromBundle(bundle)
            if(args.takenPhotosList != null){ //coming from TakePhotoFragment
                //loading the image into the imageView

            }
        }

        if(requireActivity().intent.hasExtra("tripDataList")){
            val tripDataList = requireActivity().intent.getParcelableArrayListExtra<UserTripData>("tripDataList")
            val minKm = requireActivity().intent.getIntExtra("minKm",0)
            val calendar = Calendar.getInstance() //todo: remove
            calendar.add(Calendar.DAY_OF_YEAR, -10) //todo: remove
            if (tripDataList != null) {
                tripDataListInstance = tripDataList[0]
                paymentViewModel.checkPaymentStatusAndCalculateIfNecessary(
                    tripDataList = tripDataList.toList(),
                    currentCampaignMinKm = minKm,
                    startTime = calendar.timeInMillis) //todo: use real user campaign start time
            }
            else{
                paymentViewModel.checkPaymentStatusAndCalculateIfNecessary(listOf(),minKm,calendar.timeInMillis) //todo: remove
                binding.tvPaymentNotAvailable.visibility = View.VISIBLE
            }
        }
    }

    private fun subscribeToObservers(){
        paymentViewModel.calculatedAmount.observe(viewLifecycleOwner){calculatedPayment->
            println("calculated payment = ${calculatedPayment.second}")

        }
        paymentViewModel.shouldCalculatePayment.observe(viewLifecycleOwner){shouldCalculatePayment->
            if(!shouldCalculatePayment){
                println("should not calculate payment")
                binding.tvPaymentNotAvailable.visibility = View.VISIBLE
                binding.progressBarReceivePayment.visibility = View.GONE
                binding.llReceivePayment.visibility = View.VISIBLE //TODO gone
            }
            else{
                println("should calculate payment")
                binding.llReceivePayment.visibility = View.VISIBLE
                binding.progressBarReceivePayment.visibility = View.GONE
            }
        }
        paymentViewModel.loadingState.observe(viewLifecycleOwner){loading->
            if(loading){
                binding.llReceivePayment.visibility = View.VISIBLE //todo: gone
                binding.progressBarReceivePayment.visibility = View.VISIBLE
            }
            else{
                binding.llReceivePayment.visibility = View.VISIBLE //todo: gone
                binding.progressBarReceivePayment.visibility = View.GONE
            }
        }
    }

    private fun setupOnClickListeners(){
        binding.ivBtnBackFromReceivePayment.setOnClickListener {
            val intent = Intent(requireContext(),AppAdActivity::class.java)
            intent.putExtra("toAccountSettings",true)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.imageSliderCarPhotosPayment.setOnClickListener {
            val action = ReceivePaymentFragmentDirections.actionReceivePaymentFragmentToTakePhotoFragment2(
                photosToTakeList = arrayOf(getString(R.string.capture_odometer_photo)),
                fromWhere = "payment"
            )
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnSubmitPaymentRequestOrSavePaymentDetails.setOnClickListener {
            if(paymentViewModel.validatePaymentForm(binding.llReceivePayment) && hasPhotoTaken){
                val paymentRequest = PaymentRequest(
                    requestId = "pr_" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis(),
                    driverId = tripDataListInstance.driverId,
                    driverName = FirebaseAuth.getInstance().currentUser!!.displayName!!,
                    currentCampaignId = tripDataListInstance.campaignId,
                    currentCampaignApplicationId = tripDataListInstance.campaignApplicationId,
                    amount = calculatedAmount.toDouble(),
                    bankAccountNumber = binding.tfBankAccountNumber.text.toString(),
                    bankInstitutionNumber = binding.tfBankInstitutionNumber.text.toString(),
                    bankTransitNumber = binding.tfBankTransitNumber.text.toString(),
                    paymentFormFullName = binding.tfPaymentFullName.text.toString(),
                    imageUrls = listOf()
                )
                paymentViewModel.uploadOdometerPhotoAndPaymentRequest(listOf(),paymentRequest)
            }
            else if(!hasPhotoTaken){
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.odometer_photo))
            }
        }
    }



    private fun setupViews(){
        binding.llReceivePayment.visibility = View.VISIBLE //todo: gone //payment form is initially gone
    }





}