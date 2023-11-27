package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApi
import com.google.android.material.snackbar.Snackbar

class ElectionsFragment : Fragment() {

    // TODO: Declare ViewModel
    // TODO: Add ViewModel values and create ViewModel
    private val viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory(
            CivicsApi.retrofitService,
            ElectionDatabase.getInstance(requireContext()).electionDao
        )
    }
    private lateinit var binding: FragmentElectionBinding
    private lateinit var upcomingElectionsAdapter: ElectionListAdapter
    private lateinit var savedElectionsAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    )
            : View? {
        // TODO: Add binding values
        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // TODO: Link elections to voter info
        // TODO: Initiate recycler adapters
        upcomingElectionsAdapter = ElectionListAdapter(ElectionListener {
            viewModel.displayVoterInfo(it)
        })
        savedElectionsAdapter = ElectionListAdapter(ElectionListener {
            viewModel.displayVoterInfo(it)
        })
        binding.rvSavedElections.adapter = savedElectionsAdapter
        binding.rvUpcomingElections.adapter = upcomingElectionsAdapter

        // TODO: Populate recycler adapters
        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        it.id,
                        it.division
                    )
                )
                viewModel.displayVoterInfoComplete()
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showSnackbar(error)
                viewModel.onReceivedErrorMessage()
            }
        }
        return binding.root
    }

    // TODO: Refresh adapters when fragment loads
    override fun onResume() {
        super.onResume()
        // Refresh the adapter by calling notifyDataSetChanged
        upcomingElectionsAdapter.notifyDataSetChanged()
        savedElectionsAdapter.notifyDataSetChanged()
        viewModel.getSavedElectionsFromDB()
    }

    private fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content), message, duration
        ).show()
    }
}