package moara.util.corpora;

import java.util.Stack;

public class PennTreebankItem implements Cloneable {

	private Stack<PennTreebankItemNode> nodes;
	
	public PennTreebankItem() {
		this.nodes = new Stack<PennTreebankItemNode>();
	}
	
	public Stack<PennTreebankItemNode> Nodes() {
		return this.nodes;
	}
	
	public void push(PennTreebankItemNode node) {
		this.nodes.push(node);
	}
	
	public void pop() {
		this.nodes.pop();
	}
	
	public PennTreebankItemNode peek() {
		return this.nodes.peek();
	}
	
	public PennTreebankItem copy() {
		PennTreebankItem temp = new PennTreebankItem();
		for (int i=0; i<this.nodes.size(); i++) {
			PennTreebankItemNode tempNode = this.nodes.elementAt(i);
			PennTreebankItemNode node = new PennTreebankItemNode(tempNode);
			temp.push(node);
		}
		return temp;
	}
	
	public String toString() {
		String text = "stack=" + this.nodes.size() + " ";
		for (int i=0; i<this.nodes.size(); i++)
			text += this.nodes.elementAt(i).toString() + " ";
		return text;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
}
