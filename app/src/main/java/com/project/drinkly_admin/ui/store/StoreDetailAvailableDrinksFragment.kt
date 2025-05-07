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

    lateinit var binding: FragmentStoreDetailAvailableDrinksBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    var previousAvailableDrinkImages: List<StoreImageInfo>? = mutableListOf()
    var newAvailableDrinkImages: MutableList<ImageData>? = mutableListOf()
    var newCommonAvailableDrinkImages: MutableList<ImageData>? = mutableListOf()
    var removedAvailableDrinkImages: MutableList<Int>? = mutableListOf()
    var images: MutableList<ImageData>? = mutableListOf()

    var newSojuIndex: Int = 0
    var newBeerIndex: Int = 0

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
                    newCommonAvailableDrinkImages?.add(ImageData(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_soju, "soju.png"), "소주"))
                    newSojuIndex = newAvailableDrinkImages?.size ?: 0

                    checkComplete()
                }
            }
            buttonAddBeer.setOnClickListener {
                // 맥주 추가
                if(checkDrinks("맥주")) {
                    images?.add(
                        ImageData(
                            ImageUtil.copyRawToFile(
                                mainActivity,
                                R.drawable.img_beer,
                                "beer.png"
                            ), "맥주"
                        )
                    )
                    newCommonAvailableDrinkImages?.add(
                        ImageData(
                            ImageUtil.copyRawToFile(
                                mainActivity,
                                R.drawable.img_beer,
                                "beer.png"
                            ), "맥주"
                        )
                    )
                    newBeerIndex = newAvailableDrinkImages?.size ?: 0

                    checkComplete()
                }
            }

            buttonSave.setOnClickListener {
                if(!newAvailableDrinkImages.isNullOrEmpty()) {
                    viewModel.getPresignedUrlBatch(mainActivity, newAvailableDrinkImages?.map { it.image })
                } else if(!newCommonAvailableDrinkImages.isNullOrEmpty()) {
                    // 소주가 추가되어 있는 경우
                    val newImageUrls = mutableListOf<NewImageUrl>()

                    newCommonAvailableDrinkImages?.forEach { imageData ->
                        when (imageData.description) {
                            "소주" -> {
                                newImageUrls.add(
                                    NewImageUrl(
                                        imageUrl = "공통주류/20250507034147-40441a71-23e6-4166-8965-ce331aab5998-soju",
                                        description = "소주"
                                    )
                                )
                            }
                            "맥주" -> {
                                newImageUrls.add(
                                    NewImageUrl(
                                        imageUrl = "공통주류/20250507034229-20802afc-98f4-44ce-aec7-12a559a96668-beer",
                                        description = "맥주"
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

                        if(images?.get(position)?.image is String) {
                            var imageId = previousAvailableDrinkImages?.find { it.imageUrl == images?.get(position)?.image }?.imageId
                            if (imageId != null) {
                                removedAvailableDrinkImages?.add(imageId)
                            }
                        } else if(images?.get(position)?.image is File) {
                            (images?.get(position))?.let { file ->
                                if((newAvailableDrinkImages?.indexOf(file) ?: 0) < newSojuIndex) {
                                    newSojuIndex -= 1
                                }
                                if((newAvailableDrinkImages?.indexOf(file) ?: 0) < newBeerIndex) {
                                    newBeerIndex -= 1
                                }
                                newAvailableDrinkImages?.remove(file)
                            }
                        }
                        println(removedAvailableDrinkImages)
                        images?.removeAt(position)

                        checkComplete()
                    }
                }
            }

        binding.run {
            recyclerViewAvailableDrink.apply {
                adapter = availableDrinkAdapter
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
                            description = "${newAvailableDrinkImages?.get(index)?.description}"
                        )
                    }.toMutableList() // ✨ 중간 삽입 위해 mutableList로 변환

                    // 소주가 추가되어 있는 경우
                    if (newCommonAvailableDrinkImages?.any { it.description == "소주" } == true) {
                        mappedNewImageUrls.add(
                            newSojuIndex,
                            NewImageUrl(
                                imageUrl = "공통주류/20250502162032-11f10447-1597-48b6-9ec8-28e0c37ab3ba-soju",
                                description = "소주"
                            )
                        )

                        // ⚠️ 맥주 삽입 시 소주 이후일 경우 인덱스 +1 보정 필요
                        newBeerIndex = if (newSojuIndex <= newBeerIndex) newBeerIndex + 1 else newBeerIndex
                    }

                    // 맥주가 추가되어 있는 경우
                    if (newCommonAvailableDrinkImages?.any { it.description == "맥주" } == true) {
                        mappedNewImageUrls.add(
                            newBeerIndex,
                            NewImageUrl(
                                imageUrl = "공통주류/20250502162032-b7a26511-55a9-4811-92a2-1ff564a34449-beer",
                                description = "맥주"
                            )
                        )
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

                    // liveData 값 초기화
                    viewLifecycleOwner.lifecycleScope.launch {
                        presignedUrlBatch.postValue(null)
                        presignedUrl.postValue(null)
                    }
                }
            }

        }
    }

    fun checkDrinks(description: String) : Boolean {
        if(images?.any { it.description.contains(description) } == true) {
            val dialog = DialogBasic("이미 등록되어 있는 주류에요\n같은 이름으로는 추가가 불가능해요")

            dialog.setBasicDialogInterface(object : BasicDialogInterface {
                override fun onClickYesButton() {

                }
            })

            dialog.show(mainActivity.supportFragmentManager, "DialogStoreAvailableDrink")
        }

        return images?.any { it.description.contains(description) } == false
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