package tsz_traffic;

import java.util.ArrayList;

public class Road {
    public ArrayList<Car> carArray;
    final int ROAD_LENGTH;
    public Light light;
    public int outflux = 0, influx = 0;
    final int DIRECTION;        // 0 = vertical; 0 = horizontal
    static final int HORIZONTAL = 1;
    static final int VERTICAL = 0;
    private boolean blocked;
    
    public Road(int roadLength, Light light, int direction) {
        this.ROAD_LENGTH = roadLength; // length of segment + the crossroad behind
        this.carArray = new ArrayList<Car>();
        this.light = light;
        this.DIRECTION = direction;
        this.blocked = false;
    }
    
    public synchronized void goCars(double increment) {
        // but we need to take into account of the delay caused by reaction time and interaction between cars
        for (Car c : this.carArray) {
            c.go(increment, this.carArray, this.blocked, this);
        }
    }

    public synchronized void closeInCars(double increment) {
        for (Car c : this.carArray) {
            c.closeIn(increment, this.carArray, this);
        }
    }

    public synchronized boolean carExit() {
        if (this.carArray.isEmpty()) {
            return false;
        }
        Car frontMostCar = this.carArray.get(0);
        if (frontMostCar.getY() + frontMostCar.getLength() >= this.ROAD_LENGTH) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized void updateLight(double time) {
        this.light.update(time);
    }
    
    public synchronized boolean checkCondition(double time) {
        time = Double.parseDouble(String.format("%.1f", time));
        return ((time - this.getLights().lastUpdatedTime) >= Main.checkTime * this.getLights().updateTime);
    }
    
    public synchronized void simpleUpdate(double time) { 
        this.light.update(time);
    }

    public synchronized void populateCar(Car car) {
        this.carArray.add(car);
    }

    public synchronized void addCar(Car car) {
        this.carArray.add(car);
    }

    public synchronized void removeCar(Car lastCar) {
        this.carArray.remove(lastCar);
    }

    public synchronized int getOutflux() {
        this.outflux = 0; 
        if (this.getLights().isGreen()) {
            for (int i = 0; i < this.carArray.size(); i++) {
                if (i == 0) {
                    if (((this.ROAD_LENGTH - this.carArray.get(i).getY()) / this.carArray.get(i).currentSpeed) < 5) {
                        this.outflux++;
                    }
                } else {
                    if (this.carArray.get(i).topSpeed > this.carArray.get(i - 1).currentSpeed) {
                        if (((this.ROAD_LENGTH - this.carArray.get(i).getY()) / this.carArray.get(i - 1).currentSpeed) < 5) {
                            this.outflux++;
                        }
                    } else {
                        if (((this.ROAD_LENGTH - this.carArray.get(i).getY()) / this.carArray.get(i).currentSpeed) < 5) {
                            this.outflux++;
                        }
                    }
                }
            }
        } 
        return this.outflux;
    }

    public synchronized int getInflux(ArrayList<Road> segments, int index) {
        if (index == segments.size() - 1) {
            return 100;
        }
        this.influx = segments.get(index + 1).getOutflux();
        return this.influx;
    }

    public synchronized boolean densityCheck() {
        if (this.carArray.size() > (this.ROAD_LENGTH / 19) && this.influx >= (this.outflux * Main.densityCheckConstant)) {// few % larger because tiny spikes may exist
            return true;
        } else {
            return false;
        }
    }

    public synchronized Car getFrontCar() {
        return this.carArray.get(0);
    }
    
    public synchronized Car getLastCar() {
        if (this.carArray.size() > 0) {
            return this.carArray.get(this.carArray.size() - 1);
        } else {
            System.out.println("ERROR");
            return null;
        }
    }

    public synchronized int getLength() {
        return this.ROAD_LENGTH;
    }

    public synchronized Light getLights() {
        return this.light;
    }

    public synchronized ArrayList<Car> getCarArray() {
        return this.carArray;
    }
    
    public synchronized boolean isBlocked() {
        return this.blocked;
    }
    
    public synchronized void setBlocked(boolean condition) {
        this.blocked = condition;
    }
        
}
