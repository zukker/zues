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
 * Title: Команды для градуировки приборов ИОН-1 в полуавтоматическом режиме
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
			new Command("goToAbsZenith", "Зенит 90", "", 90, 3),
			new Command("goToAzimuth", "Азимут 90", "", 90, 5),
			new Command("goToSensorAz", "Az 0", "", 0, 3),
			new Command("doJoinVizirAz", "Совместить визир с Az", "", 0, 0),
			new Command("goToVizir", "Визир 5", "", 5, 3),
			new Command("goToVizir", "Визир 10", "", 10, 3),
			new Command("goToVizir", "Визир 15", "", 15, 3),
			new Command("goToVizir", "Визир 20", "", 20, 3),
			new Command("goToVizir", "Визир 25", "", 25, 3),
			new Command("goToVizir", "Визир 30", "", 30, 3),
			new Command("goToVizir", "Визир 35", "", 35, 3),
			new Command("goToVizir", "Визир 40", "", 40, 3),
			new Command("goToVizir", "Визир 45", "", 45, 3),
			new Command("goToVizir", "Визир 50", "", 50, 3),
			new Command("goToVizir", "Визир 55", "", 55, 3),
			new Command("goToVizir", "Визир 60", "", 60, 3),
			new Command("goToVizir", "Визир 65", "", 65, 3),
			new Command("goToVizir", "Визир 70", "", 70, 3),
			new Command("goToVizir", "Визир 75", "", 75, 3),
			new Command("goToVizir", "Визир 80", "", 80, 3),
			new Command("goToVizir", "Визир 85", "", 85, 3),
			new Command("goToVizir", "Визир 90", "", 90, 3),
			new Command("goToVizir", "Визир 95", "", 95, 3),
			new Command("goToVizir", "Визир 100", "", 100, 3),
			new Command("goToVizir", "Визир 105", "", 105, 3),
			new Command("goToVizir", "Визир 110", "", 110, 3),
			new Command("goToVizir", "Визир 115", "", 115, 3),
			new Command("goToVizir", "Визир 120", "", 120, 3),
			new Command("goToVizir", "Визир 125", "", 125, 3),
			new Command("goToVizir", "Визир 130", "", 130, 3),
			new Command("goToVizir", "Визир 135", "", 135, 3),
			new Command("goToVizir", "Визир 140", "", 140, 3),
			new Command("goToVizir", "Визир 145", "", 145, 3),
			new Command("goToVizir", "Визир 150", "", 150, 3),
			new Command("goToVizir", "Визир 155", "", 155, 3),
			new Command("goToVizir", "Визир 160", "", 160, 3),
			new Command("goToVizir", "Визир 165", "", 165, 3),
			new Command("goToVizir", "Визир 170", "", 170, 3),
			new Command("goToVizir", "Визир 175", "", 175, 3),
			new Command("goToVizir", "Визир 180", "", 180, 3),
			new Command("goToVizir", "Визир 185", "", 185, 3),
			new Command("goToVizir", "Визир 190", "", 190, 3),
			new Command("goToVizir", "Визир 195", "", 195, 3),
			new Command("goToVizir", "Визир 200", "", 200, 3),
			new Command("goToVizir", "Визир 205", "", 205, 3),
			new Command("goToVizir", "Визир 210", "", 210, 3),
			new Command("goToVizir", "Визир 215", "", 215, 3),
			new Command("goToVizir", "Визир 220", "", 220, 3),
			new Command("goToVizir", "Визир 225", "", 225, 3),
			new Command("goToVizir", "Визир 230", "", 230, 3),
			new Command("goToVizir", "Визир 235", "", 235, 3),
			new Command("goToVizir", "Визир 240", "", 240, 3),
			new Command("goToVizir", "Визир 245", "", 245, 3),
			new Command("goToVizir", "Визир 250", "", 250, 3),
			new Command("goToVizir", "Визир 255", "", 255, 3),
			new Command("goToVizir", "Визир 260", "", 260, 3),
			new Command("goToVizir", "Визир 265", "", 265, 3),
			new Command("goToVizir", "Визир 270", "", 270, 3),
			new Command("goToVizir", "Визир 275", "", 275, 3),
			new Command("goToVizir", "Визир 280", "", 280, 3),
			new Command("goToVizir", "Визир 285", "", 285, 3),
			new Command("goToVizir", "Визир 290", "", 290, 3),
			new Command("goToVizir", "Визир 295", "", 295, 3),
			new Command("goToVizir", "Визир 300", "", 300, 3),
			new Command("goToVizir", "Визир 305", "", 305, 3),
			new Command("goToVizir", "Визир 310", "", 310, 3),
			new Command("goToVizir", "Визир 315", "", 315, 3),
			new Command("goToVizir", "Визир 320", "", 320, 3),
			new Command("goToVizir", "Визир 325", "", 325, 3),
			new Command("goToVizir", "Визир 330", "", 330, 3),
			new Command("goToVizir", "Визир 335", "", 335, 3),
			new Command("goToVizir", "Визир 340", "", 340, 3),
			new Command("goToVizir", "Визир 345", "", 345, 3),
			new Command("goToVizir", "Визир 350", "", 350, 3),
			new Command("goToVizir", "Визир 355", "", 355, 3), 
			};

	public static final Command[] commandsAx = {
			new Command("goToAbsZenith", "Зенит 90", "", 90, 15),
			new Command("goToAzimuth", "Азимут 180", "", 180, 30),
			new Command("goToSensorAz", "Az 0", "", 0, 15),
			new Command("goToSensorAx", "Ax 0", "", 0, 3),
			new Command("doJoinZenithAx", "Совместить зенит с Ax", "", 0, 0),
			new Command("goToZenith", "Зенит 5", "", 5, 3),
			new Command("goToZenith", "Зенит 10", "", 10, 3), 
            new Command("goToZenith", "Зенит 15", "", 15, 3),
            new Command("goToZenith", "Зенит 20", "", 20, 3), 
            new Command("goToZenith", "Зенит 25", "", 25, 3),
            new Command("goToZenith", "Зенит 30", "", 30, 3), 
            new Command("goToZenith", "Зенит 35", "", 35, 3),
            new Command("goToZenith", "Зенит 40", "", 40, 3), 
            new Command("goToZenith", "Зенит 45", "", 45, 3),
            new Command("goToZenith", "Зенит 50", "", 50, 3), 
            new Command("goToZenith", "Зенит 55", "", 55, 3),
            new Command("goToZenith", "Зенит 60", "", 60, 3), 
            new Command("goToZenith", "Зенит 65", "", 65, 3),
            new Command("goToZenith", "Зенит 70", "", 70, 3), 
            new Command("goToZenith", "Зенит 75", "", 75, 3),
            new Command("goToZenith", "Зенит 80", "", 80, 3), 
            new Command("goToZenith", "Зенит 85", "", 85, 3),
            new Command("goToZenith", "Зенит 90", "", 90, 3), 
            new Command("goToZenith", "Зенит 95", "", 95, 3),
            new Command("goToZenith", "Зенит 100", "", 100, 3), 
            new Command("goToZenith", "Зенит 105", "", 105, 3),
            new Command("goToZenith", "Зенит 110", "", 110, 3), 
            new Command("goToZenith", "Зенит 115", "", 115, 3),
            new Command("goToZenith", "Зенит 120", "", 120, 3), 
            new Command("goToZenith", "Зенит 125", "", 125, 3),
            new Command("goToZenith", "Зенит 130", "", 130, 3), 
            new Command("goToZenith", "Зенит 135", "", 135, 3),
            new Command("goToZenith", "Зенит 140", "", 140, 3), 
            new Command("goToZenith", "Зенит 145", "", 145, 3),
            new Command("goToZenith", "Зенит 150", "", 150, 3), 
            new Command("goToZenith", "Зенит 155", "", 155, 3),
            new Command("goToZenith", "Зенит 160", "", 160, 3), 
            new Command("goToZenith", "Зенит 165", "", 165, 3),
            new Command("goToZenith", "Зенит 170", "", 170, 3), 
            new Command("goToZenith", "Зенит 175", "", 175, 3),
            new Command("goToZenith", "Зенит 180", "", 180, 3), 
            new Command("goToZenith", "Зенит 185", "", 185, 3),
            new Command("goToZenith", "Зенит 190", "", 190, 3), 
            new Command("goToZenith", "Зенит 195", "", 195, 3),
            new Command("goToZenith", "Зенит 200", "", 200, 3),
            new Command("goToZenith", "Зенит 205", "", 205, 3),
            new Command("goToZenith", "Зенит 210", "", 210, 3), 
            new Command("goToZenith", "Зенит 215", "", 215, 3),
            new Command("goToZenith", "Зенит 220", "", 220, 3), 
            new Command("goToZenith", "Зенит 225", "", 225, 3),
            new Command("goToZenith", "Зенит 230", "", 230, 3), 
            new Command("goToZenith", "Зенит 235", "", 235, 3),
            new Command("goToZenith", "Зенит 240", "", 240, 3), 
            new Command("goToZenith", "Зенит 245", "", 245, 3),
            new Command("goToZenith", "Зенит 250", "", 250, 3), 
            new Command("goToZenith", "Зенит 255", "", 255, 3),
            new Command("goToZenith", "Зенит 260", "", 260, 3), 
            new Command("goToZenith", "Зенит 265", "", 265, 3),
            new Command("goToZenith", "Зенит 270", "", 270, 3), 
            new Command("goToZenith", "Зенит 275", "", 275, 3),
            new Command("goToZenith", "Зенит 280", "", 280, 3), 
            new Command("goToZenith", "Зенит 285", "", 285, 3),
            new Command("goToZenith", "Зенит 290", "", 290, 3), 
            new Command("goToZenith", "Зенит 295", "", 295, 3),
            new Command("goToZenith", "Зенит 300", "", 300, 3), 
            new Command("goToZenith", "Зенит 305", "", 305, 3),
            new Command("goToZenith", "Зенит 310", "", 310, 3), 
            new Command("goToZenith", "Зенит 315", "", 315, 3),
            new Command("goToZenith", "Зенит 320", "", 320, 3), 
            new Command("goToZenith", "Зенит 325", "", 325, 3),
            new Command("goToZenith", "Зенит 330", "", 330, 3), 
            new Command("goToZenith", "Зенит 335", "", 335, 3),
            new Command("goToZenith", "Зенит 340", "", 340, 3), 
            new Command("goToZenith", "Зенит 345", "", 345, 3),
            new Command("goToZenith", "Зенит 350", "", 350, 3), 
            new Command("goToZenith", "Зенит 355", "", 355, 3),

			};

	public static final Command[] commandsAy = {
			new Command("goToAbsZenith", "Зенит 90", "", 90, 15),
			new Command("goToAzimuth", "Азимут 180", "", 180, 30),
			new Command("goToSensorAz", "Az 270", "", 270, 15),
			new Command("goToSensorAy", "Ay 0", "", 0, 3),
			new Command("doJoinZenithAy", "Совместить зенит с Ay", "", 0, 0),
			new Command("goToZenith", "Зенит 5", "", 5, 3),
			new Command("goToZenith", "Зенит 10", "", 10, 3), 
            new Command("goToZenith", "Зенит 15", "", 15, 3),
            new Command("goToZenith", "Зенит 20", "", 20, 3), 
            new Command("goToZenith", "Зенит 25", "", 25, 3),
            new Command("goToZenith", "Зенит 30", "", 30, 3), 
            new Command("goToZenith", "Зенит 35", "", 35, 3),
            new Command("goToZenith", "Зенит 40", "", 40, 3), 
            new Command("goToZenith", "Зенит 45", "", 45, 3),
            new Command("goToZenith", "Зенит 50", "", 50, 3), 
            new Command("goToZenith", "Зенит 55", "", 55, 3),
            new Command("goToZenith", "Зенит 60", "", 60, 3), 
            new Command("goToZenith", "Зенит 65", "", 65, 3),
            new Command("goToZenith", "Зенит 70", "", 70, 3), 
            new Command("goToZenith", "Зенит 75", "", 75, 3),
            new Command("goToZenith", "Зенит 80", "", 80, 3), 
            new Command("goToZenith", "Зенит 85", "", 85, 3),
            new Command("goToZenith", "Зенит 90", "", 90, 3), 
            new Command("goToZenith", "Зенит 95", "", 95, 3),
            new Command("goToZenith", "Зенит 100", "", 100, 3), 
            new Command("goToZenith", "Зенит 105", "", 105, 3),
            new Command("goToZenith", "Зенит 110", "", 110, 3), 
            new Command("goToZenith", "Зенит 115", "", 115, 3),
            new Command("goToZenith", "Зенит 120", "", 120, 3), 
            new Command("goToZenith", "Зенит 125", "", 125, 3),
            new Command("goToZenith", "Зенит 130", "", 130, 3), 
            new Command("goToZenith", "Зенит 135", "", 135, 3),
            new Command("goToZenith", "Зенит 140", "", 140, 3), 
            new Command("goToZenith", "Зенит 145", "", 145, 3),
            new Command("goToZenith", "Зенит 150", "", 150, 3), 
            new Command("goToZenith", "Зенит 155", "", 155, 3),
            new Command("goToZenith", "Зенит 160", "", 160, 3), 
            new Command("goToZenith", "Зенит 165", "", 165, 3),
            new Command("goToZenith", "Зенит 170", "", 170, 3), 
            new Command("goToZenith", "Зенит 175", "", 175, 3),
            new Command("goToZenith", "Зенит 180", "", 180, 3), 
            new Command("goToZenith", "Зенит 185", "", 185, 3),
            new Command("goToZenith", "Зенит 190", "", 190, 3), 
            new Command("goToZenith", "Зенит 195", "", 195, 3),
            new Command("goToZenith", "Зенит 200", "", 200, 3),
            new Command("goToZenith", "Зенит 205", "", 205, 3),
            new Command("goToZenith", "Зенит 210", "", 210, 3), 
            new Command("goToZenith", "Зенит 215", "", 215, 3),
            new Command("goToZenith", "Зенит 220", "", 220, 3), 
            new Command("goToZenith", "Зенит 225", "", 225, 3),
            new Command("goToZenith", "Зенит 230", "", 230, 3), 
            new Command("goToZenith", "Зенит 235", "", 235, 3),
            new Command("goToZenith", "Зенит 240", "", 240, 3), 
            new Command("goToZenith", "Зенит 245", "", 245, 3),
            new Command("goToZenith", "Зенит 250", "", 250, 3), 
            new Command("goToZenith", "Зенит 255", "", 255, 3),
            new Command("goToZenith", "Зенит 260", "", 260, 3), 
            new Command("goToZenith", "Зенит 265", "", 265, 3),
            new Command("goToZenith", "Зенит 270", "", 270, 3), 
            new Command("goToZenith", "Зенит 275", "", 275, 3),
            new Command("goToZenith", "Зенит 280", "", 280, 3), 
            new Command("goToZenith", "Зенит 285", "", 285, 3),
            new Command("goToZenith", "Зенит 290", "", 290, 3), 
            new Command("goToZenith", "Зенит 295", "", 295, 3),
            new Command("goToZenith", "Зенит 300", "", 300, 3), 
            new Command("goToZenith", "Зенит 305", "", 305, 3),
            new Command("goToZenith", "Зенит 310", "", 310, 3), 
            new Command("goToZenith", "Зенит 315", "", 315, 3),
            new Command("goToZenith", "Зенит 320", "", 320, 3), 
            new Command("goToZenith", "Зенит 325", "", 325, 3),
            new Command("goToZenith", "Зенит 330", "", 330, 3), 
            new Command("goToZenith", "Зенит 335", "", 335, 3),
            new Command("goToZenith", "Зенит 340", "", 340, 3), 
            new Command("goToZenith", "Зенит 345", "", 345, 3),
            new Command("goToZenith", "Зенит 350", "", 350, 3), 
            new Command("goToZenith", "Зенит 355", "", 355, 3),
			};

	public static final Command[] commandsZeroAx = {
			new Command("goToAbsZenith", "Зенит 90", "", 90, 15),
			new Command("goToAzimuth", "Азимут 0", "", 0, 5),
			new Command("doJoinVizirAz", "Совместить визир с Az", "", 0, 0),
			new Command("goToVizir", "Визир 0", "", 0, 3),
			new Command("goToVizir", "Визир 90", "", 90, 3),
			new Command("goToVizir", "Визир 180", "", 180, 3),
			new Command("goToVizir", "Визир 270", "", 270, 3),
			new Command("goToVizir", "Визир 0", "", 0, 3),
			new Command("goToVizir", "Визир 90", "", 90, 3), };

	public static final Command[] commandsErrorAz = {
			new Command("goToZenith", "Зенит 90", "", 90, 15),
			new Command("doJoinVizirAz", "Совместить визир с Az", "", 0, 0),
			new Command("goToZenith", "Зенит 7", "", 7, 15),
			new Command("goToVizir", "Визир 0", "", 0, 15),
			new Command("goToVizir", "Визир 90", "", 90, 15),
			new Command("goToVizir", "Визир 180", "", 180, 15),
			new Command("goToVizir", "Визир 270", "", 270, 15), };

};
