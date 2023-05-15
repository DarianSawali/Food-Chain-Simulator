package util;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import ecosys.simulation.Food;
import ecosys.simulation.Hunter;
import ecosys.simulation.Predator;
import ecosys.simulation.Prey;
import ecosys.simulation.SimulationObject;
import main.GardenPanel;
import processing.core.PVector;

public class Util {

	public static float random(double min, double max) {
		return (float) (Math.random() * (max - min) + min);
	}

	public static float random(double max) {
		return (float) (Math.random() * max);
	}

	public static Color randomColor() {
		int r = (int) random(255);
		int g = (int) random(255);
		int b = (int) random(255);

		return new Color(r, g, b);
	}

	public static PVector randomPVector(int maxX, int maxY) {
		return new PVector((float) random(maxX), (float) random(maxY));
	}

	public static PVector randomPVector(float magnitude) {
		return PVector.random2D().mult(magnitude);
	}

	public static Food randomFood(GardenPanel pane) {
		return new Food(Util.random(100, pane.panelSize.width - 200), Util.random(100, pane.panelSize.height - 200),
				Util.random(0.6f, 0.9f));
	}

	public static int countFood(ArrayList<SimulationObject> objList) {
		int i = 0;
		for (SimulationObject obj : objList)
			if (obj instanceof Food)
				i++;
		return i;
	}

	public static int countPrey(ArrayList<SimulationObject> objList) {
		int i = 0;
		for (SimulationObject obj : objList)
			if (obj instanceof Prey)
				i++;
		return i;
	}

	public static int countPredator(ArrayList<SimulationObject> objList) {
		int i = 0;
		for (SimulationObject obj : objList)
			if (obj instanceof Predator)
				i++;
		return i;
	}

	public static int countHunter(ArrayList<SimulationObject> objList) {
		int i = 0;
		for (SimulationObject obj : objList)
			if (obj instanceof Hunter)
				i++;
		return i;
	}
}
