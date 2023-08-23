package com.petmily.presentation.view.store

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentPointLogBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.ShopViewModel

class PointLogFragment :
    BaseFragment<FragmentPointLogBinding>(FragmentPointLogBinding::bind, R.layout.fragment_point_log) {

    private val mainActivity by lazy {
        context as MainActivity
    }
    private val mainViewModel: MainViewModel by activityViewModels()
    private val shopViewModel: ShopViewModel by activityViewModels()
    private lateinit var pointLogAdapter: PointLogAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviInVisible()
        initApi()
        initAdapter()
        initObserve()
        initButton()
    }

    private fun initApi() {
        shopViewModel.requestPointLog()
    }

    private fun initObserve() = with(shopViewModel) {
        resultPointLog.observe(viewLifecycleOwner) {
            pointLogAdapter.apply {
                setPointLogList(it)
                notifyDataSetChanged()
            }
        }
    }

    private fun initButton() = with(binding) {
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initAdapter() = with(binding) {
        pointLogAdapter = PointLogAdapter(mainActivity)

        rcvPointLog.apply {
            adapter = pointLogAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }
}
