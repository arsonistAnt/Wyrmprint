package com.example.wyrmprint.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wyrmprint.databinding.FragBrowseLayoutBinding

class BrowseFragment : Fragment() {
    private lateinit var binding: FragBrowseLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragBrowseLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}