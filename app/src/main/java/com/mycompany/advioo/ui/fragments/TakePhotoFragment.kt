package com.mycompany.advioo.ui.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentTakePhotoBinding
import com.mycompany.advioo.util.SnackbarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.util.UUID


class TakePhotoFragment : Fragment() {

    private var _binding: FragmentTakePhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraManager : CameraManager
    private lateinit var textureView: TextureView
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var cameraDevice: CameraDevice
    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var capReq: CaptureRequest.Builder
    private lateinit var imageReader: ImageReader
    private var photosToTakeList: MutableList<String> = mutableListOf()
    private var sensorOrientation : Int = 0
    private var currentPhoto: Bitmap? = null
    private val photoList : MutableList<Bitmap> = mutableListOf()
    private var isLoading: Boolean = false
    private var fromPayment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTakePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancelPhoto.visibility = View.GONE
        binding.btnConfirmPhoto.visibility = View.GONE
        binding.ivCapturedPhoto.visibility = View.GONE
        val bundle = arguments
        if(bundle != null){
            val args = TakePhotoFragmentArgs.fromBundle(bundle)
            args.photosToTakeList?.forEach {
                photosToTakeList.add(it)
            }
            if(args.fromWhere != null){
                if(args.fromWhere == "payment"){
                    fromPayment = true
                }
            }
        }

        binding.tvPhotoToTake.text = photosToTakeList[0]


