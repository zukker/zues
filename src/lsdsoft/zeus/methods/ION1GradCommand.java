package lsdsoft.zeus.methods;

/*
 import java.lang.reflect.*;
 import java.lang.Character;
 import java.text.*;
 import java.util.*;
 import java.util.Timer;

 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 import javax.swing.border.*;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.TreeSelectionModel;

 import com.lsdsoft.math.*;
 import lsdsoft.metrolog.*;
 import lsdsoft.metrolog.unit.*;
 import lsdsoft.units.*;
 import lsdsoft.util.*;
 import lsdsoft.zeus.*;
 import lsdsoft.zeus.ui.*;
 */

/**
 * <p>
 * Title: ������� ��� ����������� �������� ���-1 � ������������������ ������
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Ural-Geo
 * </p>
 * 
 * @author lsdsoft
 * @version 1.0
 * @todo: load from file
 */
public class ION1GradCommand {
	static class Command {
		// protected Class[] params = {};

		public String description;
		public String name;
		public String method;
		double arg1, arg2;

		public Command(String method, String name, String desc, double a1,
				double a2) {
			this.method = method;
			this.name = name;
			this.description = desc;
			arg1 = a1;
			arg2 = a2;
		}

		/*
		 * void execute() throws Exception { // Method[] meths =
		 * viewer.getClass().getMethods(); Method method =
		 * viewer.getClass().getMethod(this.method, params);
		 * viewer.commandArgument1 = arg1; viewer.commandArgument2 = arg2;
		 * 
		 * if (method != null) { method.invoke(viewer, (Object[]) null); }
		 * 
		 * }
		 */
		public String toString() {
			return name;
		}
	}

	public static final Command[] commandsAz = {
			new Command("goToAbsZenith", "����� 90", "", 90, 3),
			new Command("goToAzimuth", "������ 90", "", 90, 5),
			new Command("goToSensorAz", "Az 0", "", 0, 3),
			new Command("doJoinVizirAz", "���������� ����� � Az", "", 0, 0),
			new Command("goToVizir", "����� 5", "", 5, 3),
			new Command("goToVizir", "����� 10", "", 10, 3),
			new Command("goToVizir", "����� 15", "", 15, 3),
			new Command("goToVizir", "����� 20", "", 20, 3),
			new Command("goToVizir", "����� 25", "", 25, 3),
			new Command("goToVizir", "����� 30", "", 30, 3),
			new Command("goToVizir", "����� 35", "", 35, 3),
			new Command("goToVizir", "����� 40", "", 40, 3),
			new Command("goToVizir", "����� 45", "", 45, 3),
			new Command("goToVizir", "����� 50", "", 50, 3),
			new Command("goToVizir", "����� 55", "", 55, 3),
			new Command("goToVizir", "����� 60", "", 60, 3),
			new Command("goToVizir", "����� 65", "", 65, 3),
			new Command("goToVizir", "����� 70", "", 70, 3),
			new Command("goToVizir", "����� 75", "", 75, 3),
			new Command("goToVizir", "����� 80", "", 80, 3),
			new Command("goToVizir", "����� 85", "", 85, 3),
			new Command("goToVizir", "����� 90", "", 90, 3),
			new Command("goToVizir", "����� 95", "", 95, 3),
			new Command("goToVizir", "����� 100", "", 100, 3),
			new Command("goToVizir", "����� 105", "", 105, 3),
			new Command("goToVizir", "����� 110", "", 110, 3),
			new Command("goToVizir", "����� 115", "", 115, 3),
			new Command("goToVizir", "����� 120", "", 120, 3),
			new Command("goToVizir", "����� 125", "", 125, 3),
			new Command("goToVizir", "����� 130", "", 130, 3),
			new Command("goToVizir", "����� 135", "", 135, 3),
			new Command("goToVizir", "����� 140", "", 140, 3),
			new Command("goToVizir", "����� 145", "", 145, 3),
			new Command("goToVizir", "����� 150", "", 150, 3),
			new Command("goToVizir", "����� 155", "", 155, 3),
			new Command("goToVizir", "����� 160", "", 160, 3),
			new Command("goToVizir", "����� 165", "", 165, 3),
			new Command("goToVizir", "����� 170", "", 170, 3),
			new Command("goToVizir", "����� 175", "", 175, 3),
			new Command("goToVizir", "����� 180", "", 180, 3),
			new Command("goToVizir", "����� 185", "", 185, 3),
			new Command("goToVizir", "����� 190", "", 190, 3),
			new Command("goToVizir", "����� 195", "", 195, 3),
			new Command("goToVizir", "����� 200", "", 200, 3),
			new Command("goToVizir", "����� 205", "", 205, 3),
			new Command("goToVizir", "����� 210", "", 210, 3),
			new Command("goToVizir", "����� 215", "", 215, 3),
			new Command("goToVizir", "����� 220", "", 220, 3),
			new Command("goToVizir", "����� 225", "", 225, 3),
			new Command("goToVizir", "����� 230", "", 230, 3),
			new Command("goToVizir", "����� 235", "", 235, 3),
			new Command("goToVizir", "����� 240", "", 240, 3),
			new Command("goToVizir", "����� 245", "", 245, 3),
			new Command("goToVizir", "����� 250", "", 250, 3),
			new Command("goToVizir", "����� 255", "", 255, 3),
			new Command("goToVizir", "����� 260", "", 260, 3),
			new Command("goToVizir", "����� 265", "", 265, 3),
			new Command("goToVizir", "����� 270", "", 270, 3),
			new Command("goToVizir", "����� 275", "", 275, 3),
			new Command("goToVizir", "����� 280", "", 280, 3),
			new Command("goToVizir", "����� 285", "", 285, 3),
			new Command("goToVizir", "����� 290", "", 290, 3),
			new Command("goToVizir", "����� 295", "", 295, 3),
			new Command("goToVizir", "����� 300", "", 300, 3),
			new Command("goToVizir", "����� 305", "", 305, 3),
			new Command("goToVizir", "����� 310", "", 310, 3),
			new Command("goToVizir", "����� 315", "", 315, 3),
			new Command("goToVizir", "����� 320", "", 320, 3),
			new Command("goToVizir", "����� 325", "", 325, 3),
			new Command("goToVizir", "����� 330", "", 330, 3),
			new Command("goToVizir", "����� 335", "", 335, 3),
			new Command("goToVizir", "����� 340", "", 340, 3),
			new Command("goToVizir", "����� 345", "", 345, 3),
			new Command("goToVizir", "����� 350", "", 350, 3),
			new Command("goToVizir", "����� 355", "", 355, 3), 
			};

