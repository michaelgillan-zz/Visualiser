/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import GeneralType.GeneralType;
import com.intellij.debugger.actions.DebuggerAction;
import com.intellij.debugger.actions.ViewAsGroup;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sun.jdi.ObjectReference;
import com.sun.tools.jdi.ObjectReferenceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 04/07/16.
 */
public class Visualise extends DebuggerAction {

  @Override
  public void actionPerformed(AnActionEvent event) {
    //Gets the object reference of the object selected in the debugger
    try {
      ObjectReferenceImpl objectReference = (ObjectReferenceImpl)ViewAsGroup.getSelectedValues(event).get(0).getDescriptor().getValue();
      List<ObjectReference> objList = new ArrayList<>();
      objList.add(objectReference);
      //Generates a GeneralType tree for the object
      GeneralType gt = GeneralType.convert(objectReference, objList);
      //Draws the GeneralType tree
      Visualiser.show(gt);
    } catch (ClassCastException ex) {
      Notification notification = new Notification("", "Invalid target", "Target must be an object", NotificationType.ERROR);
      Notifications.Bus.notify(notification);
    }

  }
}
