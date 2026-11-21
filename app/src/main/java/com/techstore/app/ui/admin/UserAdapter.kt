package com.techstore.app.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techstore.app.data.model.User
import com.techstore.app.databinding.ItemUserBinding

class UserAdapter(
    private val onEdit: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val items = mutableListOf<User>()

    fun submit(users: List<User>) {
        items.clear()
        items.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = items[position]
        holder.binding.tvUser.text =
            "${user.nombre}\n${user.email}\nRol: ${user.role}\nActivo: ${user.activo}"
        holder.binding.btnEdit.text = if (user.activo) "Desactivar" else "Activar"
        holder.binding.btnEdit.setOnClickListener { onEdit(user) }
        holder.binding.btnDelete.setOnClickListener { onDelete(user) }
    }

    override fun getItemCount(): Int = items.size

    class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)
}
