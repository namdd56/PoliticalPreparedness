package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.material.snackbar.Snackbar

class VoterInfoFragment : Fragment() {

    // TODO: Add ViewModel values and create ViewModel
    private val viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao)
    }
    private lateinit var binding: FragmentVoterInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    )
            : View? {
        // TODO: Add binding values
        binding = FragmentVoterInfoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.voterInfoViewModel = viewModel

        // TODO: Populate voter info -- hide views without provided data.
        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        viewModel.populateVoterInfo(electionId, division)
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */

        // TODO: Handle loading of URLs
        viewModel.url.observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                loadUrl(it)
            }
        })
        // TODO: Handle save button UI state
        binding.btnSave.setOnClickListener { viewModel.saveOrRemoveElection() }

        // TODO: cont'd Handle save button clicks
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    error,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.onReceivedErrorMessage()
            }
        }

        return binding.root
    }

    // TODO: Create method to load URL intents
    private fun loadUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}