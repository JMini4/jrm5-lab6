/* Julia Mini
 * Lab: Thursday Afternoon
 * Lab 6
 *
 * Questions:
 * 
 * 10.3) Using only stack operations, the way to copy a stack is by popping off
 * each value of the stack and pushing it to the dummy stack which will have the
 * reverse order. Then to make the copied stack and restore the original stack you
 * pop each value off of the dummy stack and push the value onto both the new copy
 * copy of the stack and also the original. Therefore, two additional stacks are
 * necessary for copying a stack and maintaining order.
 * 
 * 10.4) Using only stack operations, the way to reverse a stack and place it in 
 * the original stack is by first popping and pushing each element to the first
 * dummy stack (this stack is not reveresed). Then popping each value and pushing 
 * them onto a second dummy stack (this stack has the original order). Then popping
 * and oushing each value from dummy stack 2 onto the original stack will reverse
 * the elements fo the stack and put it back in the original stack. This operation
 * requires two additional stacks.
 * 
 * 10.5) Using only queue operations, the way to copy a queue is by using the 
 * dequeue method to remove the first element, then add that element to both a 
 * a new queue and also to the back of the original queue. You would repeat this
 * operation only as many times as the size of the queue so that the element that
 * was originally first and then gotmoved to the back reemerges as the front of
 * the queue.
 */

import structure5.*;
import java.util.Iterator;

// a postscript calculator which takes an object reader and interprets the results
// using basic mathematic methods 
public class Interpreter {

    Stack <Token> valueStack = new StackList <Token>();
    SymbolTable symbolTable = new SymbolTable();

    public static void main(String args[]){
	Interpreter interpreter = new Interpreter();
	interpreter.interpret(new Reader());
    }
    
    public void interpret(Reader reader){
	while (reader.hasNext()){
	    Token t = reader.next();
      	    if (t.isSymbol() && !t.getSymbol().startsWith("/")){
		if(t.getSymbol().equals("add")){
		    add();
		} else if (t.getSymbol().equals("sub")){
		    sub();
		} else if (t.getSymbol().equals("mul")){
		    mul();
		} else if (t.getSymbol().equals("div")){
		    div();
		} else if (t.getSymbol().equals("dup")){
		    dup();
		} else if (t.getSymbol().equals("exch")){
		    exch();
		} else if (t.getSymbol().equals("eq")){
		    eq();
		} else if (t.getSymbol().equals("ne")){
		    ne();
		} else if (t.getSymbol().equals("def")){
		    def();
		} else if (t.getSymbol().equals("pop")){
		    pop();
		} else if (t.getSymbol().equals("quit")){
		    break;
	        } else if (t.getSymbol().equals("pstack")){
		    pstack();
		} else if (t.getSymbol().equals("ptable")) {
		    ptable();
		// handles the if operator 
		} else if (t.getSymbol().equals("if")){
		    Token tokenExec = valueStack.pop();
		    Token tokenCond = valueStack.pop();
		    // checks if token is a boolean
		    if (tokenCond.getBoolean()){
			Reader newReader;
			// runs procedure
			if(tokenExec.isProcedure()){
			    newReader = new Reader(tokenExec.getProcedure());
			} else{
			    newReader = new Reader(tokenExec);
			}
			interpret(newReader);
		    }
		    // looks up symbol in symbolTable
		} else if (symbolTable.contains(t.getSymbol())){
		    Token s = symbolTable.get(t.getSymbol());
		    // runs procedure
		    if (s.isProcedure()){
			Reader newReader = new Reader(s.getProcedure());
			interpret(newReader);
		    } else { 
			valueStack.push(s);
		    }
		} else {  
		    Assert.fail("invaid input");
		} else { 
		valueStack.push(t);
	    }
	}
    }

    // prints stack
    public void pstack(){
	Iterator <Token> iter = valueStack.iterator();
	System.out.println("here is the current stack:");
	while(iter.hasNext()){
	    System.out.println(iter.next());
	}		
    }

   
    public void add(){
	arithmeticConditions(false);
	double result = valueStack.pop().getNumber() + valueStack.pop().getNumber();
	valueStack.push(new Token(result));
    }

    public void sub(){
	arithmeticConditions(false);
	double t = valueStack.pop().getNumber();
	double s = valueStack.pop().getNumber();
       	valueStack.push(new Token(s - t));
    }

    public void mul(){
	arithmeticConditions(false);
	double result = valueStack.pop().getNumber() * valueStack.pop().getNumber();
        valueStack.push(new Token(result));
    }

    public void div(){
	arithmeticConditions(true);
	double t = valueStack.pop().getNumber();
	double s = valueStack.pop().getNumber();
	valueStack.push(new Token(s / t));
    }

    // helper function for conditions necessary for +, -, /, *
    public void arithmeticConditions(boolean isDiv){
	Assert.condition(valueStack.size() >= 2, "Stack has at least 2 tokens");
	Assert.condition(valueStack.peek().isNumber(), "First token is number");
	Token t = valueStack.pop();
	Assert.condition(valueStack.peek().isNumber(), "Second token is number");
	if (isDiv){
	    Assert.condition(valueStack.peek().getNumber()!= 0,
			     "Denominator is not zero");
	}
	valueStack.push(t);
    }

    // pre: valueStack.size() >= 1
    // post: duplicates the top token of the stack
    public void dup(){
	Assert.condition(valueStack.size() >= 1, "Stack has at least 1 token");
	valueStack.push(valueStack.peek());
    }

    // pre: valueStack.size() >= 2
    // post: switches the top two tokens in the stack
    public void exch(){
	Assert.condition(valueStack.size() >= 2, "Stack has at least 2 tokens");
	Token t = valueStack.pop();
	Token s = valueStack.pop();
	valueStack.push(t);
	valueStack.push(s);
    }

    // pre: valueStack.size() >= 2
    // post: checks that top two tokens are equal
    public void eq(){
	Assert.condition(valueStack.size() >= 2, "Stack has at least 2 tokens");
	Token t = valueStack.pop();
	Token s = valueStack.pop();
	valueStack.push(new Token(t.equals(s)));
    }

    
    // pre: valueStack.size() >= 2
    // post: checks that top two tokens are not equal
    public void ne(){
	Assert.condition(valueStack.size() >= 2, "Stack has at least 2 tokens");
	Token t = valueStack.pop();
	Token s = valueStack.pop();
	valueStack.push(new Token(!t.equals(s)));
    }
	

    // pre: valueStack.size() >= 2
    // pre: second token starts with a "/"
    // post: adds symbol and definition to symbolTable
    public void def(){
      	Assert.condition(valueStack.size() >= 2, "Stack has at least 2 tokens");
	Token def = valueStack.pop();
	Assert.condition(valueStack.peek().getSymbol().startsWith("/"),
                         "Token is a new symbol");
	symbolTable.add(valueStack.pop().getSymbol().substring(1), def);
	}


    // pre: valueStack.size() >= 1
    // post: removes top token from stack
    public void pop(){
	Assert.condition(valueStack.size() >= 1, "Stack has at least 1 token");
	valueStack.pop();
    }
    
    // prints symbol table
    public void ptable(){
	System.out.println(symbolTable);
    }

}