package com.mycompany.advioo.ui.fragments.auth.city

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.CityListAdapter
import com.mycompany.advioo.databinding.FragmentCityListBinding
import com.mycompany.advioo.models.city.Province
import com.mycompany.advioo.viewmodels.CityStateViewModel
import javax.inject.Inject


class CityListFragment @Inject constructor(
    val cityListAdapter : CityListAdapter
) : Fragment(R.layout.fragment_city_list) {

    private lateinit var provinceObject : Province
    private lateinit var viewModel : CityStateViewModel
    private var fragmentBinding : FragmentCityListBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        val binding = FragmentCityListBinding.bind(view)
        fragmentBinding = binding
        viewModel = ViewModelProvider(requireActivity()).get(CityStateViewModel::class.java)
        binding.rvCityList.adapter = cityListAdapter
        binding.rvCityList.layoutManager = LinearLayoutManager(requireContext())
        if(bundle!=null){
            val args = CityListFragmentArgs.fromBundle(bundle)
            provinceObject = args.selectedProvinceObject
            cityListAdapter.cities = provinceObject.cities
        }

        cityListAdapter.setOnItemClickListener {
            val selectedLocation : String = viewModel.selectedUserState.value!!+","+it
            println("selected location = "+selectedLocation)
            val selectedLocationArray = arrayOf(viewModel.selectedUserState.value!!,it)
            val action = CityListFragmentDirections.actionCityListFragmentToRegisterAddressDetailsFragment(selectedLocationArray)
            Navigation.findNavController(requireView()).navigate(action)
        }

        fragmentBinding?.etSearchCity?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterCities(s.toString())
            }
        })

        binding.ivBtnBackFromCity.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun filterCities(query: String) {
        val filteredCities = provinceObject.cities.filter { city ->
            city.contains(query, ignoreCase = true)
        }
        cityListAdapter.cities = filteredCities
    }


}