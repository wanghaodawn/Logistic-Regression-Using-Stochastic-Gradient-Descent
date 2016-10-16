/**
 * 10605 - HW3
 * Entry for hashmap
 * @author Hao Wang (haow2)
 * */
public class Entry {
	private double value;
	private int lastTimeModified;
	
	public Entry() {
		this.value = 0.0;
		this.lastTimeModified = 0;
	}
	
	public Entry(double value, int lastTimeModified) {
		this.value = value;
		this.lastTimeModified = lastTimeModified;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public int getLastTimeModified() {
		return this.lastTimeModified;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public void setLastTimeModified(int lastTimeModified) {
		this.lastTimeModified = lastTimeModified;
	}
}
