package com.project.drinkly_admin.ui.store

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.mutableLongSetOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.request.image.CommonImageData
import com.project.drinkly_admin.api.request.image.ImageData
import com.project.drinkly_admin.api.request.image.NewImageUrl
import com.project.drinkly_admin.api.request.image.StoreImageRequest
import com.project.drinkly_admin.api.response.home.StoreImageInfo
import com.project.drinkly_admin.databinding.FragmentStoreDetailAvailableDrinksBinding
import com.project.drinkly_admin.ui.BasicDialogInterface
import com.project.drinkly_admin.ui.DialogBasic
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.adapter.AvailableDrinkAdapter
import com.project.drinkly_admin.util.ImageUtil
import com.project.drinkly_admin.util.MainUtil.fromDpToPx
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.StoreViewModel
import kotlinx.coroutines.launch
import java.io.File

class StoreDetailAvailableDrinksFragment : Fragment() {

    companion object {
        val SOJU_IMAGE_URL = "공통주류/20250507034147-40441a71-23e6-4166-8965-ce331aab5998-soju"
        val BEER_IMAGE_URL =  "공통주류/20250507034229-20802afc-98f4-44ce-aec7-12a559a96668-beer"
        val DRAFT_IMAGE_URL = "공통주류/20250617200637-a0444443-5965-418e-9802-19cca40de2c8-draft"
        val HIGHBALL_IMAGE_URL = "공통주류/20250617200742-72659ec0-e6ea-498e-baaf-197f4997f833-highball"
        val BASIC_IMAGE_URL = "공통주류/20250618155356-8e22ebc7-72a0-4c19-b5e5-7a2c4c880c68-basic"
    }

    lateinit var binding: FragmentStoreDetailAvailableDrinksBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    var previousAvailableDrinkImages: List<StoreImageInfo>? = mutableListOf()
    var newAvailableDrinkImages: MutableList<ImageData>? = mutableListOf()
    var newCommonAvailableDrinkImages: MutableList<CommonImageData>? = mutableListOf()
    var removedAvailableDrinkImages: MutableList<Int>? = mutableListOf()
    var images: MutableList<ImageData>? = mutableListOf()

    var newSojuIndex: Int = 0
    var newBeerIndex: Int = 0
    var newDraftBeerIndex: Int = 0
    var newHighballIndex: Int = 0

