package com.example.ffpanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ffpanel.databinding.ItemProfileBinding

class ProfileAdapter(
    private val items: List<SensitivityProfile>,
    private val onLongClick: (SensitivityProfile) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.VH>() {

    inner class VH(val b: ItemProfileBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.b.tvName.text = p.name
        holder.b.tvValues.text =
            "Gen:${p.general}  RD:${p.redDot}  2x:${p.scope2x}  4x:${p.scope4x}  Sniper:${p.sniper}  FL:${p.freeLook}"
        holder.itemView.setOnLongClickListener { onLongClick(p); true }
    }

    override fun getItemCount() = items.size
}