	public static final Command[] commandsAx = {
			new Command("goToAbsZenith", "����� 90", "", 90, 15),
			new Command("goToAzimuth", "������ 180", "", 180, 30),
			new Command("goToSensorAz", "Az 0", "", 0, 15),
			new Command("goToSensorAx", "Ax 0", "", 0, 3),
			new Command("doJoinZenithAx", "���������� ����� � Ax", "", 0, 0),
			new Command("goToZenith", "����� 5", "", 5, 3),
			new Command("goToZenith", "����� 10", "", 10, 3), 
            new Command("goToZenith", "����� 15", "", 15, 3),
            new Command("goToZenith", "����� 20", "", 20, 3), 
            new Command("goToZenith", "����� 25", "", 25, 3),
            new Command("goToZenith", "����� 30", "", 30, 3), 
            new Command("goToZenith", "����� 35", "", 35, 3),
            new Command("goToZenith", "����� 40", "", 40, 3), 
            new Command("goToZenith", "����� 45", "", 45, 3),
            new Command("goToZenith", "����� 50", "", 50, 3), 
            new Command("goToZenith", "����� 55", "", 55, 3),
            new Command("goToZenith", "����� 60", "", 60, 3), 
            new Command("goToZenith", "����� 65", "", 65, 3),
            new Command("goToZenith", "����� 70", "", 70, 3), 
            new Command("goToZenith", "����� 75", "", 75, 3),
            new Command("goToZenith", "����� 80", "", 80, 3), 
            new Command("goToZenith", "����� 85", "", 85, 3),
            new Command("goToZenith", "����� 90", "", 90, 3), 
            new Command("goToZenith", "����� 95", "", 95, 3),
            new Command("goToZenith", "����� 100", "", 100, 3), 
            new Command("goToZenith", "����� 105", "", 105, 3),
            new Command("goToZenith", "����� 110", "", 110, 3), 
            new Command("goToZenith", "����� 115", "", 115, 3),
            new Command("goToZenith", "����� 120", "", 120, 3), 
            new Command("goToZenith", "����� 125", "", 125, 3),
            new Command("goToZenith", "����� 130", "", 130, 3), 
            new Command("goToZenith", "����� 135", "", 135, 3),
            new Command("goToZenith", "����� 140", "", 140, 3), 
            new Command("goToZenith", "����� 145", "", 145, 3),
            new Command("goToZenith", "����� 150", "", 150, 3), 
            new Command("goToZenith", "����� 155", "", 155, 3),
            new Command("goToZenith", "����� 160", "", 160, 3), 
            new Command("goToZenith", "����� 165", "", 165, 3),
            new Command("goToZenith", "����� 170", "", 170, 3), 
            new Command("goToZenith", "����� 175", "", 175, 3),
            new Command("goToZenith", "����� 180", "", 180, 3), 
            new Command("goToZenith", "����� 185", "", 185, 3),
            new Command("goToZenith", "����� 190", "", 190, 3), 
            new Command("goToZenith", "����� 195", "", 195, 3),
            new Command("goToZenith", "����� 200", "", 200, 3),
            new Command("goToZenith", "����� 205", "", 205, 3),
            new Command("goToZenith", "����� 210", "", 210, 3), 
            new Command("goToZenith", "����� 215", "", 215, 3),
            new Command("goToZenith", "����� 220", "", 220, 3), 
            new Command("goToZenith", "����� 225", "", 225, 3),
            new Command("goToZenith", "����� 230", "", 230, 3), 
            new Command("goToZenith", "����� 235", "", 235, 3),
            new Command("goToZenith", "����� 240", "", 240, 3), 
            new Command("goToZenith", "����� 245", "", 245, 3),
            new Command("goToZenith", "����� 250", "", 250, 3), 
            new Command("goToZenith", "����� 255", "", 255, 3),
            new Command("goToZenith", "����� 260", "", 260, 3), 
            new Command("goToZenith", "����� 265", "", 265, 3),
            new Command("goToZenith", "����� 270", "", 270, 3), 
            new Command("goToZenith", "����� 275", "", 275, 3),
            new Command("goToZenith", "����� 280", "", 280, 3), 
            new Command("goToZenith", "����� 285", "", 285, 3),
            new Command("goToZenith", "����� 290", "", 290, 3), 
            new Command("goToZenith", "����� 295", "", 295, 3),
            new Command("goToZenith", "����� 300", "", 300, 3), 
            new Command("goToZenith", "����� 305", "", 305, 3),
            new Command("goToZenith", "����� 310", "", 310, 3), 
            new Command("goToZenith", "����� 315", "", 315, 3),
            new Command("goToZenith", "����� 320", "", 320, 3), 
            new Command("goToZenith", "����� 325", "", 325, 3),
            new Command("goToZenith", "����� 330", "", 330, 3), 
            new Command("goToZenith", "����� 335", "", 335, 3),
            new Command("goToZenith", "����� 340", "", 340, 3), 
            new Command("goToZenith", "����� 345", "", 345, 3),
            new Command("goToZenith", "����� 350", "", 350, 3), 
            new Command("goToZenith", "����� 355", "", 355, 3),

			};

