//Implement a queue with two stacks so that each queue operations takes a 
//constant amortized number of stack operations.

Stack<Integer> enq = new Stack<Integer>();
Stack<Integer> deq = new Stack<Integer>();

public Integer dequeue(){
	while(enq != null){
		deq.push(enq.pop());
		}
	Integer d = deq.pop();

	while(deq != null){
		enq.push(deq.pop());
		}
	return d;
	}
	
public void enqueue(Integer v){
	enqueue.push(v);
	}