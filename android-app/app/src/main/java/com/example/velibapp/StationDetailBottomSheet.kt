package com.example.velibapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StationDetailBottomSheet(
    private val stationId: String,
    private val stationName: String,
    private val bikes: Int,
    private val docks: Int
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view =
            inflater.inflate(
                R.layout.bottom_sheet_station,
                container,
                false
            )

        view.findViewById<TextView>(R.id.tvStationName).text =
            stationName

        view.findViewById<TextView>(R.id.tvBikes).text =
            bikes.toString()

        view.findViewById<TextView>(R.id.tvDocks).text =
            docks.toString()

        val tvStatus =
            view.findViewById<TextView>(
                R.id.tvStatus
            )

        tvStatus.text =
            when {

                bikes == 0 ->
                    "🔴 Aucun vélo disponible"

                bikes <= 10 ->
                    "🟠 Disponibilité moyenne"

                else ->
                    "🟢 Station disponible"
            }

        view.findViewById<Button>(R.id.btnFavorite)
            .setOnClickListener {

                FavoriteManager.addFavorite(
                    requireContext(),
                    stationId,
                    stationName
                )

                dismiss()
            }

        return view
    }
}