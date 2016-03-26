package jugador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Mapas {

	private static MongoClient client;
	private static MongoDatabase database;
	private static MongoCollection<Document> collection;   

	public static void generarMapas(){

		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("niveles");//elegimos la colecci�n

		int id = 1;//id deñ nivel
		int cnt = 0;
		if(collection.count()==0 || collection.count()==601){//no estan creados los niveles, los generamos
			collection.drop();//limpiamos por si habia contenido
			Path ruta;
			char[][] mapa = new char[11][20];
			for(int i=1; i<=new File("."+File.separator+"niveles"+File.separator).listFiles().length; i++)
			{
				try {
					ruta = Paths.get("."+File.separator+"niveles"+File.separator+"level"+String.valueOf(i)+".txt");
					Iterator<String> it = Files.lines(ruta).iterator();
					while(it.hasNext()) {
						String s = it.next();
						if(s.length()>0&&s.charAt(0)!=';')
						{//leemos el nivel del txt
							for(int j = 0; j<s.length(); j++)
							{
								mapa[cnt][j] = s.charAt(j);
							}
							cnt++;
						}
						else if(s.length()==0&&cnt!=0)
						{//fin de nivel
							ajustarMapa(mapa);
							List<List<String>> mapaAux = new ArrayList<List<String>>();
							for(int k = 0; k<mapa.length; k++)
							{//lo pasamos a un formato reconocible por la base de datos y sobre el que trabajaremos
								List<String> aux = new ArrayList<String>();
								for(int l = 0; l<mapa[k].length; l++)
								{
									aux.add(String.valueOf(mapa[k][l]).replace('\u0000', ' '));
								}
								mapaAux.add(aux);
							}
							collection.insertOne(new Document("_id", id).append("Mapa", mapaAux).append("Jugada",new Document("seq", new ArrayList<String>()).append("Jugador", "")));
							mapa = new char[11][20];//nuevo nivel
							id++;
							cnt=0;
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		client.close();
	}

	private static void ajustarMapa(char[][] mapa){
		int cntCol=0;//cuenta las columnas de la derecha que no tienen nada
		boolean finCol = false;
		int cntFil=0;//cuenta las filas inferiores que no tienen nada
		boolean finFil = false;

		//reordenamos columnas
		for(int j = mapa[0].length-1; j>=0; j--){
			if(!finCol){
				for(int i = 0; i<mapa.length; i++)
				{

					if(mapa[i][j]!='\u0000')
					{
						finCol = true;
						break;
					}
				}
			}else{
				break;
			}
			cntCol++;
		}

		for(int k = 0; k<(cntCol-1)/2; k++)
		{
			char[] last = getColumn(mapa, mapa[0].length-1);
			for( int l =mapa[0].length-2; l >= 0 ; l-- )
			{
				setColumn(mapa, getColumn(mapa, l), l+1);
			}
			setColumn(mapa, last, 0);  
		} 

		//reordenamos filas
		for(int jk = mapa.length-1; jk>=0; jk--){
			if(!finFil){
				for(int ik = 0; ik<mapa[jk].length; ik++)
				{

					if(mapa[jk][ik]!='\u0000')
					{
						finFil = true;
						break;
					}
				}
			}else{
				break;
			}
			cntFil++;
		}

		for(int k = 0; k<(cntFil-1)/2; k++)
		{
			char[] last = mapa[mapa.length-1];
			for( int l =mapa.length-2; l >= 0 ; l-- )
			{
				mapa[l+1] = mapa[l];
			}
			mapa[0] = last;  
		} 
	}

	private static char[] getColumn(char[][] array, int index){
		char[] column = new char[array[0].length]; // Here I assume a rectangular 2D array! 
	    for(int i=0; i<array.length; i++){
	       column[i] = array[i][index];
	    }
	    return column;
	}

	private static void setColumn(char[][] array, char[] column, int index){
		for(int i=0; i<array.length; i++){
			array[i][index] = column[i];
		}
	}
}
