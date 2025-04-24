package com.project.drinkly_admin.ui.home.info

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentStoreDetailInfoBinding
import com.project.drinkly_admin.ui.MainActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class StoreDetailInfoFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailInfoBinding
    lateinit var mainActivity: MainActivity

    var isImageUpload = false
    var storeMainImage: File? = null

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
                        imageViewStoreImage.setImageURI(uri)
                        layoutStoreImage.visibility = View.INVISIBLE
                    }

                    storeMainImage = compressedFile
                } else {
                    Log.e("ImageCompression", "압축된 파일이 존재하지 않거나 비어 있습니다.")
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView.rootView) { _, insets ->
            val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            updateViewPositionForKeyboard(imeHeight - sysBarInsets.bottom)
            insets
        }

        binding.run {
            scrollView.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()
                false
            }

            layoutStoreImage.setOnClickListener {
                // 갤러리 오픈
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                checkComplete()
            }

            imageViewStoreImage.setOnClickListener {
                // 갤러리 오픈
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                checkComplete()
            }

            editTextStoreDescription.addTextChangedListener {
                checkComplete()
                if(editTextStoreDescription.text.isNotEmpty()) {
                    editTextStoreDescription.setBackgroundResource(R.drawable.background_edittext_radius10_filled)
                } else {
                    editTextStoreDescription.setBackgroundResource(R.drawable.background_edittext_radius10_default)
                }
            }

            editTextStoreInstagram.addTextChangedListener {
                if(editTextStoreInstagram.text.isNotEmpty()) {
                    editTextStoreInstagram.setBackgroundResource(R.drawable.background_edittext_radius10_filled)
                } else {
                    editTextStoreInstagram.setBackgroundResource(R.drawable.background_edittext_radius10_default)
                }
            }

            buttonSave.setOnClickListener {

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "매장 정보"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun checkComplete() {
        binding.run {
            buttonSave.isEnabled =
                storeMainImage != null && editTextStoreDescription.text.isNotEmpty()
        }
    }

    private fun updateViewPositionForKeyboard(keyboardHeight: Int) {
        val layoutParams =
            binding.scrollView.layoutParams as ConstraintLayout.LayoutParams
        if (keyboardHeight > 0) {
            layoutParams.bottomMargin = keyboardHeight
        } else {
            layoutParams.bottomMargin = 0
        }
        binding.scrollView.layoutParams = layoutParams
    }

    private fun convertResizeImage(imageUri: Uri): Uri {
        val contentResolver = requireContext().contentResolver
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        // 이미지 리사이즈 (절반 크기로)
        val resizedBitmap =
            Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        // 임시 파일 생성
        val tempFile = File.createTempFile("resized_image", ".jpg", requireContext().cacheDir)

        // 이미지 파일 쓰기
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)

            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(byteArrayOutputStream.toByteArray())
                outputStream.flush()
            }

        } catch (e: Exception) {
            Log.e("ImageResize", "Failed to write file: ${e.message}")
        } finally {
            // 메모리 해제
            resizedBitmap.recycle()
        }

        return Uri.fromFile(tempFile)
    }
}