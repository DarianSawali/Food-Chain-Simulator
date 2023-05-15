package ecosys.simulation;

public interface Mover {
	void move();
	void approach(SimulationObject obj);
	void checkCollision();
}
