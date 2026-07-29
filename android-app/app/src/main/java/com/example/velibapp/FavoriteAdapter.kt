package com.example.velibapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteAdapter(
    private val favorites: List<FavoriteStation>,
    private val onClick: (FavoriteStation) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val stationName: TextView =
            itemView.findViewById(
                R.id.tvStationName
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_favorite,
                    parent,
                    false
                )

        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FavoriteViewHolder,
        position: Int
    ) {

        val favorite =
            favorites[position]

        holder.stationName.text =
            "⭐ ${favorite.stationName}"

        holder.itemView.setOnClickListener {

            onClick(favorite)
        }
    }

    override fun getItemCount(): Int {

        return favorites.size
    }
}