package jugador;

import java.util.ArrayList;
import java.util.List;

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

	@SuppressWarnings("unchecked")
	public void updatePlayer(List<String> secuencia)
	{
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("jugadores");
		if(secuencia!=null){
			Document player = collection.find(new Document("_id", this.getId())).first();
			List<String> pasosAnt =((List<String>)player.get("Progreso."+(this.getProgreso()-1)+".Jugada"));
			if(pasosAnt==null||secuencia.size()<pasosAnt.size())
			{
				collection.updateOne(new Document("_id", this.getId()), new Document("$set", new Document("Progreso."+(this.getProgreso()-1)+".Jugada",secuencia)));
			}
		}
		updateProgreso();
		client.close();
	}

	private void updateProgreso()
	{
		this.setProgreso(this.getProgreso()+1);
		collection.updateOne(new Document("_id", this.getId()), new Document("$set", new Document("Progreso."+(this.getProgreso()-1)+".Nivel",(this.getProgreso()))));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	private boolean login(){
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
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
