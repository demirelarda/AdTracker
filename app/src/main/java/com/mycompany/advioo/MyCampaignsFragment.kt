package com.mycompany.advioo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.advioo.databinding.FragmentMyCampaignsBinding


class MyCampaignsFragment : Fragment() {

    private var _binding: FragmentMyCampaignsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyCampaignsBinding.inflate(inflater, container, false)
        return binding.root
    }


}