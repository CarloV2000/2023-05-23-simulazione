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
	
	private List<People>dreamTeam;
	private double salarioMaggiore;
	
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
	public List<People>calcolaDreamTeam(int anno) {
		List<People>parziale = new ArrayList<>();
		cerca(parziale, 0, anno );
		return this.dreamTeam;
	}
	public void cerca(List<People>parziale, int livello, int anno) {
		
		if(getSalarioTeam(parziale)> this.salarioMaggiore) {//se il salario tot è maggiore: parziale diventa migliore
			this.salarioMaggiore = getSalarioTeam(parziale);
			this.dreamTeam = new ArrayList<>(parziale);
		}
		
		List<String>codiciSquadreDreamTeam = new ArrayList<>();
		for(People x : grafo.vertexSet()) {
			List<String> squadre = dao.readTeamDelPlayerNellAnno(x, anno);
			
				if(!codiciSquadreDreamTeam.contains(squadre.get(0)) && !codiciSquadreDreamTeam.contains(squadre.get(1))) {//qui posso avere soluzioni
					codiciSquadreDreamTeam.add(squadre.get(0));
					if(squadre.get(1)!=null) {
						codiciSquadreDreamTeam.add(squadre.get(1));
					}
					parziale.add(x);
					cerca(parziale, livello+1, anno);
					parziale.remove(parziale.size()-1);
				}
			
		}
	}
	public Double getSalarioTeam(List<People>parziale) {
		Double salarioTOT = 0.0;
		for(People x : parziale) {
			salarioTOT += x.getSalario();
		}
		return salarioTOT;
	}
	
	
}