package com.project.drinkly_admin.ui.home.info

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentStoreDetailAvailableDrinksBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.adapter.AvailableDrinkAdapter
import com.project.drinkly_admin.util.ImageUtil
import com.project.drinkly_admin.util.MainUtil.fromDpToPx
import java.io.File

class StoreDetailAvailableDrinksFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailAvailableDrinksBinding
    lateinit var mainActivity: MainActivity

    var availableDrinksImage: MutableList<File>? = mutableListOf()
    var availableDrinksName: MutableList<String>? = mutableListOf()

    lateinit var availableDrinkAdapter: AvailableDrinkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailAvailableDrinksBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            buttonAdd.setOnClickListener {
                // 주류 추가
                val bottomSheet = StoreAvailableDrinksBottomSheetFragment()

                bottomSheet.setAvailableDrinkBottomSheetInterface(object : AvailableDrinkBottomSheetInterface {
                    override fun onAvailableDrinkClickCompleteButton(images: File?, names: String?) {
                        println("${images != null} & $names")
                        if (images != null && names != null) {
                            availableDrinksImage?.add(images)
                            availableDrinksName?.add(names)
                        }

                        checkComplete()
                    }
                })

                bottomSheet.show(
                    mainActivity.supportFragmentManager,
                    "AvailableDrinkBottomsheet"
                )
            }
            buttonAddSoju.setOnClickListener {
                // 소주 추가
                availableDrinksImage?.add(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_pass, "soju.png"))
                availableDrinksName?.add("소주")
                checkComplete()
            }
            buttonAddBeer.setOnClickListener {
                // 맥주 추가
                availableDrinksImage?.add(ImageUtil.copyRawToFile(mainActivity, R.drawable.img_complete, "beer.png"))
                availableDrinksName?.add("맥주")
                checkComplete()
            }

            buttonSave.setOnClickListener {

            }
        }

        return binding.root
    }

    fun checkComplete() {
        availableDrinkAdapter.updateList(availableDrinksImage, availableDrinksName)

        binding.run {
            if((availableDrinksImage?.size ?: 0) > 0 && (availableDrinksName?.size ?: 0) > 0 && (availableDrinksImage?.size == availableDrinksName?.size)) {
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
        availableDrinkAdapter =
            AvailableDrinkAdapter(mainActivity, availableDrinksImage, availableDrinksName).apply {
                itemClickListener = object : AvailableDrinkAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        availableDrinksImage?.removeAt(position)
                        availableDrinksName?.removeAt(position)

                        updateList(availableDrinksImage, availableDrinksName)
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