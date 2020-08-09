//Create a data structure that efficiently supports the stack operations (push and pop) and also 
//a return-the-maximum operation. Assume the elements are real numbers so that you can compare 
//them.

Double max = null;

Stack<Double> s = new Stack<Double>();

public void pushMax(Double d){
	s.push(d);
	if (max < s.peek()){
		max = d;
	}
}
	
public Double popMax(){
	Stack<Double> t = new Stack<Double>();
	Double currentMax = max;
	max = null;
	while(s != null){
		if(s.peek() == currentMax){
			s.pop();
		}
		else{
			if(max < s.peek()){
				max = s.pop();
			}
			t.push(s.pop())
		}
	}
	while(t != null){
		s.push(t.pop());
	}
	return currentMax;
}

public Double peekMax(){
	return max;
	}
