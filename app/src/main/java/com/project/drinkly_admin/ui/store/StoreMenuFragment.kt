package com.project.drinkly_admin.ui.store

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.api.request.image.NewImageUrl
import com.project.drinkly_admin.api.request.image.StoreImageRequest
import com.project.drinkly_admin.api.response.home.StoreImageInfo
import com.project.drinkly_admin.databinding.FragmentStoreMenuBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.adapter.MenuAdapter
import com.project.drinkly_admin.util.MainUtil.fromDpToPx
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.StoreViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class StoreMenuFragment : Fragment() {

    lateinit var binding: FragmentStoreMenuBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    var previousMenuImages: List<StoreImageInfo>? = mutableListOf()
    var newMenuImages: MutableList<File>? = mutableListOf()
    var removedMenuImages: MutableList<Int>? = mutableListOf()
    var images: MutableList<Any>? = mutableListOf()

    lateinit var menuAdapter: MenuAdapter


    // 여러 장 선택 가능한 Photo Picker 등록
    val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (!uris.isNullOrEmpty()) {
                for (uri in uris) {
                    Log.d("PhotoPicker", "Selected URI: $uri")

                    val resizedUri = convertResizeImage(uri)
                    val compressedFile = File(resizedUri.path!!)

                    if (compressedFile.exists() && compressedFile.length() > 0) {
                        newMenuImages?.add(compressedFile)
                        images?.add(compressedFile)
                    } else {
                        Log.e("ImageCompression", "압축된 파일이 존재하지 않거나 비어 있습니다.")
                    }
                }

                menuAdapter.updateList(images)
                println(images)
                checkComplete()
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreMenuBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            buttonSave.setOnClickListener {
                if(!newMenuImages.isNullOrEmpty()) {
                    viewModel.getPresignedUrlBatch(mainActivity, newMenuImages!!, "menu")
                } else {
                    var storeInfo =
                        StoreImageRequest(
                            type = "menu",
                            newImageUrls = emptyList(),
                            removeImageIds = removedMenuImages ?: emptyList()
                        )
                    viewModel.editStoreImage(mainActivity, MyApplication.storeId, storeInfo)
                }

                buttonSave.isEnabled = false
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
                textViewTitle.text = "메뉴판"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun initAdapter() {
        if(!viewModel.storeDetailInfo.value?.menuImageUrls.isNullOrEmpty()) {
            previousMenuImages = viewModel.storeDetailInfo.value?.menuImageUrls
            images = viewModel.storeDetailInfo.value?.menuImageUrls?.map { it.imageUrl }?.toMutableList()
        }

        menuAdapter =
            MenuAdapter(mainActivity, images).apply {
                deleteClickListener = object : MenuAdapter.OnItemDeleteClickListener {
                    override fun onDeleteClick(position: Int) {
                        // 이미지 삭제
                        if(images?.get(position -1) is String) {
                            var imageId = previousMenuImages?.find { it.imageUrl == images?.get(position -1) }?.imageId
                            if (imageId != null) {
                                removedMenuImages?.add(imageId)
                            }
                        } else if(images?.get(position -1) is File) {
                            (images?.get(position - 1) as? File)?.let { file ->
                                newMenuImages?.remove(file)
                            }
                        }
                        println(removedMenuImages)
                        images?.removeAt(position -1)
                        updateList(images)
                        checkComplete()
                    }
                }

                galleryClickListener = object : MenuAdapter.OnItemGalleryClickListener {
                    override fun onGalleryClick(position: Int) {
                        // 갤러리 오픈
                        // Launch the photo picker and let the user choose only images.
                        pickMultipleMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                }
            }

        binding.run {
            recyclerViewMenu.apply {
                adapter = menuAdapter
                layoutManager = GridLayoutManager(context, 2)

                addItemDecoration(
                    GridSpacingItemDecoration(spanCount = 2, spacing = 10f.fromDpToPx())
                )
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            presignedUrlBatch.observe(viewLifecycleOwner) { presignedList ->
                if (!presignedList.isNullOrEmpty()) {
                    val mappedNewImageUrls = presignedList.mapIndexed { index, newImageUrl ->
                        NewImageUrl(
                            imageUrl = newImageUrl.filePath,
                            description = "메뉴판${index + 1}"
                        )
                    }

                    MyApplication.storeId?.let { storeId ->
                        val storeInfo = StoreImageRequest(
                            type = "menu",
                            newImageUrls = mappedNewImageUrls,
                            removeImageIds = removedMenuImages ?: emptyList()
                        )

                        viewModel.editStoreImage(mainActivity, storeId, storeInfo)
                    } ?: run {
                        Log.e("StoreMenuFragment", "storeId is null")
                    }

                    // liveData 값 초기화
                    viewLifecycleOwner.lifecycleScope.launch {
                        presignedUrlBatch.postValue(null)
                        presignedUrl.postValue(null)
                    }
                }
            }

        }
    }

    fun checkComplete() {
        binding.run {
            if((images?.size ?: 0) > 0) {
                buttonSave.isEnabled = true
            } else {
                buttonSave.isEnabled = false
            }
        }
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


    internal class GridSpacingItemDecoration(
        private val spanCount: Int, // Grid의 column 수
        private val spacing: Int // 간격
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position: Int = parent.getChildAdapterPosition(view)

            if (position >= 0) {
                val column = position % spanCount // item column
                outRect.apply {
                    // spacing - column * ((1f / spanCount) * spacing)
                    left = spacing - column * spacing / spanCount
                    // (column + 1) * ((1f / spanCount) * spacing)
                    right = (column + 1) * spacing / spanCount
                    if (position < spanCount) top = spacing
                    bottom = spacing
                }
            } else {
                outRect.apply {
                    left = 0
                    right = 0
                    top = 0
                    bottom = 0
                }
            }
        }
    }

}