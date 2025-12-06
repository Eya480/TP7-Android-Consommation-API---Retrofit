package com.isetr.menufragapp.ui.ajout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.isetr.menufragapp.data.Etudiant
import com.isetr.menufragapp.data.repository.EtudiantRepository
import com.isetr.menufragapp.databinding.FragmentAjoutEtudiantBinding
import com.isetr.menufragapp.viewModel.EtudiantViewModel
import com.isetr.menufragapp.viewModel.EtudiantViewModelFactory

class AjoutEtudiantFragment : Fragment() {
    private var _binding: FragmentAjoutEtudiantBinding? = null
    private val binding get() = _binding!!
    private lateinit var etudiantViewModel: EtudiantViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjoutEtudiantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = EtudiantRepository()
        val viewModelFactory = EtudiantViewModelFactory(repository)
        etudiantViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(EtudiantViewModel::class.java)

        binding.btnAjouter.setOnClickListener {
            ajouterEtudiant()
        }

        observerAddResult()
    }

    private fun ajouterEtudiant() {
        val cin = binding.editTextCin.text.toString().trim()
        val nom = binding.editTextNom.text.toString().trim()
        val prenom = binding.editTextPrenom.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val classe = binding.editTextClasse.text.toString().trim()

        if (cin.isEmpty() || nom.isEmpty() || prenom.isEmpty() ||
            email.isEmpty() || classe.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", false)
            return
        }

        val nouvelEtudiant = Etudiant(
            cin = cin,
            nom = nom,
            prenom = prenom,
            mail = email,
            classe = classe
        )

        etudiantViewModel.addNewEtudiant(nouvelEtudiant)
    }

    private fun observerAddResult() {
        etudiantViewModel.addResult.observe(viewLifecycleOwner) { etudiant ->
            if (etudiant != null) {
                showMessage("Étudiant ajouté avec succès!", true)
                clearForm()
                findNavController().popBackStack()
            }
        }

        etudiantViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                showMessage(it, false)
            }
        }
    }

    private fun showMessage(message: String, isSuccess: Boolean) {
        binding.textMessage.text = message
        binding.textMessage.setTextColor(
            if (isSuccess) resources.getColor(android.R.color.holo_green_dark, null)
            else resources.getColor(android.R.color.holo_red_dark, null)
        )
        binding.textMessage.visibility = View.VISIBLE
    }

    private fun clearForm() {
        binding.editTextCin.text?.clear()
        binding.editTextNom.text?.clear()
        binding.editTextPrenom.text?.clear()
        binding.editTextEmail.text?.clear()
        binding.editTextClasse.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}