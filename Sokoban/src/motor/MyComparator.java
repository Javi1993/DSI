package motor;

import java.util.Comparator;

public class MyComparator implements Comparator<Node>{
	
	public int compare(Node a, Node b) {
		if(a.getI()!=b.getI())
		{//primero los nodos con más cajas acomodadas correctamente i(n)
			if(a.getI()>b.getI())
			{
				return -1;
			}else{
				return 1;
			}

		}else
		{// en caso de empates seguirán aquellos con menor costo acumulado de g(n) y h(n)
			Integer aux = a.getG()+a.getH();
			Integer aux2 = b.getG()+b.getH();
			if(aux<aux2){
				return -1;
			}else{
				return 1;
			}
		}
	}
}
