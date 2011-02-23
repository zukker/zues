package lsdsoft.zeus.ui;

import javax.swing.*;
import java.awt.*;

public class JImage extends JComponent {
  protected Image image = null;
  public JImage() {
  }

  public JImage(Image image) {
    setImage(image);
    setOpaque(false);
  }
  public JImage(Class cls, String path) {
    setImage(createImage(cls, path));
    setOpaque(false);
  }
  protected Image createImage(Class cls, String path) {
      java.net.URL imgURL = cls.getResource(path);
      if (imgURL != null) {
          return new ImageIcon(imgURL).getImage();
      } else {
          System.err.println("Couldn't find file: " + path);
          return null;
      }
  }

  public void setImage(Image image) {
    if(image != null) {
      this.image = image;
      autoSize();
    }
  }
  private void autoSize() {
    setBounds(getX(), getY(), image.getWidth(null), image.getHeight(null));
  }

  public Image getImage() {
    return image;
  }

  public void paintComponent(Graphics graph) {
    if(image != null) {
      autoSize();
      graph.drawImage(image, 0 , 0, null);
    }
  }
}
