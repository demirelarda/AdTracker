package com.mycompany.advioo.ui.fragments.auth.city

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.StateListAdapter
import com.mycompany.advioo.databinding.FragmentStateListBinding
import com.mycompany.advioo.util.Status
import com.mycompany.advioo.viewmodels.CityStateViewModel
import javax.inject.Inject


class StateListFragment @Inject constructor(
    val stateListAdapter : StateListAdapter
) : Fragment(R.layout.fragment_state_list) {




    lateinit var viewModel : CityStateViewModel
    private var fragmentBinding : FragmentStateListBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("created")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentStateListBinding.bind(view)
        fragmentBinding = binding
        viewModel = ViewModelProvider(requireActivity()).get(CityStateViewModel::class.java)
        subscribeToObservers()
        binding.rvStateList.adapter = stateListAdapter
        binding.rvStateList.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getStates()
        stateListAdapter.setOnItemClickListener {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_state_list, container, false)

    }

    private fun subscribeToObservers(){
        println("entered observer")
        viewModel.stateList.observe(viewLifecycleOwner, Observer{
            when(it.status){
                Status.SUCCESS->{
                    println("entered status success")
                    val provinces = it.data!!.provinces
                    stateListAdapter.states = provinces
                }
                Status.ERROR->{
                    //error
                    println("msg="+it.message)
                    println("error")
                }
                Status.LOADING->{
                    //loading
                    println("loading")
                }
            }
        })
    }



}