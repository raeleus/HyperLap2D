package games.rednblack.editor.plugin.skincomposer;

import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.puremvc.patterns.facade.Facade;
import games.rednblack.h2d.common.MsgAPI;
import games.rednblack.h2d.common.plugins.H2DPluginAdapter;
import games.rednblack.h2d.common.view.SettingsNodeValue;
import games.rednblack.h2d.common.view.ui.StandardWidgetsFactory;

public class SkinComposerSettings extends SettingsNodeValue<SkinComposerVO> {

    private final VisCheckBox alwaysCheckUpdates;
    private final H2DPluginAdapter plugin;

    public SkinComposerSettings(Facade facade, H2DPluginAdapter plugin) {
        super("Skin Composer", facade);

        this.plugin = plugin;

        getContentTable().add("Updates").left().row();
        getContentTable().addSeparator();
        alwaysCheckUpdates = StandardWidgetsFactory.createCheckBox("Always check for updates");
        getContentTable().add(alwaysCheckUpdates).left().padTop(5).padLeft(8).row();
    }

    @Override
    public void translateSettingsToView() {
        alwaysCheckUpdates.setChecked(getSettings().alwaysCheckUpdates);
    }

    @Override
    public void translateViewToSettings() {
        getSettings().alwaysCheckUpdates = alwaysCheckUpdates.isChecked();
        getSettings().toStorage(plugin.getStorage());
        facade.sendNotification(MsgAPI.SAVE_EDITOR_CONFIG);
    }

    @Override
    public boolean validateSettings() {
        return getSettings().alwaysCheckUpdates != alwaysCheckUpdates.isChecked();
    }
}
