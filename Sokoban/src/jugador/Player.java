package jugador;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import interfaz.Escenario;
import motor.Node;

public class Player {

	private String id;//id unica
	private String pswd;//id unica
	private int progreso;//nivel (debe ser una lista con todos los nivles compeltados)
	public boolean estado = false;
	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;   

	public Player(String id, String pswd){
		this.setId(id);
		this.pswd=pswd;
		estado = login();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		String aux = null;
		try{
			aux = id.toLowerCase();
			aux = Character.toString(aux.charAt(0)).toUpperCase()+aux.substring(1);
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.id = aux;
	}
	public int getProgreso() {
		return progreso;
	}
	public void setProgreso(int progreso) {
		this.progreso = progreso;
	}

	@SuppressWarnings("unchecked")
	public void updatePlayer(List<String> secuencia, Escenario escenario)
	{
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("jugadores");
		if(secuencia!=null){
			Document player = collection.find(new Document("_id", this.getId())).first();
			List<Document> pasosAnt =((List<Document>)player.get("Progreso."+(this.getProgreso()-1)+".Jugada"));
			if(pasosAnt==null||secuencia.size()<pasosAnt.size())
			{
				List<Document> seq = new ArrayList<Document>();//lista que guarda las teclas, heuristica y su mapa
				Escenario test = new Escenario(escenario.getNivel(), false);
				Node aux = new Node(test, 0, test.placedBox(), "");
				seq.add(new Document("mapa", test.charArrayToList()).append("heuristica", aux.getF()));
				for (String c : secuencia) {
					test.realizarMovimiento(c.charAt(0));
					aux = new Node(test, aux.getG()+1, test.placedBox(), aux.getID()+c);
					seq.add(new Document("tecla", c).append("mapa", test.charArrayToList()).append("heuristica", aux.getF()));
				}
				collection.updateOne(new Document("_id", this.getId()), new Document("$set", new Document("Progreso."+(this.getProgreso()-1)+".Jugada", seq)));
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
		if(this.getId().equals("IA")
				||this.getId()==null||this.getId().equals("")
				||this.pswd==null||this.pswd.trim().equals("")){//nadie puede usar este nombre o es invlaido
			return false;
		}
		Document user = collection.find(new Document("_id",this.id)).first();
		if(user!=null){
			if(user.getString("Pass").equals(this.pswd)){
				int tmn = ((ArrayList<Document>)user.get("Progreso")).size();
				this.progreso = (int)(((ArrayList<Document>)user.get("Progreso")).get(tmn-1).get("Nivel"));
				if(this.progreso>150){this.progreso=150;}
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
