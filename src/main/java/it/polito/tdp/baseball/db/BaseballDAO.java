package it.polito.tdp.baseball.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.baseball.model.Appearances;
import it.polito.tdp.baseball.model.CoppiaA;
import it.polito.tdp.baseball.model.People;
import it.polito.tdp.baseball.model.Team;


public class BaseballDAO {
	
	public List<People> readAllPlayers(){
		String sql = "SELECT * "
				+ "FROM people";
		List<People> result = new ArrayList<People>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new People(rs.getString("playerID"), 
						rs.getString("birthCountry"), 
						rs.getString("birthCity"), 
						rs.getString("deathCountry"), 
						rs.getString("deathCity"),
						rs.getString("nameFirst"), 
						rs.getString("nameLast"), 
						rs.getInt("weight"), 
						rs.getInt("height"), 
						rs.getString("bats"), 
						rs.getString("throws"),
						getBirthDate(rs), 
						getDebutDate(rs), 
						getFinalGameDate(rs), 
						getDeathDate(rs)) );
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	public List<Team> readAllTeams(){
		String sql = "SELECT * "
				+ "FROM  teams";
		List<Team> result = new ArrayList<Team>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Team( rs.getInt("iD"),
						rs.getInt("year"), 
						rs.getString("teamCode"), 
						rs.getString("divID"), 
						rs.getInt("div_ID"), 
						rs.getInt("teamRank"),
						rs.getInt("games"), 
						rs.getInt("gamesHome"), 
						rs.getInt("wins"), 
						rs.getInt("losses"), 
						rs.getString("divisionWinnner"), 
						rs.getString("leagueWinner"),
						rs.getString("worldSeriesWinnner"), 
						rs.getInt("runs"), 
						rs.getInt("hits"), 
						rs.getInt("homeruns"), 
						rs.getInt("stolenBases"),
						rs.getInt("hitsAllowed"), 
						rs.getInt("homerunsAllowed"), 
						rs.getString("name"), 
						rs.getString("park")  ) );
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<People> readAllPlayersDatoAnnoESalario(int anno, int salaryMIN) {
		String sql = "SELECT p.*, s.salary "
				+ "FROM people p, salaries s "
				+ "WHERE p.playerID = s.playerID "
				+ "AND s.year = ? "
				+ "AND s.salary > ? ";
		List<People> result = new ArrayList<People>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, salaryMIN);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				People p = new People(rs.getString("playerID"), 
						rs.getString("birthCountry"), 
						rs.getString("birthCity"), 
						rs.getString("deathCountry"), 
						rs.getString("deathCity"),
						rs.getString("nameFirst"), 
						rs.getString("nameLast"), 
						rs.getInt("weight"), 
						rs.getInt("height"), 
						rs.getString("bats"), 
						rs.getString("throws"),
						getBirthDate(rs), 
						getDebutDate(rs), 
						getFinalGameDate(rs), 
						getDeathDate(rs));
				result.add(p);
				double salario = rs.getDouble("s.salary");
				p.setSalario(salario);
				
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
/**
 * in questo metodo prendo tutte le coppie di giocatori che hanno giocato nella stessa squadra nell'anno indicato
 * tra tutti questi: filtro tramite idMap (prendendo solo i giocatori che sono Vertici del grafo) e creo gli archi
 * @param idMap
 * @param anno
 * @return
 */
	public List<CoppiaA> getAllCoppie(Map<String, People>idMap, int anno) {
		String sql = "SELECT a1.playerID AS id1, a2.playerID AS id2 "
				+ "FROM appearances a1, appearances a2 "
				+ "WHERE a1.teamCode = a2.teamCode "
				+ "AND a1.year = a2.year AND a1.year = ? ";
		List<CoppiaA> result = new ArrayList<CoppiaA>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				People p1 = idMap.get(rs.getString("id1"));
				People p2 = idMap.get(rs.getString("id2"));
				if(p1!=null && p2!=null && p1!= p2) {
					CoppiaA arco = new CoppiaA(p1, p2);
					result.add(arco);
				
				}
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	//=================================================================
	//==================== HELPER FUNCTIONS =========================
	//=================================================================
	public List<String> readTeamDelPlayerNellAnno(People x, int anno) {
		String sql = "SELECT a1.teamCode "
				+ "FROM appearances a1 "
				+ "WHERE a1.year = ? "
				+ "AND a1.playerID = ? ";
		List<String> teamIDs = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, x.getPlayerID());
			st.setInt(2, anno);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				String team = rs.getString("teamCode");
				teamIDs.add(team);
		}
		conn.close();
		return teamIDs;

	} catch (SQLException e) {
		e.printStackTrace();
		System.out.println("Errore connessione al database");
		throw new RuntimeException("Error Connection Database");
	}
}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getBirthDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("birth_date") != null) {
				return rs.getTimestamp("birth_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getDebutDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("debut_date") != null) {
				return rs.getTimestamp("debut_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getFinalGameDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("finalgame_date") != null) {
				return rs.getTimestamp("finalgame_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getDeathDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("death_date") != null) {
				return rs.getTimestamp("death_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	public List<Integer> readTeamDelPlayerNellAnno(int anno, People p, Map<Integer, Team> teamsIDMap) {
		String sql = "SELECT a.playerID, a.teamID "
				+ "FROM appearances a "
				+ "WHERE a.year=? AND a.playerID = ? ";
		List<Integer> result = new ArrayList<Integer>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setObject(2, p.getPlayerID());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Integer squadra = teamsIDMap.get(rs.getInt("teamID")).getID();
				result.add(squadra);
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}



	
}
