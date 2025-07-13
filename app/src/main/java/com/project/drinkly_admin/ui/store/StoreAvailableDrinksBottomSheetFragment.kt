package com.project.drinkly_admin.ui.store

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.databinding.FragmentStoreAvailableDrinksBottomSheetBinding
import com.project.drinkly_admin.viewModel.StoreViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

interface AvailableDrinkBottomSheetInterface {
    fun onAvailableDrinkClickCompleteButton(images: File?, names: String?)
}

class StoreAvailableDrinksBottomSheetFragment() : DialogFragment() {
    lateinit var binding: FragmentStoreAvailableDrinksBottomSheetBinding
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    // 인터페이스 인스턴스
    private var listener: AvailableDrinkBottomSheetInterface? = null

    // 리스너 설정 메서드
    fun setAvailableDrinkBottomSheetInterface(listener: AvailableDrinkBottomSheetInterface) {
        this.listener = listener
    }

    var isImageUpload = false
    var availableDrinkImage: File? = null

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                isImageUpload = true
                Log.d("PhotoPicker", "Selected URI: $uri")

                // 이미지 처리 및 압축
                val resizedUri = convertResizeImage(uri) // 기존 이미지를 리사이징한 후 Uri 얻기
                val compressedFile = File(resizedUri.path!!)

                // 파일이 정상적으로 생성되었는지 확인
                if (compressedFile.exists() && compressedFile.length() > 0) {
                    binding.run {
                        imageViewAvaiableDrink.setImageURI(uri)
                        imageViewGallery.visibility = View.INVISIBLE
                    }

                    availableDrinkImage = compressedFile

                    checkComplete()
                } else {
                    Log.e("ImageCompression", "압축된 파일이 존재하지 않거나 비어 있습니다.")
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onStart() {
        super.onStart()

        // 다이얼로그를 하단에 배치하고 마진 설정
        dialog?.window?.let { window ->
            val params = window.attributes

            // 화면 너비에서 좌우 마진과 하단 마진 계산
            val marginHorizontalInPx =
                (20 * requireContext().resources.displayMetrics.density).toInt() // 좌우 마진 20dp
            val marginBottomInPx =
                (11 * requireContext().resources.displayMetrics.density).toInt() // 하단 마진 11dp

            params.width =
                requireContext().resources.displayMetrics.widthPixels - (marginHorizontalInPx * 2) // 좌우 마진 적용
            params.height = WindowManager.LayoutParams.WRAP_CONTENT // 세로는 콘텐츠 크기
            params.gravity = android.view.Gravity.BOTTOM // 하단에 배치
            params.y = marginBottomInPx // 하단 마진 적용

            window.attributes = params
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreAvailableDrinksBottomSheetBinding.inflate(inflater, container, false)

        // 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        observeViewModel()

        binding.run {
            imageViewAvaiableDrink.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                checkComplete()
            }

            editTextAvailableDrink.addTextChangedListener {
                checkComplete()
            }

            buttonAdd.setOnClickListener {
                listener?.onAvailableDrinkClickCompleteButton(availableDrinkImage, editTextAvailableDrink.text.toString())
                dismiss()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {

        }
    }

    fun checkComplete() {
        binding.run {
            if(editTextAvailableDrink.text.isNotEmpty()) {
                buttonAdd.isEnabled = true
            } else {
                buttonAdd.isEnabled = false
            }
        }
    }


    fun initView() {

    }

    private fun convertResizeImage(imageUri: Uri): Uri {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(imageUri) ?: return imageUri
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // 회전 보정
        val rotatedBitmap = rotateBitmapIfRequired(bitmap, imageUri)

        // 이미지 리사이즈 (절반 크기로)
        val resizedBitmap = Bitmap.createScaledBitmap(
            rotatedBitmap,
            rotatedBitmap.width / 2,
            rotatedBitmap.height / 2,
            true
        )

        // 임시 파일 생성
        val tempFile = File.createTempFile("resized_image", ".jpg", requireContext().cacheDir)

        try {
            val outputStream = FileOutputStream(tempFile)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("ImageResize", "Failed to write file: ${e.message}")
        } finally {
            resizedBitmap.recycle()
        }

        return Uri.fromFile(tempFile)
    }

    private fun rotateBitmapIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return bitmap
        val exif = ExifInterface(inputStream)

        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }

        inputStream.close()
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}