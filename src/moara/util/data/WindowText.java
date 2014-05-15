package moara.util.data;

import moara.util.Constant;

public class WindowText {

	private int minus;
	private int plus;
	private WindowTextItem[] window;
	
	public WindowText(int minus, int plus, WindowTextItem wti) {
		this.window = new WindowTextItem[minus+plus+1];
		this.minus = minus;
		this.plus = plus;
		init(wti);
	}
	
	private void init(WindowTextItem wti) {
		for (int i=0; i<this.window.length; i++) {
			this.window[i] = wti.newObject();
		}
	}
	
	public int Minus() {
		return this.minus;
	}
	
	public int Plus() {
		return this.plus;
	}
	
	public int getPosition(int index) {
		return (index-this.minus); 
	}
	
	public int getIndex(int position) {
		if (position==0)
			return this.minus;
		else
			return position + this.minus;
	}
	
	public WindowTextItem getElement(int position) {
		return this.window[getIndex(position)];
	}
	
	public void setElement(WindowTextItem wti, int position) {
		/*WindowTextItem temp = wti.copy();
		temp.addToName(getNameByPosition(position));
		this.window[getIndex(position)] = temp;*/
		
		this.window[getIndex(position)] = wti;
		wti.addToName(getNameByPosition(position));
	}
	
	private String getNameByPosition(int position) {
		if (position<0)
			return Constant.MINUS_FEATURE+(-position);
		else if (position>0)
			return Constant.PLUS_FEATURE+position;
		else
			return "";
	}
	
	public boolean checkElements(String value) {
		for (int i=0; i<this.window.length; i++)
			if (this.window[i]!=null && this.window[i].check(value))
				return true;
		return false;
	}
	
	public void updateWindow() {
		for (int i=0; i<(this.window.length-1); i++)
			this.window[i]= this.window[i+1];
		//this.window[i]= this.window[i+1].copy();
	}
	
	public String toString() {
		String text = "minus=" + this.minus + ", plus=" + plus + "\n";
		for (int i=0; i<this.window.length; i++)
			text += this.getPosition(i) + ": " + this.window[i].toString() + "\n";
		return text;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
}