    lateinit var availableDrinkAdapter: AvailableDrinkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailAvailableDrinksBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            buttonAdd.setOnClickListener {
                // 주류 추가
                val bottomSheet = StoreAvailableDrinksBottomSheetFragment()

                bottomSheet.setAvailableDrinkBottomSheetInterface(object :
                    AvailableDrinkBottomSheetInterface {
                    override fun onAvailableDrinkClickCompleteButton(imageFile: File?, name: String?) {
                        if(checkDrinks(name.toString())) {
                            if (imageFile != null && name != null) {
                                images?.add(ImageData(imageFile, name))
                                newAvailableDrinkImages?.add(ImageData(imageFile, name))
                            } else if (imageFile== null) {
                                images?.add(ImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_basic, "basic.png"), name.toString()))
                                newCommonAvailableDrinkImages?.add(CommonImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_basic, "basic.png"), "basic", name.toString()))
                            }

                            checkComplete()
                        }
                    }
                })

                bottomSheet.show(
                    mainActivity.supportFragmentManager,
                    "AvailableDrinkBottomsheet"
                )
            }
            buttonAddSoju.setOnClickListener {
                // 소주 추가
                if(checkDrinks("소주")) {
                    images?.add(ImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_soju, "soju.png"), "소주"))
                    newCommonAvailableDrinkImages?.add(CommonImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_soju, "soju.png"), "soju", "소주"))
                    newSojuIndex = newAvailableDrinkImages?.size ?: 0

                    checkComplete()
                }
            }
            buttonAddBeer.setOnClickListener {
                // 병맥주 추가
                if(checkDrinks("병맥주")) {
                    images?.add(ImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_beer, "beer.png"), "병맥주"))
                    newCommonAvailableDrinkImages?.add(CommonImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_beer, "beer.png"), "beer", "병맥주"))
                    newBeerIndex = newAvailableDrinkImages?.size ?: 0

                    checkComplete()
                }
            }
            buttonAddDraftbeer.setOnClickListener {
                // 생맥주 추가
                if(checkDrinks("생맥주")) {
                    images?.add(ImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_draft_beer, "draft_beer.png"), "생맥주"))
                    newCommonAvailableDrinkImages?.add(CommonImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_draft_beer, "draft_beer.png"), "draft-beer", "생맥주"))
                    newDraftBeerIndex = newAvailableDrinkImages?.size ?: 0

                    checkComplete()
                }
            }
            buttonAddHighball.setOnClickListener {
                // 하이볼 추가
                if(checkDrinks("하이볼")) {
                    images?.add(ImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_highball, "highball.png"), "하이볼"))
                    newCommonAvailableDrinkImages?.add(CommonImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_highball, "highball.png"), "highball", "하이볼"))
                    newHighballIndex = newAvailableDrinkImages?.size ?: 0

                    checkComplete()
                }
            }

            buttonSave.setOnClickListener {
                if(!newAvailableDrinkImages.isNullOrEmpty()) {
                    viewModel.getPresignedUrlBatch(mainActivity, newAvailableDrinkImages?.map { it.image }, "availableDrinks")
                } else if(!newCommonAvailableDrinkImages.isNullOrEmpty()) {
                    // 기본 주류가 추가되어 있는 경우
                    val newImageUrls = mutableListOf<NewImageUrl>()

                    newCommonAvailableDrinkImages?.forEach { imageData ->
                        when (imageData.type) {
                            "soju" -> {
                                newImageUrls.add(
                                    NewImageUrl(
                                        imageUrl = SOJU_IMAGE_URL,
                                        description = "소주"
                                    )
                                )
                            }
                            "beer" -> {
                                newImageUrls.add(
                                    NewImageUrl(
                                        imageUrl = BEER_IMAGE_URL,
                                        description = "병맥주"
                                    )
                                )
                            }
                            "draft-beer" -> {
                                newImageUrls.add(
                                    NewImageUrl(
                                        imageUrl = DRAFT_IMAGE_URL,
                                        description = "생맥주"
                                    )
                                )
                            }
                            "highball" -> {
                                newImageUrls.add(
                                    NewImageUrl(
                                        imageUrl = HIGHBALL_IMAGE_URL,
                                        description = "하이볼"
                                    )
                                )
                            }
                            "basic" -> {
                                newImageUrls.add(
                                    NewImageUrl(
                                        imageUrl = BASIC_IMAGE_URL,
                                        description = imageData.description
                                    )
                                )
                            }
                        }
                    }

                    var storeInfo =
                        StoreImageRequest(
                            type = "availableDrinks",
                            newImageUrls = newImageUrls ?: emptyList<NewImageUrl>(),
                            removeImageIds = removedAvailableDrinkImages?: emptyList()
                        )
                    viewModel.editStoreImage(mainActivity, MyApplication.storeId, storeInfo)
                } else {
                    var storeInfo =
                        StoreImageRequest(
                            type = "availableDrinks",
                            newImageUrls = emptyList(),
                            removeImageIds = removedAvailableDrinkImages ?: emptyList()
                        )
                    viewModel.editStoreImage(mainActivity, MyApplication.storeId, storeInfo)
                }

                buttonSave.isEnabled = false
            }
        }

        return binding.root
    }

    fun checkComplete() {
        availableDrinkAdapter.updateList(images)

        binding.run {
            if((newAvailableDrinkImages?.size ?: 0) > 0 || (newCommonAvailableDrinkImages?.size ?: 0) > 0 || (removedAvailableDrinkImages?.size ?: 0) > 0) {
                buttonSave.isEnabled = true
            } else {
                buttonSave.isEnabled = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        viewModel.storeDetailInfo.value?.availableDrinkImageUrls?.let { availableDrinkList ->
            if (availableDrinkList.isNotEmpty()) {
                previousAvailableDrinkImages = availableDrinkList
                images?.clear()
                images?.addAll(
                    availableDrinkList.map { item ->
                        ImageData(image = item.imageUrl, description = item.description)
                    }
                )
            }
        }

        availableDrinkAdapter =
            AvailableDrinkAdapter(mainActivity, images).apply {
                itemClickListener = object : AvailableDrinkAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val targetImage = images?.get(position)

                        if (targetImage?.image is String) {
                            val imageId = previousAvailableDrinkImages?.find { it.imageUrl == targetImage.image && it.description == targetImage.description }?.imageId
                            if (imageId != null) {
                                removedAvailableDrinkImages?.add(imageId)
                            }

                        } else if (targetImage?.image is File) {
                            val file = targetImage.image as File

                            val indexInNew = newAvailableDrinkImages?.indexOfFirst {
                                it.image == file && it.description == targetImage.description
                            } ?: -1

                            if (indexInNew >= 0) {
                                // 인덱스 조정
                                if (indexInNew < newSojuIndex) newSojuIndex--
                                if (indexInNew < newBeerIndex) newBeerIndex--
                                if (indexInNew < newDraftBeerIndex) newDraftBeerIndex--
                                if (indexInNew < newHighballIndex) newHighballIndex--

                                newAvailableDrinkImages?.removeAt(indexInNew)
                            } else {
                                val indexInCommon = newCommonAvailableDrinkImages?.indexOfFirst {
                                    it.image == file && it.description == targetImage.description
                                } ?: -1

                                if (indexInCommon >= 0) {
                                    newCommonAvailableDrinkImages?.removeAt(indexInCommon)

                                    when (targetImage.description) {
                                        "소주" -> newSojuIndex = 0
                                        "병맥주" -> newBeerIndex = 0
                                        "생맥주" -> newDraftBeerIndex = 0
                                        "하이볼" -> newHighballIndex = 0
                                    }
                                }
                            }
                        }

                        images?.removeAt(position)
                        checkComplete()
                    }
                }
            }

        binding.run {
            recyclerViewAvailableDrink.apply {
                adapter = availableDrinkAdapter
                layoutManager = GridLayoutManager(context, 2)
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
                            description = newAvailableDrinkImages?.get(index)?.description ?: ""
                        )
                    }.toMutableList()

                    // 기본 이미지로 추가한 주류들 삽입
                    newCommonAvailableDrinkImages?.forEach { imageData ->
                        val insertIndex = when (imageData.description) {
                            "소주" -> newSojuIndex
                            "병맥주" -> newBeerIndex
                            "생맥주" -> newDraftBeerIndex
                            "하이볼" -> newHighballIndex
                            else -> {
                                // description에 해당하는 항목을 newAvailableDrinkImages에서 찾아 index 추정
                                newAvailableDrinkImages?.indexOfFirst { it.description == imageData.description }
                                    ?.takeIf { it >= 0 } ?: mappedNewImageUrls.size
                            }
                        }

                        val imageUrl = when (imageData.type) {
                            "soju" -> SOJU_IMAGE_URL
                            "beer" -> BEER_IMAGE_URL
                            "draft-beer" -> DRAFT_IMAGE_URL
                            "highball" -> HIGHBALL_IMAGE_URL
                            "basic" -> BASIC_IMAGE_URL
                            else -> BASIC_IMAGE_URL
                        }

                        val clampedIndex = insertIndex.coerceIn(0, mappedNewImageUrls.size)

                        mappedNewImageUrls.add(
                            clampedIndex,
                            NewImageUrl(
                                imageUrl = imageUrl,
                                description = imageData.description
                            )
                        )

                        if (clampedIndex <= newSojuIndex) newSojuIndex++
                        if (clampedIndex <= newBeerIndex) newBeerIndex++
                        if (clampedIndex <= newDraftBeerIndex) newDraftBeerIndex++
                        if (clampedIndex <= newHighballIndex) newHighballIndex++
                    }

                    MyApplication.storeId?.let { storeId ->
                        val storeInfo = StoreImageRequest(
                            type = "availableDrinks",
                            newImageUrls = mappedNewImageUrls,
                            removeImageIds = removedAvailableDrinkImages ?: emptyList()
                        )

                        viewModel.editStoreImage(mainActivity, storeId, storeInfo)
                    } ?: run {
                        Log.e("StoreMenuFragment", "storeId is null")
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        presignedUrlBatch.postValue(null)
                        presignedUrl.postValue(null)
                    }
                }
            }
        }
    }

    fun checkDrinks(description: String) : Boolean {
        if(images?.any { it.description == (description) } == true) {
            val dialog = DialogBasic("이미 등록되어 있는 주류에요\n같은 이름으로는 추가가 불가능해요")

            dialog.setBasicDialogInterface(object : BasicDialogInterface {
                override fun onClickYesButton() {

                }
            })

            dialog.show(mainActivity.supportFragmentManager, "DialogStoreAvailableDrink")
        }

        return images?.any { it.description == (description) } == false
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "멤버십 제공 주류"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }

        checkComplete()
    }
}