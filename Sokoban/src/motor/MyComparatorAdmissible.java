package motor;

import java.util.Comparator;

public class MyComparatorAdmissible implements Comparator<Node> {
	@Override
	public int compare(Node a, Node b) {
			Integer aux = a.getG()+a.getH();
			Integer aux2 = b.getG()+b.getH();
			if(aux<aux2){//se ordena por coste de f(h)
				return -1;
			}else{
				return 1;
			}
	}
}
