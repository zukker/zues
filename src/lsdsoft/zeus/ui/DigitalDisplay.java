package lsdsoft.zeus.ui;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

public class DigitalDisplay extends JImage {
  private static final int MAX_DIGITS = 12;
  // изображения цифр
  private ImageIcon[] icons = new ImageIcon[13];
  // ширина поля в знаках (цифрах)
  private int fieldSize = 8;
  // количесво знаков после запятой (дробная часть)
  private int fraction = 3;

  private int digitWidth = 16;
  private int digitHeight = 16;
  private double multi = 1000;
  private final static int SIGN_MINUS = 10;
  private final static int SIGN_POINT = 11;
  private final static int SIGN_EMPTY = 12;
  protected Color foreColor = new Color(100, 200, 20);
  protected Color backColor = new Color(0, 0, 0);
  // названия фалов
  private String[] iconNames = {
    "dig0.png", "dig1.png", "dig2.png", "dig3.png", "dig4.png",
    "dig5.png", "dig6.png", "dig7.png", "dig8.png", "dig9.png",
    "minus.png", "point.png", "dig_.png"};
  private BufferedImage bImage = null; // = new BufferedImage(200, 35, BufferedImage.TYPE_INT_RGB);

  public DigitalDisplay(int field, int frac) {
    initIcons();
    setFieldSize(field);
    setFractionSize(frac);
  }
  public void setFieldSize(int field) {
    if(field > MAX_DIGITS)
      field = MAX_DIGITS;
    fieldSize = field;
    createImageSurface();
  }
  public void setFractionSize(int frac) {
    if(frac > fieldSize)
      frac = fieldSize;
    fraction = frac;
    multi = 1;
    for(int i = 0; i < fraction; i++)
      multi *= 10.0;
  }
  private void createImageSurface() {
    digitWidth = icons[0].getIconWidth();
    digitHeight = icons[0].getIconHeight();
    int width = digitWidth * fieldSize;
    bImage = new BufferedImage(width, digitHeight, BufferedImage.TYPE_INT_RGB);
    image = bImage;
  }

  public Image getImage() {
    return image;
  }
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = ClassLoader.getSystemResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  private void initIcons() {
    for(int i = 0; i < icons.length; i++) {
      icons[i] = createImageIcon("images/digits/" + iconNames[i]);
    }
  }
  // прорисовка цифры в указанной позиции
  // 0 - самая левая позиция (мето для знака "минус")
  private void renderDigit(int pos, int id) {
    image.getGraphics().drawImage(icons[id].getImage(),
                                  digitWidth * pos, 0, null);
  }
  private void renderPoint() {
    image.getGraphics().drawImage(icons[SIGN_POINT].getImage(),
                                  (fieldSize - fraction) * digitWidth - 5,
                                  digitHeight - 6, null);
  }
  public void render(int number) {
    //WritableRaster raster = bImage.getRaster();
    //int width = bImage.getWidth();
    boolean neg = number < 0;
    int digcount = 0;
    if(neg) {
        number = -number;
    }
    for(int i = 0; i < fieldSize; i++) {
      int index = number % 10;
      //Graphics g = icons[i].getImage().getGraphics();
      if(i > fraction && index == 0 && number == 0){
          index = SIGN_EMPTY;
      }
      if(index < 10) {
          digcount++;
      }
      renderDigit(fieldSize - i - 1, index);
      //image.getGraphics().drawImage(icons[index].getImage(), width - digitWidth * i, 0, null);
//      BufferedImage img = (BufferedImage)icons[i].getImage();
 //     raster.setRect(width - digitWidth * i, 0, img.getRaster());
      number /= 10;
//      if(number == 0 && index > 0)
//        negpos = fieldSize - i;
    }
    // draw "minus" sign if need
    if(neg) {
      int negpos = fieldSize - digcount - 1;
      if(negpos < 0) {
          negpos = 0;
      }
      renderDigit(negpos, SIGN_MINUS);
    }
    if(fraction > 0) {
        renderPoint();
    }
  }

  public void render(double number) {
    render((int)(number * multi));
  }
  public void renderClear() {
    for(int i = 0; i < fieldSize; i++) {
        renderDigit( fieldSize - i - 1, SIGN_EMPTY );
    }
  }
}
