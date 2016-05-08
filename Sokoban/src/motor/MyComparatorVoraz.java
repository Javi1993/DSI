package motor;

import java.util.Comparator;

public class MyComparatorVoraz implements Comparator<Node>{

	@Override
	public int compare(Node a, Node b) {
		if(a.getI()>b.getI()){//comparamos las cajas colocadas i(x).
			return -1;
		}else{
			return 1;
		}
	}
}
