package motor;

import java.util.Comparator;

public class MyComparatorTest implements Comparator<Node>{

	@Override
	public int compare(Node a, Node b) {
		if(a.getH()!=b.getH()){//primero los nodos con menor h(x).
			if(a.getH()<b.getH()){
				return -1;
			}else{
				return 1;
			}
		}else{// en caso de empates seguir�n aquellos con menor costo acumulado de g(x).
			if(a.getG()<b.getG()){
				return -1;
			}else{
				return 1;
			}
		}
	}
}
