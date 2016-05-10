package jugador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bson.Document;
import com.csvreader.CsvWriter;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Mapas {

	private static MongoClient client;
	private static MongoDatabase database;
	private static MongoCollection<Document> collection;   

	/**
	 * Genera los mapas apartir del fichero niveles.txt si estos no se encuentran en la base de datos.
	 */
	public static void generarMapas(){
		client = new MongoClient("localhost", 27017);//conectamos.
		database = client.getDatabase("sokoban");//elegimos bbdd.
		collection = database.getCollection("niveles");//elegimos la colecci�n.
		
		int id = 1;//id auxiliar del nivel.
		int cnt = 0;
		if(collection.count()==0){//no estan creados los niveles, los generamos.
			collection.drop();//limpiamos por si habia contenido.
			Path ruta;
			char[][] mapa = new char[14][20];
			try {
				ruta = Paths.get(Mapas.class.getClassLoader().getResource("levels.txt").toURI());
				Iterator<String> it = Files.lines(ruta).iterator();
				while(it.hasNext()) {
					String s = it.next();
					if(s.length()>0&&s.charAt(0)!=';'){//leemos el nivel del txt.
						for(int j = 0; j<s.length(); j++){
							mapa[cnt][j] = s.charAt(j);
						}
						cnt++;
					}else if(s.length()==0&&cnt!=0){//fin de nivel.
						ajustarMapa(mapa);
						List<List<String>> mapaAux = new ArrayList<List<String>>();
						for(int k = 0; k<mapa.length; k++){//lo pasamos a un formato reconocible por la base de datos y sobre el que trabajaremos.
							List<String> aux = new ArrayList<String>();
							for(int l = 0; l<mapa[k].length; l++){
								aux.add(String.valueOf(mapa[k][l]).replace('\u0000', ' '));
							}
							mapaAux.add(aux);
						}
						collection.insertOne(new Document("_id", id).append("Mapa", mapaAux).append("Jugada",new Document("seq", new ArrayList<String>()).append("Jugador", "").append("Time", "")));
						mapa = new char[14][20];//nuevo nivel
						id++;
						cnt=0;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		client.close();//cerramos la conexion.
	}

	/**
	 * Dado un nivel aniade columnas y filas para ajustarlo centralmente a una dimension de 14x20.
	 * @param mapa - nivel a ajustar.
	 */
	private static void ajustarMapa(char[][] mapa){
		int cntCol=0;//cuenta las columnas de la derecha que no tienen nada.
		boolean finCol = false;
		int cntFil=0;//cuenta las filas inferiores que no tienen nada.
		boolean finFil = false;

		for(int j = mapa[0].length-1; j>=0; j--){		//reordenamos columnas.
			if(!finCol){
				for(int i = 0; i<mapa.length; i++){
					if(mapa[i][j]!='\u0000'){
						finCol = true;
						break;
					}
				}
			}else{
				break;
			}
			cntCol++;
		}
		for(int k = 0; k<(cntCol-1)/2; k++){
			char[] last = getColumn(mapa, mapa[0].length-1);
			for( int l =mapa[0].length-2; l >= 0 ; l-- ){
				setColumn(mapa, getColumn(mapa, l), l+1);
			}
			setColumn(mapa, last, 0);  
		} 

		for(int jk = mapa.length-1; jk>=0; jk--){		//reordenamos filas
			if(!finFil){
				for(int ik = 0; ik<mapa[jk].length; ik++){
					if(mapa[jk][ik]!='\u0000'){
						finFil = true;
						break;
					}
				}
			}else{
				break;
			}
			cntFil++;
		}
		for(int k = 0; k<(cntFil-1)/2; k++){
			char[] last = mapa[mapa.length-1];
			for( int l =mapa.length-2; l >= 0 ; l-- ){
				mapa[l+1] = mapa[l];
			}
			mapa[0] = last;  
		} 
	}

	/**
	 * Dada una matriz de caracteres devuelve la comluna pedida.
	 * @param array - matriz.
	 * @param index - numero de columna.
	 * @return Columna en array de caracteres.
	 */
	private static char[] getColumn(char[][] array, int index){
		char[] column = new char[array[0].length];
		for(int i=0; i<array.length; i++){
			column[i] = array[i][index];
		}
		return column;
	}

	/**
	 * Dada una matriz de caracteres, una columna y una posicion, aniade la columna a la matriz en la posicion indicada.
	 * @param array - matriz a modificar.
	 * @param column - columna a aniadir.
	 * @param index - posicion.
	 */
	private static void setColumn(char[][] array, char[] column, int index){
		for(int i=0; i<array.length; i++){
			array[i][index] = column[i];
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Dada la ID de un nivel y el tipo de algoritmo ve si ya existe una solucion guardada en la BD.
	 * @param id - ID del nivel.
	 * @param tipe - Tipo de algoritmo.
	 * @return - Secuencia de caracteres de la solucion.
	 */
	public static char[] verSol(int id, String tipe){
		client = new MongoClient("localhost", 27017);//conectamos.
		database = client.getDatabase("sokoban");//elegimos bbdd.
		collection = database.getCollection("niveles");//elegimos la colecci�n.
		char seq[] = null;
		Document sol = collection.find(new Document("_id", id)).first();
		if(sol!=null){
			Document inf = (Document)sol.get(tipe);
			if(inf!=null){//hay una solucion ya guardada.
				List<Document> secuencia = (List<Document>) inf.get("seq");
				seq = new char[secuencia.size()-1];
				for(int i = 0; i<seq.length; i++){
					seq[i] = ((Document)secuencia.get(i+1)).getString("tecla").charAt(0);
				}
			}
		}
		client.close();//cerramos la conexion.
		return seq;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Metodo auxiliar que imprime por pantalla el total de tiempo, nodos y pasos del set de niveles para un algoritmo.
	 * @param tipe - algoritmo elegido.
	 */
	public static void verResultados(String tipe){
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("niveles");//elegimos la colecci�n

		double time = 0;
		long nodos = 0;
		long pasos = 0;
		for(int i = 0; i<collection.count(); i++){
			Document doc = collection.find(new Document("_id",i+1)).first();
			try{
				time = time + ((Document)doc.get(tipe)).getLong("Time");
				nodos = nodos + ((Document)doc.get(tipe)).getInteger("Nodos");
				pasos = pasos + (((List<Document>)((Document)doc.get(tipe)).get("seq")).size()-1);
			}catch(NullPointerException e){
				continue;
			}
		}
		System.out.println("Tiempo total: "+(time/(1000*60*60))+" horas");
		System.out.println("Nodos totales: "+nodos);
		System.out.println("Pasos totales: "+pasos);
		client.close();
	}


	@SuppressWarnings("unchecked")
	/**
	 * Metodo auxiliar que genera un CSV con el tiempo, nodos y pasos de cada nivel del set de niveles para un algoritmo.
	 * @param tipe - algoritmo elegido.
	 */
	public static void escribirResultados(String tipe){
		try {
			CsvWriter csvOutput = new CsvWriter(new FileWriter("."+File.separator+"niveles.csv", true), ';');
			csvOutput.write("id");
			csvOutput.write("steps");
			csvOutput.write("time");
			csvOutput.write("nodes");
			csvOutput.endRecord();
			client = new MongoClient("localhost", 27017);//conectamos.
			database = client.getDatabase("sokoban");//elegimos bbdd.
			collection = database.getCollection("niveles");//elegimos la colecci�n.
			for(int i = 0; i<collection.count(); i++){
				Document doc = collection.find(new Document("_id",i+1)).first();
				csvOutput.write(String.valueOf(doc.getInteger("_id")));
				try{
					csvOutput.write(String.valueOf(((List<Document>)((Document)doc.get(tipe)).get("seq")).size()-1));
					csvOutput.write(String.valueOf(((Document)doc.get(tipe)).getLong("Time")));
					csvOutput.write(String.valueOf(((Document)doc.get(tipe)).getInteger("Nodos")));
					csvOutput.endRecord();
				}catch(NullPointerException e){
					csvOutput.endRecord();
					continue;
				}
			}
			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
