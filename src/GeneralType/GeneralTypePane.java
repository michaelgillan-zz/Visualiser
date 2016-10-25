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
package GeneralType;

import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by michael on 03/10/16.
 */
public class GeneralTypePane extends JComponent {

  private final int childSep = 50;
  private final int maxWidth = 100;

  private final TreeLayout<GeneralType> treeLayout;

  private TreeForTreeLayout<GeneralType> getTree() {
    return  treeLayout.getTree();
  }

  private Iterable<GeneralType> getChildren(GeneralType parent) {
    return getTree().getChildren(parent);
  }

  private Rectangle2D.Double getBoundsOfNode(GeneralType node) {
    return treeLayout.getNodeBounds().get(node);
  }

  public GeneralTypePane(TreeLayout<GeneralType> treeLayout) {
    this.treeLayout = treeLayout;

    Dimension size = treeLayout.getBounds().getBounds().getSize();
    setPreferredSize(size);
  }

  private void paintEdges(Graphics g, GeneralType parent) {

    if (!getTree().isLeaf(parent)) {
      Rectangle2D.Double b1 = getBoundsOfNode(parent);
      g.drawLine((int)(b1.x + (b1.width/2)), (int)(b1.y + b1.height), (int)(b1.x + (b1.width/2)), (int)(b1.y + b1.height + childSep/2));

      for (GeneralType child : getChildren(parent)) {
        Rectangle2D.Double b2 = getBoundsOfNode(child);
        g.drawLine((int)(b1.x + (b1.width/2)), (int)(b1.y + b1.height + childSep/2), (int)(b2.x + (b2.width/2)), (int)(b2.y - childSep/2));
        g.drawLine((int)(b2.x + (b2.width/2)), (int)b2.y, (int)(b2.x + (b2.width/2)), (int)(b2.y - childSep/2));

        paintEdges(g, child);
      }
    }

  }

  private void paintBox(Graphics g, GeneralType node) {

    Rectangle2D.Double box = getBoundsOfNode(node);

    g.drawRect((int) box.x, (int) box.y, (int) box.width - 1,
               (int) box.height - 1);



    int stringSep = (int)box.height/(node.fields.length + 1);
    int indent = 5;

    for (int i = 0; i < node.fields.length; i++) {
      int length = g.getFontMetrics().stringWidth(node.strFields[i]);
      String str = node.strFields[i];
      if (length > maxWidth) {
        str = snipLine(str, maxWidth, g.getFontMetrics());
      }
      g.drawString(str, (int) box.x + indent, (int) box.y + (stringSep/2 + (stringSep * (i+1))));
    }

  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);

    paintEdges(g, getTree().getRoot());

    // paint the boxes
    for (GeneralType node : treeLayout.getNodeBounds().keySet()) {
      paintBox(g, node);
    }
  }


  private String snipLine(String str, int maxWidth, FontMetrics fm) {
    while (fm.stringWidth(str) > maxWidth) {
      str = str.substring(0, str.length() - 1);
    }
    str = str.substring(0, str.length() - 3);
    str += "...";

    return  str;

  }

}