        textureView = binding.textureView
        cameraManager = requireActivity().getSystemService(android.content.Context.CAMERA_SERVICE) as CameraManager
        handlerThread = HandlerThread("cameraThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)


        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {

            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {

            }
        }

        imageReader = ImageReader.newInstance(1080,1920,ImageFormat.JPEG,3)
        imageReader.setOnImageAvailableListener({
            //TODO: GET THE IMAGE
            val image = imageReader.acquireLatestImage()
            if (image != null) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                image.close()
                currentPhoto = bitmap
                activity?.runOnUiThread {
                    showCapturedImage(bitmap)
                }

            }
            else{
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.something_went_wrong_camera))
            } },
            handler)

        setupOnClickListeners()
    }

    @SuppressLint("MissingPermission")
    private fun openCamera(){
        cameraManager.openCamera(cameraManager.cameraIdList[0],
            object: CameraDevice.StateCallback(){
                override fun onOpened(p0: CameraDevice) {
                    cameraDevice = p0
                    capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    val surface = Surface(textureView.surfaceTexture)
                    capReq.addTarget(surface)
                    val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraDevice.id)
                    sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

                    cameraDevice.createCaptureSession(listOf(surface,imageReader.surface),object: CameraCaptureSession.StateCallback(){
                        override fun onConfigured(p0: CameraCaptureSession) {
                            cameraCaptureSession = p0
                            cameraCaptureSession.setRepeatingRequest(capReq.build(),null,null)
                        }

                        override fun onConfigureFailed(p0: CameraCaptureSession) {

                        }

                    },handler)
                }

                override fun onDisconnected(p0: CameraDevice) {

                }

                override fun onError(p0: CameraDevice, p1: Int) {

                }

            },
            handler)
    }

    private fun setupOnClickListeners() {
        binding.btnCapturePhoto.setOnClickListener {
            capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            capReq.addTarget(imageReader.surface)
            cameraCaptureSession.capture(capReq.build(), null, null)


        }

        binding.btnConfirmPhoto.setOnClickListener {
            photosToTakeList.removeAt(0)

            if (photosToTakeList.isEmpty()) {
                println("END OF THE PHOTOS")
                if(currentPhoto!=null){
                    photoList.add(currentPhoto!!)
                }
                isLoading = true
                updateProgressBarVisibility()

                lifecycleScope.launch {
                    val savedFiles = saveBitmapsToPrivateStorage(requireContext(), photoList)
                    isLoading = false
                    updateProgressBarVisibility()
                    if(savedFiles.isNotEmpty()){
                        if(fromPayment){
                            navigateToReceivePaymentFragment(savedFiles)
                        }
                        else{
                            navigateToShowPhotoFragment(savedFiles)
                        }
                    }
                }
            } else {
                if(currentPhoto != null){
                    photoList.add(currentPhoto!!)
                }
                currentPhoto = null
                binding.tvPhotoToTake.text = photosToTakeList[0]
                resetCaptureState()
            }
        }



        binding.btnCancelPhoto.setOnClickListener {
            resetCaptureState()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraDevice.close()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }

    override fun onPause() {
        super.onPause()
        super.onDestroy()
        cameraDevice.close()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }

    private fun showConfirmationButtons() {
        binding.btnConfirmPhoto.visibility = View.VISIBLE
        binding.btnCancelPhoto.visibility = View.VISIBLE
        binding.btnCapturePhoto.visibility = View.GONE
        binding.centerView.visibility = View.VISIBLE
    }

    private fun resetCaptureState() {
        binding.btnConfirmPhoto.visibility = View.GONE
        binding.btnCancelPhoto.visibility = View.GONE
        binding.btnCapturePhoto.visibility = View.VISIBLE
        binding.centerView.visibility = View.GONE
        binding.ivCapturedPhoto.visibility = View.GONE
        startCameraPreview()
    }

    private fun startCameraPreview() {
        binding.textureView.visibility = View.VISIBLE
        binding.ivCapturedPhoto.visibility = View.GONE
        capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        val surface = Surface(textureView.surfaceTexture)
        capReq.addTarget(surface)
        cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
    }

    private fun showCapturedImage(bitmap: Bitmap){
        showConfirmationButtons()
        binding.textureView.visibility = View.GONE
        val imageView = binding.ivCapturedPhoto
        val rotatedBitmap = rotateBitmap(bitmap, sensorOrientation.toFloat())
        imageView.setImageBitmap(rotatedBitmap)
        imageView.visibility = View.VISIBLE
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private suspend fun saveBitmapsToPrivateStorage(context: Context, bitmapList: List<Bitmap>): List<String> {
        return withContext(Dispatchers.IO) {
            val fileNames = mutableListOf<String>()
            val rotatedBitmapList = rotateBitmapList(bitmapList,sensorOrientation.toFloat())
            for ((index, bitmap) in rotatedBitmapList.withIndex()) {
                val uuid = UUID.randomUUID()
                val filename = "${uuid}_car_image_$index.jpg"
                try {
                    val fileOutputStream: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)
                    fileOutputStream.close()
                    fileNames.add(filename)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            fileNames
        }
    }

    private fun rotateBitmapList(sourceBitmaps: List<Bitmap>, angle: Float): List<Bitmap> {
        val matrix = Matrix()
        matrix.postRotate(angle)

        return sourceBitmaps.map { sourceBitmap ->
            Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.width, sourceBitmap.height, matrix, true)
        }
    }


    private fun updateProgressBarVisibility() {
        if (isLoading) {
            binding.progressBarTakePhoto.visibility = View.VISIBLE
            binding.textureView.visibility = View.GONE
            binding.btnCapturePhoto.visibility = View.GONE
            binding.btnCancelPhoto.visibility = View.GONE
            binding.btnConfirmPhoto.visibility = View.GONE
            binding.tvPhotoToTake.visibility = View.GONE
        } else {
            binding.progressBarTakePhoto.visibility = View.GONE
            binding.textureView.visibility = View.VISIBLE
            binding.btnCapturePhoto.visibility = View.VISIBLE
            binding.btnCancelPhoto.visibility = View.VISIBLE
            binding.btnConfirmPhoto.visibility = View.VISIBLE
            binding.tvPhotoToTake.visibility = View.VISIBLE
        }
    }

    private fun navigateToShowPhotoFragment(photoList: List<String>){
        val action = TakePhotoFragmentDirections.actionTakePhotoFragment2ToShowPhotoFragment(photoList.toTypedArray())
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun navigateToReceivePaymentFragment(photoList: List<String>){
        val action = TakePhotoFragmentDirections.actionTakePhotoFragment2ToReceivePaymentFragment(photoList.toTypedArray())
        Navigation.findNavController(requireView()).navigate(action)
    }




}