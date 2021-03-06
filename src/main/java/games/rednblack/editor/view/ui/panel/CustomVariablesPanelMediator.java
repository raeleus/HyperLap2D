/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package games.rednblack.editor.view.ui.panel;

import com.badlogic.ashley.core.Entity;
import games.rednblack.editor.view.menu.WindowMenu;
import games.rednblack.h2d.common.MsgAPI;
import com.puremvc.patterns.mediator.SimpleMediator;
import com.puremvc.patterns.observer.Notification;
import games.rednblack.editor.HyperLap2DFacade;
import games.rednblack.editor.controller.commands.CustomVariableModifyCommand;
import games.rednblack.editor.renderer.components.MainItemComponent;
import games.rednblack.editor.renderer.utils.ComponentRetriever;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.editor.view.stage.UIStage;
import games.rednblack.editor.view.ui.properties.panels.UIBasicItemProperties;

import java.util.Set;

/**
 * Created by azakhary on 5/12/2015.
 */
public class CustomVariablesPanelMediator extends SimpleMediator<CustomVariablesPanel> {
    private static final String TAG = CustomVariablesPanelMediator.class.getCanonicalName();
    private static final String NAME = TAG;

    private Entity observable = null;

    public CustomVariablesPanelMediator() {
        super(NAME, new CustomVariablesPanel());
    }

    @Override
    public void onRegister() {
        super.onRegister();
        facade = HyperLap2DFacade.getInstance();
        viewComponent.setEmpty();
    }

    @Override
    public String[] listNotificationInterests() {
        return new String[]{
                MsgAPI.ITEM_SELECTION_CHANGED,
                MsgAPI.EMPTY_SPACE_CLICKED,
                UIBasicItemProperties.CUSTOM_VARS_BUTTON_CLICKED,
                CustomVariablesPanel.ADD_BUTTON_PRESSED,
                CustomVariablesPanel.DELETE_BUTTON_PRESSED,
                WindowMenu.CUSTOM_VARIABLES_EDITOR_OPEN,
                CustomVariableModifyCommand.DONE
        };
    }

    @Override
    public void handleNotification(Notification notification) {
        super.handleNotification(notification);

        Sandbox sandbox = Sandbox.getInstance();
        UIStage uiStage = sandbox.getUIStage();

        switch (notification.getName()) {
            case WindowMenu.CUSTOM_VARIABLES_EDITOR_OPEN:
                viewComponent.show(uiStage);
                break;
            case UIBasicItemProperties.CUSTOM_VARS_BUTTON_CLICKED:
                viewComponent.show(uiStage);
                break;
            case MsgAPI.ITEM_SELECTION_CHANGED:
                Set<Entity> selection = notification.getBody();
                if(selection.size() == 1) {
                    setObservable(selection.iterator().next());
                }
                break;
            case MsgAPI.EMPTY_SPACE_CLICKED:
                setObservable(null);
                break;
            case CustomVariablesPanel.ADD_BUTTON_PRESSED:
                setVariable();
                break;
            case CustomVariablesPanel.DELETE_BUTTON_PRESSED:
                removeVariable(notification.getBody());
                break;
            case CustomVariableModifyCommand.DONE:
                updateView();
                break;
        }
    }

    private void setVariable() {
        String key = viewComponent.getKey();
        String value = viewComponent.getValue();

        sendNotification(MsgAPI.CUSTOM_VARIABLE_MODIFY, CustomVariableModifyCommand.addCustomVariable(observable, key, value));
    }

    private void removeVariable(String key) {
        sendNotification(MsgAPI.CUSTOM_VARIABLE_MODIFY, CustomVariableModifyCommand.removeCustomVariable(observable, key));
    }

    private void setObservable(Entity item) {
        observable = item;
        updateView();
        viewComponent.setKeyFieldValue("");
        viewComponent.setValueFieldValue("");
    }

    private void updateView() {
        if(observable == null) {
            viewComponent.setEmpty();
        } else {
            MainItemComponent mainItemComponent = ComponentRetriever.get(observable, MainItemComponent.class);
            viewComponent.updateView(mainItemComponent.customVariables);
        }
    }
}
