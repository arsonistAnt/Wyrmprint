package com.example.wyrmprint.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wyrmprint.databinding.FragFavoriteLayoutBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.viewmodels.FavoriteViewModel

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragFavoriteLayoutBinding
    private val viewModel: FavoriteViewModel by viewModel {
        injector.favoriteViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragFavoriteLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
}