	public static final Command[] commandsAy = {
			new Command("goToAbsZenith", "����� 90", "", 90, 15),
			new Command("goToAzimuth", "������ 180", "", 180, 30),
			new Command("goToSensorAz", "Az 270", "", 270, 15),
			new Command("goToSensorAy", "Ay 0", "", 0, 3),
			new Command("doJoinZenithAy", "���������� ����� � Ay", "", 0, 0),
			new Command("goToZenith", "����� 5", "", 5, 3),
			new Command("goToZenith", "����� 10", "", 10, 3), 
            new Command("goToZenith", "����� 15", "", 15, 3),
            new Command("goToZenith", "����� 20", "", 20, 3), 
            new Command("goToZenith", "����� 25", "", 25, 3),
            new Command("goToZenith", "����� 30", "", 30, 3), 
            new Command("goToZenith", "����� 35", "", 35, 3),
            new Command("goToZenith", "����� 40", "", 40, 3), 
            new Command("goToZenith", "����� 45", "", 45, 3),
            new Command("goToZenith", "����� 50", "", 50, 3), 
            new Command("goToZenith", "����� 55", "", 55, 3),
            new Command("goToZenith", "����� 60", "", 60, 3), 
            new Command("goToZenith", "����� 65", "", 65, 3),
            new Command("goToZenith", "����� 70", "", 70, 3), 
            new Command("goToZenith", "����� 75", "", 75, 3),
            new Command("goToZenith", "����� 80", "", 80, 3), 
            new Command("goToZenith", "����� 85", "", 85, 3),
            new Command("goToZenith", "����� 90", "", 90, 3), 
            new Command("goToZenith", "����� 95", "", 95, 3),
            new Command("goToZenith", "����� 100", "", 100, 3), 
            new Command("goToZenith", "����� 105", "", 105, 3),
            new Command("goToZenith", "����� 110", "", 110, 3), 
            new Command("goToZenith", "����� 115", "", 115, 3),
            new Command("goToZenith", "����� 120", "", 120, 3), 
            new Command("goToZenith", "����� 125", "", 125, 3),
            new Command("goToZenith", "����� 130", "", 130, 3), 
            new Command("goToZenith", "����� 135", "", 135, 3),
            new Command("goToZenith", "����� 140", "", 140, 3), 
            new Command("goToZenith", "����� 145", "", 145, 3),
            new Command("goToZenith", "����� 150", "", 150, 3), 
            new Command("goToZenith", "����� 155", "", 155, 3),
            new Command("goToZenith", "����� 160", "", 160, 3), 
            new Command("goToZenith", "����� 165", "", 165, 3),
            new Command("goToZenith", "����� 170", "", 170, 3), 
            new Command("goToZenith", "����� 175", "", 175, 3),
            new Command("goToZenith", "����� 180", "", 180, 3), 
            new Command("goToZenith", "����� 185", "", 185, 3),
            new Command("goToZenith", "����� 190", "", 190, 3), 
            new Command("goToZenith", "����� 195", "", 195, 3),
            new Command("goToZenith", "����� 200", "", 200, 3),
            new Command("goToZenith", "����� 205", "", 205, 3),
            new Command("goToZenith", "����� 210", "", 210, 3), 
            new Command("goToZenith", "����� 215", "", 215, 3),
            new Command("goToZenith", "����� 220", "", 220, 3), 
            new Command("goToZenith", "����� 225", "", 225, 3),
            new Command("goToZenith", "����� 230", "", 230, 3), 
            new Command("goToZenith", "����� 235", "", 235, 3),
            new Command("goToZenith", "����� 240", "", 240, 3), 
            new Command("goToZenith", "����� 245", "", 245, 3),
            new Command("goToZenith", "����� 250", "", 250, 3), 
            new Command("goToZenith", "����� 255", "", 255, 3),
            new Command("goToZenith", "����� 260", "", 260, 3), 
            new Command("goToZenith", "����� 265", "", 265, 3),
            new Command("goToZenith", "����� 270", "", 270, 3), 
            new Command("goToZenith", "����� 275", "", 275, 3),
            new Command("goToZenith", "����� 280", "", 280, 3), 
            new Command("goToZenith", "����� 285", "", 285, 3),
            new Command("goToZenith", "����� 290", "", 290, 3), 
            new Command("goToZenith", "����� 295", "", 295, 3),
            new Command("goToZenith", "����� 300", "", 300, 3), 
            new Command("goToZenith", "����� 305", "", 305, 3),
            new Command("goToZenith", "����� 310", "", 310, 3), 
            new Command("goToZenith", "����� 315", "", 315, 3),
            new Command("goToZenith", "����� 320", "", 320, 3), 
            new Command("goToZenith", "����� 325", "", 325, 3),
            new Command("goToZenith", "����� 330", "", 330, 3), 
            new Command("goToZenith", "����� 335", "", 335, 3),
            new Command("goToZenith", "����� 340", "", 340, 3), 
            new Command("goToZenith", "����� 345", "", 345, 3),
            new Command("goToZenith", "����� 350", "", 350, 3), 
            new Command("goToZenith", "����� 355", "", 355, 3),
			};

	public static final Command[] commandsZeroAx = {
			new Command("goToAbsZenith", "����� 90", "", 90, 15),
			new Command("goToAzimuth", "������ 0", "", 0, 5),
			new Command("doJoinVizirAz", "���������� ����� � Az", "", 0, 0),
			new Command("goToVizir", "����� 0", "", 0, 3),
			new Command("goToVizir", "����� 90", "", 90, 3),
			new Command("goToVizir", "����� 180", "", 180, 3),
			new Command("goToVizir", "����� 270", "", 270, 3),
			new Command("goToVizir", "����� 0", "", 0, 3),
			new Command("goToVizir", "����� 90", "", 90, 3), };

	public static final Command[] commandsErrorAz = {
			new Command("goToZenith", "����� 90", "", 90, 15),
			new Command("doJoinVizirAz", "���������� ����� � Az", "", 0, 0),
			new Command("goToZenith", "����� 7", "", 7, 15),
			new Command("goToVizir", "����� 0", "", 0, 15),
			new Command("goToVizir", "����� 90", "", 90, 15),
			new Command("goToVizir", "����� 180", "", 180, 15),
			new Command("goToVizir", "����� 270", "", 270, 15), };

};
