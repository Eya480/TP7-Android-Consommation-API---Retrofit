package com.isetr.menufragapp.ui.liste

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isetr.menufragapp.data.Etudiant
import com.isetr.menufragapp.databinding.ItemEtudiantBinding

class EtudiantAdapter(
    private var etudiants: List<Etudiant> = emptyList(),
    private val onItemClicked: (Etudiant) -> Unit = {}
) : RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder>() {

    class EtudiantViewHolder(private val binding: ItemEtudiantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(etudiant: Etudiant, onItemClicked: (Etudiant) -> Unit) {
            binding.textViewNom.text = "${etudiant.nom} ${etudiant.prenom}"
            binding.textViewCin.text = "CIN: ${etudiant.cin}"
            binding.textViewMail.text = "Email: ${etudiant.mail ?: ""}"
            binding.textViewClasse.text = "Classe: ${etudiant.classe ?: ""}"

            binding.root.setOnClickListener {
                onItemClicked(etudiant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtudiantViewHolder {
        val binding = ItemEtudiantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EtudiantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EtudiantViewHolder, position: Int) {
        val etudiant = etudiants[position]
        holder.bind(etudiant, onItemClicked)
    }

    override fun getItemCount(): Int = etudiants.size

    fun updateList(newList: List<Etudiant>) {
        etudiants = newList.toList()
        notifyDataSetChanged()
    }
}