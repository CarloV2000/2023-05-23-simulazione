package it.polito.tdp.baseball.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.baseball.db.BaseballDAO;

public class Model {
	private Graph<People, DefaultEdge>grafo;
	private List<People>allPeople;
	private BaseballDAO dao;
	private Map<String, People>idMap;
	
	private Map<People, Double> salariesIDMap;
	private Map<People, List<Team>> playerTeamsMap;
	private List<People>dreamTeam;
	private Double salarioMaggiore;
	
	public Model() {
		this.allPeople = new ArrayList<>();
		this.dao = new BaseballDAO();
		this.idMap = new HashMap<>();		
	}
	
	public String creaGrafo(int anno, int salaryMINMLN) {
		int salaryMIN = salaryMINMLN*1000000;
		
		this.grafo = new SimpleGraph<People, DefaultEdge>(DefaultEdge.class);
		
		this.allPeople = dao.readAllPlayersDatoAnnoESalario(anno, salaryMIN);
		Graphs.addAllVertices(grafo, this.allPeople);
		
		for(People x : this.allPeople) {
			this.idMap.put(x.getPlayerID(), x);
		}
		
		List<CoppiaA>allCoppie = new ArrayList<>(dao.getAllCoppie(idMap, anno));
		for(CoppiaA x: allCoppie) {
			grafo.addEdge(x.getP1(), x.getP2());
		}
		
		return ("Grafo creato con "+grafo.vertexSet().size()+" vertici e "+grafo.edgeSet().size()+" archi");
	}
	
	public People getVerticeGradoMassimo(int anno) {
		int gradoMAX = 0;
		People verticeGradoMAX = null;
		for(People x : grafo.vertexSet()) {
			if(this.calcolaGrado(x, anno) > gradoMAX) {
				gradoMAX = this.calcolaGrado(x, anno);
				verticeGradoMAX = x;
			}
		}
		return verticeGradoMAX;
	}
	
	public int calcolaGrado(People p, int anno) {
		int n = 0;
		for(CoppiaA x : dao.getAllCoppie(idMap, anno)) {
			if(x.getP1().equals(p)) {
				n++;
			}
		}
		return n;
	}
	
	public int getNumberOfConnectedComponents(){
		int nComponentiConnesse = 0;
		ConnectivityInspector<People, DefaultEdge> inspector = new ConnectivityInspector<>(this.grafo);
                  List<Set<People>> connectedComponents = inspector.connectedSets();
                  for (Set<People> component : connectedComponents) {
                 	    nComponentiConnesse++;
	         }
        
        return nComponentiConnesse;
    }
	
	/**
	 * Dato il grafo costruito al punto precedente, si vuole definire un dream team costituito dal più grande numero
	 * di giocatori che non ha giocato nella stessa squadra per l’anno selezionato. La direzione non bada a spese, ed
	 * ha deciso di investire tutto il denaro necessario per assicurarsi i giocatori migliori.
	 *	 Due giocatori possono far parte del dream team se non hanno fatto parte della stessa squadra per l’anno
	 *  	in corso (anche per parte di esso).
	 *   La squadra selezionata dovrà selezionare, fra le varie soluzioni possibili, quella con salario cumulativo più alto.
	 */
	/**
	 * Metodo che calcola il Dream Team
	 */
	public void  calcolaDreamTeam() {
		this.salarioMaggiore = 0.0;
		this.dreamTeam = new ArrayList<People>();
		List<People> rimanenti = new ArrayList<People>(this.grafo.vertexSet());
		
		/*
		 * Questo check non era richiesto nel testo, ma servo ad escludere dal calcolo
		 * del dream team i giocatori che non hanno mai giocato nell'anno (ad esempio perché infortunati).
		 * Per come è stato costruito il grafo questi sono dei vertici isolati.
		 */
		
		List<People> playersInattivi = new ArrayList<People>(this.grafo.vertexSet());
		for (People p : rimanenti) {
			if (!this.playerTeamsMap.get(p).isEmpty()){
				playersInattivi.remove(p);
			}
		}
		rimanenti.removeAll(playersInattivi);
		
		ricorsione(new ArrayList<People>(), rimanenti);
	}
	
	
	
