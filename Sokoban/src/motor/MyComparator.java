package motor;

import java.util.Comparator;

public class MyComparator implements Comparator<Node>{
	
	public int compare(Node a, Node b) {
		if(a.getI().compareTo(b.getI())!=0)
		{//primero los nodos con más cajas acomodadas correctamente i(n)
			return (-1)*(a.getI().compareTo(b.getI()));

		}else
		{// en caso de empates seguirán aquellos con menor costo acumulado de g(n) y h(n)
			Integer aux = a.getG()+a.getH();
			Integer aux2 = b.getG()+b.getH();
			return aux.compareTo(aux2);
		}
	}
}
