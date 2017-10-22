package com.vovasoft.unilot.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vovasoft.unilot.R
import com.vovasoft.unilot.components.Preferences
import com.vovasoft.unilot.ui.AppFragmentManager
import kotlinx.android.synthetic.main.fragment_settings.*


/***************************************************************************
 * Created by arseniy on 01/10/2017.
 ****************************************************************************/
class SettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lockDrawerMode(true)
        setupView()
    }


    private fun setupView() {
        backBtn.setOnClickListener {
            onBackPressed()
        }

        dailySwitch.isChecked = Preferences.instance.dayLotteryNotify
        weeklySwitch.isChecked = Preferences.instance.weekLotteryNotify
        monthlySwitch.isChecked = Preferences.instance.monthLotteryNotify

        when (Preferences.instance.language) {
            "en" -> enSwitch.isChecked = true
            "ru" -> ruSwitch.isChecked = true
            "de" -> deSwitch.isChecked = true
        }

        dailySwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.instance.dayLotteryNotify = isChecked
        }

        weeklySwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.instance.weekLotteryNotify = isChecked
        }

        monthlySwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.instance.monthLotteryNotify = isChecked
        }

        enSwitch.setOnCheckListener {
            enSwitch.isChecked = true
            ruSwitch.isChecked = false
            deSwitch.isChecked = false
            changeLanguage("en")
        }

        ruSwitch.setOnCheckListener {
            enSwitch.isChecked = false
            ruSwitch.isChecked = true
            deSwitch.isChecked = false
            changeLanguage("ru")
        }

        deSwitch.setOnCheckListener {
            enSwitch.isChecked = false
            ruSwitch.isChecked = false
            deSwitch.isChecked = true
            changeLanguage("de")
        }
    }


    private fun changeLanguage(language: String) {
        if (Preferences.instance.language != language) {
            Preferences.instance.language = language
            val intent = activity.intent
            AppFragmentManager.instance.clearBackStack()
            activity.startActivity(intent)
        }
    }


    override fun onBackPressed() {
        lockDrawerMode(false)
        AppFragmentManager.instance.popBackStack()
    }
}