	/**
	 * La ricorsione vera e propria
	 * @param parziale
	 * @param rimanenti
	 */
	private void ricorsione(List<People> parziale, List<People> rimanenti){
		/*
		 * L'idea della ricorsione è di prendere un giocatore, metterlo nella lista parziale,
		 * e rimuovere tutti i suoi compagni di squadra (trovati come i suoi vicini) dalla lista di giocatori rimanenti.
		 * Dopodichè, ripetiamo la ricorsione, usando parziale ed il nuovo insieme ridotto di giocatori rimanenti,
		 * fino a che non li finiamo.
		 */
		// Condizione Terminale
		if (rimanenti.isEmpty()) {
			//calcolo costo
			double salario = getSalarioTeam(parziale);
			if (salario>this.salarioMaggiore) {
				this.salarioMaggiore = salario;
				this.dreamTeam = new ArrayList<People>(parziale);
			}
			return;
		}
		
		/*
		 * VERSIONE NON OTTIMIZZATA DELLA RICORSIONE
		 */
		/*
		 * Questa versione riguarda le stesse combinazioni di giocatori più volte, e richiede mooolto tempo.
		 * Riesce a terminare in tempi acettabili solo su grafi molto piccoli, con meno di 10 vertici. La versione 
		 * ottimizzata di sotto riesce a gestire velocemente anche grafi con 40-50 vertici.
		 */
//		for (People p : rimanenti) {
//			List<People> currentRimanenti = new ArrayList<>(rimanenti);
//				parziale.add(p);
//				currentRimanenti.removeAll(Graphs.neighborListOf(this.grafo, p));
//				currentRimanenti.remove(p);
//				ricorsione(parziale, currentRimanenti);
//				parziale.remove(parziale.size()-1);
//		}
		
		
		/*
		 * VERSIONE OTTIMIZZATA DELLA RICORSIONE
		 */
		/*
		 * Rispetto alla versione non ottimizzata, qui l'idea è di ciclare su una squadra alla volta
		 * piuttosto che su tutti i vertici del grafo, rimuovendo così molti casi.
		 * Per selezionare una squadra potremmo prendere un vertice, e poi prendere tutti i suoi vicini.
		 * Però alcuni di questi vertici (giocatori) potrebbero aver giocato per 2 squadre in un anno,
		 * perciò se selezionassimo i suoi vicini prenderemmo due squadre invece di una.
		 * Per evitare questo problema, andiamo prima a prendere un vertice qualsiasi, con tutti i suoi vicini.
		 * Poi, tra questi prendiamo un vertice di grado minimo, e andiamo a calcolare i suoi vicini.
		 * L'alternativa sarebbe di fare, nel metodo calcolaDreamTeam(), un sort dei vertici in 'rimanente' in ordine crescente del loro grado
		 * e poi selezionare sempre il primo.
		 */
		List<People> squadra =  Graphs.neighborListOf(this.grafo, rimanenti.get(0));
		squadra.add( rimanenti.get(0));
		People startP = minDegreeVertex(squadra);
		List<People> squadraMin =  Graphs.neighborListOf(this.grafo, rimanenti.get(0));
		squadraMin.add( rimanenti.get(0));
		
		for (People p : squadraMin) {
			List<People> currentRimanenti = new ArrayList<>(rimanenti);
			parziale.add(p);
			currentRimanenti.removeAll(squadraMin);
			ricorsione(parziale, currentRimanenti);
			parziale.remove(parziale.size()-1);
		}
	}
	
	
	/**
	 * Metodo che calcola il salario nell'anno di una lista di giocatori
	 * Usato nella ricorsione, per calcolare il salario del Dream Team
	 * @param team
	 * @return
	 */
	private double getSalarioTeam(List<People> team) {
		double result = 0.0;
		for (People p : team) {
			result += this.salariesIDMap.get(p);
		}
		return result;
	}
	
	
	/**
	 * Metodo per calcolare il vertice di grado minimo tra un insieme di vertici
	 * @param squadra
	 * @return
	 */
	private People minDegreeVertex(List<People> squadra) {
		People res = null;
		int gradoMin = -1;
		for (People p : squadra) {
			int grado = Graphs.neighborListOf(this.grafo, p).size();
			if (gradoMin==-1 || grado<gradoMin) {
				res = p;
			}
		}		
		return res;
	}

	public Graph<People, DefaultEdge> getGrafo() {
		return grafo;
	}

	public void setGrafo(Graph<People, DefaultEdge> grafo) {
		this.grafo = grafo;
	}

	public List<People> getAllPeople() {
		return allPeople;
	}

	public void setAllPeople(List<People> allPeople) {
		this.allPeople = allPeople;
	}

	public List<People> getDreamTeam() {
		return dreamTeam;
	}

	public void setDreamTeam(List<People> dreamTeam) {
		this.dreamTeam = dreamTeam;
	}
	
	
}