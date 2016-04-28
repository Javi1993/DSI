package jugador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	public static void generarMapas(){

		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("niveles");//elegimos la colecci�n

		int id = 1;//id deñ nivel
		int cnt = 0;
		if(collection.count()==0){//no estan creados los niveles, los generamos
			collection.drop();//limpiamos por si habia contenido
			Path ruta;
			char[][] mapa = new char[14][20];
			try {
				ruta = Paths.get("."+File.separator+"niveles"+File.separator+"levels.txt");
				Iterator<String> it = Files.lines(ruta).iterator();
				while(it.hasNext()) {
					String s = it.next();
					if(s.length()>0&&s.charAt(0)!=';'){//leemos el nivel del txt
						for(int j = 0; j<s.length(); j++){
							mapa[cnt][j] = s.charAt(j);
						}
						cnt++;
					}else if(s.length()==0&&cnt!=0){//fin de nivel
						ajustarMapa(mapa);
						List<List<String>> mapaAux = new ArrayList<List<String>>();
						for(int k = 0; k<mapa.length; k++){//lo pasamos a un formato reconocible por la base de datos y sobre el que trabajaremos
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		client.close();
	}

	//	public static void repetidos()
	//	{
	//		Path ruta;
	//		char[][] mapa = new char[14][20];
	//		int cnt = 0;
	//		List<char[][]> test = new ArrayList<char[][]>();
	//		try {
	//			ruta = Paths.get("."+File.separator+"niveles"+File.separator+"levels.txt");
	//			Iterator<String> it = Files.lines(ruta).iterator();
	//			while(it.hasNext()) {
	//				String s = it.next();
	//				if(s.length()>0&&s.charAt(0)!=';')
	//				{//leemos el nivel del txt
	//					for(int j = 0; j<s.length(); j++)
	//					{
	//						mapa[cnt][j] = s.charAt(j);
	//					}
	//					cnt++;
	//				}else if(s.length()==0&&cnt!=0)
	//				{//fin de nivel
	//					test.add(mapa);
	//					mapa = new char[14][20];//nuevo nivel
	//					cnt=0;
	//				}
	//			}
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		for (int i = 0; i < test.size(); i++) {
	//			for (int j = 0; j < test.size(); j++) {
	//				if(i!=j)
	//				{
	//					if(Arrays.deepEquals(test.get(i), test.get(j)))
	//					{
	//						System.out.println((i+1)+" es igual a "+(j+1));
	//					}
	//				}
	//			}
	//		}
	//	}

	private static void ajustarMapa(char[][] mapa){
		int cntCol=0;//cuenta las columnas de la derecha que no tienen nada
		boolean finCol = false;
		int cntFil=0;//cuenta las filas inferiores que no tienen nada
		boolean finFil = false;

		//reordenamos columnas
		for(int j = mapa[0].length-1; j>=0; j--){
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

		//reordenamos filas
		for(int jk = mapa.length-1; jk>=0; jk--){
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

	@SuppressWarnings("unchecked")
	public static char[] verSol(int id, String tipe)
	{
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("niveles");//elegimos la colecci�n
		char seq[] = null;
		Document sol = collection.find(new Document("_id", id)).first();
		if(sol!=null){
			Document inf = (Document)sol.get(tipe);
			if(inf!=null){//hay una solucion ya guardada	
				List<Document> secuencia = (List<Document>) inf.get("seq");
				seq = new char[secuencia.size()-1];
				for(int i = 0; i<seq.length; i++){
					seq[i] = ((Document)secuencia.get(i+1)).getString("tecla").charAt(0);
				}
			}
		}
		client.close();
		return seq;
	}

	@SuppressWarnings("unchecked")
	public static void verResultados(){
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("niveles");//elegimos la colecci�n

		double time = 0;
		long nodos = 0;
		long pasos = 0;
		for(int i = 0; i<collection.count(); i++){
			Document doc = collection.find(new Document("_id",i+1)).first();
			time = time + ((Document)doc.get("AStar")).getLong("Time");
			nodos = nodos + ((Document)doc.get("AStar")).getInteger("Nodos");
			pasos = pasos + (((List<Document>)((Document)doc.get("AStar")).get("seq")).size()-1);
		}
		System.out.println("Tiempo total: "+(time/(1000*60*60))+" horas");
		System.out.println("Nodos totales: "+nodos);
		System.out.println("Pasos totales: "+pasos);
		client.close();
	}

	@SuppressWarnings("unchecked")
	public static void escribirResultados(){
		try {
			CsvWriter csvOutput = new CsvWriter(new FileWriter("."+File.separator+"niveles.csv", true), ',');
			csvOutput.write("id");
			csvOutput.write("steps");
			csvOutput.write("time");
			csvOutput.write("nodes");
			csvOutput.endRecord();
			client = new MongoClient("localhost", 27017);//conectamos
			database = client.getDatabase("sokoban");//elegimos bbdd
			collection = database.getCollection("niveles");//elegimos la colecci�n
			for(int i = 0; i<collection.count(); i++){
				Document doc = collection.find(new Document("_id",i+1)).first();
				csvOutput.write(String.valueOf(doc.getInteger("_id")));
				csvOutput.write(String.valueOf(((List<Document>)((Document)doc.get("AStar")).get("seq")).size()-1));
				csvOutput.write(String.valueOf(((Document)doc.get("AStar")).getLong("Time")));
				csvOutput.write(String.valueOf(((Document)doc.get("AStar")).getInteger("Nodos")));
				csvOutput.endRecord();
			}
			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
