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
import GeneralType.GeneralTypeNodeExtentProvider;
import GeneralType.GeneralTypePane;
import GeneralType.GeneralTypeTreeForTreeLayout;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;

import javax.swing.*;
import java.awt.*;

/**
 * Created by michael on 03/10/16.
 */
public class Visualiser extends JPanel {


  private static void showInDialog(JComponent panel) {
    JDialog dialog = new JDialog();
    Container contentPane = dialog.getContentPane();
    ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    contentPane.add(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
  }

  public static void show(GeneralType gt) {
    TreeForTreeLayout<GeneralType> tree = new GeneralTypeTreeForTreeLayout(gt);

    double gapBetweenLevels = 50;
    double gapBetweenNodes = 10;
    DefaultConfiguration<GeneralType> configuration = new DefaultConfiguration<>(
      gapBetweenLevels, gapBetweenNodes);
    GeneralTypeNodeExtentProvider nodeExtentProvider = new GeneralTypeNodeExtentProvider();

    TreeLayout<GeneralType> treeLayout = new TreeLayout<>(tree, nodeExtentProvider, configuration);

    GeneralTypePane panel = new GeneralTypePane(treeLayout);
    showInDialog(panel);
  }
}
