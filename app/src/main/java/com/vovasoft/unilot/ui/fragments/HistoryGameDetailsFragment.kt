package com.vovasoft.unilot.ui.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vovasoft.unilot.R
import com.vovasoft.unilot.components.toHumanDate
import com.vovasoft.unilot.repository.models.Game
import com.vovasoft.unilot.repository.models.Winner
import com.vovasoft.unilot.ui.AppFragmentManager
import com.vovasoft.unilot.ui.recycler_adapters.DetailsHistoryRecyclerAdapter
import com.vovasoft.unilot.ui.widgets.ZxingReader
import com.vovasoft.unilot.view_models.GamesVM
import kotlinx.android.synthetic.main.fragment_history_game_details.*
import java.util.regex.Pattern

/***************************************************************************
 * Created by arseniy on 22/10/2017.
 ****************************************************************************/
class HistoryGameDetailsFragment : BaseFragment(), SearchView.OnQueryTextListener {

    private val gamesVM: GamesVM
        get() = ViewModelProviders.of(activity).get(GamesVM::class.java)

    private var hasCameraPermission = false

    private var game: Game? = null

    private val adapter = DetailsHistoryRecyclerAdapter()

    private val winners = mutableListOf<Winner>()

    private var searchValue: String = ""


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_history_game_details, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lockDrawerMode(true)
        game = gamesVM.selectedHistoryGame
        setupViews()
        observeData()
    }


    private fun observeData() {
        showLoading(true)
        game?.let {
            it.id?.let {
                gamesVM.getWinners(it).observe(this, Observer { winnersData ->
                    showLoading(false)
                    winnersData?.let {
                        this.winners.clear()
                        this.winners.addAll(it)
                    }
                    refreshSearch()
                })
            }

        }
    }


    private fun setupViews() {
        backBtn.setOnClickListener {
            onBackPressed()
        }

        when (game?.type) {
            Game.Type.DAILY.value -> titleIcon.setImageResource(R.drawable.ic_day_selected)
            Game.Type.WEEKLY.value -> titleIcon.setImageResource(R.drawable.ic_week_selected)
            Game.Type.MONTHLY.value -> titleIcon.setImageResource(R.drawable.ic_month_selected)
        }

        dateTv.text = game?.endTime().toHumanDate()
        prizeTv.text = "%.4f".format(game?.prizeAmount)
        peopleTv.text = game?.playersNum?.toString()

        searchBox.setOnQueryTextListener(this)

        scanBtn.setOnClickListener {
            if (hasCameraPermission) {
                runQReader()
            } else {
                requestPermissions(listOf(ZxingReader.CAMERA_PERMISSION).toTypedArray(), ZxingReader.CAMERA_PERMISSION_CODE)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }




    private fun refreshSearch() {
        adapter.dataSet = winners.filter { winner ->
            winner.address?.contains(Regex(Pattern.quote(searchValue))) == true
        }.toMutableList()
    }


    override fun onQueryTextSubmit(text: String?): Boolean {
        return true
    }


    override fun onQueryTextChange(text: String?): Boolean {
        searchValue = text ?: ""
        refreshSearch()
        return true
    }


    private fun runQReader() {
        val intent = Intent(context, ZxingReader::class.java)
        startActivityForResult(intent, ZxingReader.RESULT_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ZxingReader.RESULT_CODE -> {
                    searchBox.setQuery(data?.getStringExtra("result"), false)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ZxingReader.CAMERA_PERMISSION_CODE -> {
                hasCameraPermission = ContextCompat.checkSelfPermission(context, ZxingReader.CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
                if (hasCameraPermission) {
                    runQReader()
                }
            }
        }
    }


    override fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }


    override fun onBackPressed() {
        lockDrawerMode(false)
        gamesVM.selectedHistoryGame = null
        AppFragmentManager.instance.popBackStack()
    }

}