package tv.projectivy.plugin.wallpaperprovider.minimalistic


import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidanceStylist.Guidance

class SettingsFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(R.string.plugin_name),
            "v${BuildConfig.VERSION_NAME}\n\n${getString(R.string.plugin_description)}",
            getString(R.string.settings),
            AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_plugin)
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(SHOW_ACTION_BUTTON_ID)
                .title(R.string.show_action_button)
                .checkSetId(GuidedAction.CHECKBOX_CHECK_SET_ID)
                .checked(
                    PreferencesManager[
                        PreferencesManager.SHOW_ACTION_BUTTON,
                        PreferencesManager.DEFAULT_SHOW_ACTION_BUTTON
                    ]
                )
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == SHOW_ACTION_BUTTON_ID) {
            PreferencesManager[PreferencesManager.SHOW_ACTION_BUTTON] = action.isChecked
            (requireActivity() as SettingsActivity).requestWallpaperUpdate()
        }
    }

    private companion object {
        const val SHOW_ACTION_BUTTON_ID = 1L
    }
}
