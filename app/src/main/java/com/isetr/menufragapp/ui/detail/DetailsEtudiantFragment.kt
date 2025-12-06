package com.isetr.menufragapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.isetr.menufragapp.EtudiantApplication
import com.isetr.menufragapp.data.Etudiant
import com.isetr.menufragapp.databinding.FragmentDetailsEtudiantBinding
import com.isetr.menufragapp.viewModel.EtudiantViewModel

class DetailsEtudiantFragment : Fragment() {
    private var _binding: FragmentDetailsEtudiantBinding? = null
    private val binding get() = _binding!!
    private lateinit var etudiantViewModel: EtudiantViewModel
    private var currentEtudiant: Etudiant? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsEtudiantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        val cin = arguments?.getString("cin") ?: ""

        if (cin.isNotEmpty()) {
            etudiantViewModel.getEtudiantByCin(cin)
        } else {
            showMessage("CIN non fourni", false)
        }

        setupObservers()
        setupClickListeners()
    }

    // AJOUTER cette méthode
    private fun setupViewModel() {
        val application = requireActivity().application as EtudiantApplication
        etudiantViewModel = ViewModelProvider(
            this, // Utiliser 'this' au lieu de requireActivity()
            application.viewModelFactory
        ).get(EtudiantViewModel::class.java)
    }

    private fun setupObservers() {
        // Observer l'étudiant sélectionné
        etudiantViewModel.selectedEtudiant.observe(viewLifecycleOwner) { etudiant ->
            etudiant?.let {
                currentEtudiant = it
                populateForm(it)
                binding.textError.visibility = View.GONE
            }
        }

        // Observer les messages d'erreur
        etudiantViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                showMessage(it, false)
                binding.textError.text = it
                binding.textError.visibility = View.VISIBLE
            }
        }

        // Observer le résultat de la mise à jour
        etudiantViewModel.updateResult.observe(viewLifecycleOwner) { etudiant ->
            if (etudiant != null) {
                showMessage("Étudiant mis à jour avec succès!", true)
                currentEtudiant = etudiant
            }
        }

        // Observer le résultat de la suppression
        etudiantViewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showMessage("Étudiant supprimé avec succès!", true)
                // Naviguer vers la liste après un délai
                view?.postDelayed({
                    findNavController().popBackStack()
                }, 1500)
            }
        }

        // Observer le succès des opérations
        etudiantViewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                // Nettoyer après une opération réussie
                view?.postDelayed({
                    etudiantViewModel.clearOperationSuccess()
                }, 2000)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnUpdate.setOnClickListener {
            updateEtudiant()
        }

        binding.btnDelete.setOnClickListener {
            currentEtudiant?.let { etudiant ->
                etudiantViewModel.deleteEtudiant(etudiant.cin)
            }
        }
    }

    private fun populateForm(etudiant: Etudiant) {
        binding.editTextCin.setText(etudiant.cin)
        binding.editTextNom.setText(etudiant.nom)
        binding.editTextPrenom.setText(etudiant.prenom)
        binding.editTextEmail.setText(etudiant.mail)
        binding.editTextClasse.setText(etudiant.classe)
    }

    private fun updateEtudiant() {
        val cin = binding.editTextCin.text.toString().trim()
        val nom = binding.editTextNom.text.toString().trim()
        val prenom = binding.editTextPrenom.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val classe = binding.editTextClasse.text.toString().trim()

        if (nom.isEmpty() || prenom.isEmpty() ||
            email.isEmpty() || classe.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", false)
            return
        }

        val updatedEtudiant = Etudiant(
            cin = cin,
            nom = nom,
            prenom = prenom,
            mail = email,
            classe = classe
        )

        etudiantViewModel.updateEtudiant(updatedEtudiant)
    }

    private fun showMessage(message: String, isSuccess: Boolean) {
        binding.textMessage.text = message
        binding.textMessage.setTextColor(
            if (isSuccess) resources.getColor(android.R.color.holo_green_dark, null)
            else resources.getColor(android.R.color.holo_red_dark, null)
        )
        binding.textMessage.visibility = View.VISIBLE

        view?.postDelayed({
            binding.textMessage.visibility = View.GONE
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        etudiantViewModel.clearSelectedEtudiant()
        _binding = null
    }
}