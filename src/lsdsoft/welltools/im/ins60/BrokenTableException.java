package lsdsoft.welltools.im.ins60;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Ural-Geo</p>
 *
 * @author lsdsoft
 * @version 1.0
 */
public class BrokenTableException
    extends Exception {
    public BrokenTableException() {
        super("������ ������� ��� ����������.");
    }
    public BrokenTableException(int line) {
        super("������ ������� ��� ����������(������ " + line +").");
    }
}
