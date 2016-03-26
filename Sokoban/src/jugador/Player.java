package jugador;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Player {

	private String id;//id unica
	private String pswd;//id unica
	private int progreso;//nivel (debe ser una lista con todos los nivles compeltados)
	public boolean estado = false;
	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;   

	public Player(String id, String pswd){
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		this.id=id;
		this.pswd=pswd;
		estado = login();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getProgreso() {
		return progreso;
	}
	public void setProgreso(int progreso) {
		this.progreso = progreso;
	}

	@SuppressWarnings({ "unchecked", "serial" })
	private boolean login(){
		collection = database.getCollection("jugadores");
		Document user = collection.find(new Document("_id",this.id)).first();
		if(user!=null)
		{
			if(user.getString("Pass").equals(this.pswd))
			{
				int tmn = ((ArrayList<Document>)user.get("Progreso")).size();
				this.progreso = (int)(((ArrayList<Document>)user.get("Progreso")).get(tmn-1).get("Nivel"));
			}else{
				client.close();
				return false;
			}
		}else{
			ArrayList<Document> progresoList = new ArrayList<Document>(){{
				add(new Document("Nivel", 1));
			}};
			collection.insertOne(new Document("_id", this.id).append("Pass", this.pswd).append("Progreso",progresoList));
			this.progreso=1;
		}
		client.close();
		return true;
	}
}
