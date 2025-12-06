package com.isetr.menufragapp.ui.liste

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.isetr.menufragapp.EtudiantApplication
import com.isetr.menufragapp.R
import com.isetr.menufragapp.databinding.FragmentListeEtudiantsBinding
import com.isetr.menufragapp.viewModel.EtudiantViewModel

class ListeEtudiantsFragment : Fragment() {

    private lateinit var binding: FragmentListeEtudiantsBinding
    private lateinit var etudiantViewModel: EtudiantViewModel
    private lateinit var etudiantAdapter: EtudiantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListeEtudiantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSwipeToRefresh()
        observeLiveData()

        if (etudiantViewModel.etudiants.value.isNullOrEmpty()) {
            showLoadingState()
            etudiantViewModel.getAllEtudiants()
        }
    }

    private fun setupViewModel() {
        val application = requireActivity().application as EtudiantApplication
        etudiantViewModel = ViewModelProvider(
            requireActivity(),
            application.viewModelFactory
        ).get(EtudiantViewModel::class.java)
    }

    private fun setupRecyclerView() {
        etudiantAdapter = EtudiantAdapter(mutableListOf()) { etudiant ->
            navigateToDetails(etudiant.cin)
        }

        binding.recyclerViewEtudiants.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = etudiantAdapter
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            etudiantViewModel.getAllEtudiants()
        }
    }

    private fun navigateToDetails(cin: String) {
        try {
            val bundle = Bundle().apply {
                putString("cin", cin)
            }
            findNavController().navigate(
                R.id.detailsEtudiantFragment,
                bundle
            )
            Log.d("Navigation", "Navigation vers détails avec CIN: $cin")
        } catch (e: Exception) {
            Log.e("Navigation", "Erreur navigation: ${e.message}")
        }
    }

    private fun observeLiveData() {
        etudiantViewModel.etudiants.observe(viewLifecycleOwner, Observer { etudiants ->
            Log.d("ListeFragment", "Nombre d'étudiants reçus: ${etudiants.size}")
            etudiantAdapter.updateList(etudiants)
            binding.swipeRefreshLayout.isRefreshing = false

            if (etudiants.isNotEmpty()) {
                showDataState()
            } else if (etudiantViewModel.errorMessage.value == null) {
                showEmptyState("Aucun étudiant trouvé.")
            }
        })

        etudiantViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMsg ->
            errorMsg?.let {
                showErrorState(it)
                binding.swipeRefreshLayout.isRefreshing = false
                Log.e("ListeFragment", "Erreur: $it")
            }
        })
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.textError.visibility = View.GONE
        binding.recyclerViewEtudiants.visibility = View.GONE
        binding.textError.text = "Chargement..."
    }

    private fun showDataState() {
        binding.progressBar.visibility = View.GONE
        binding.textError.visibility = View.GONE
        binding.recyclerViewEtudiants.visibility = View.VISIBLE
    }

    private fun showEmptyState(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.textError.text = message
        binding.textError.visibility = View.VISIBLE
        binding.recyclerViewEtudiants.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.textError.text = errorMessage
        binding.textError.visibility = View.VISIBLE
        binding.recyclerViewEtudiants.visibility = View.GONE
    }
}