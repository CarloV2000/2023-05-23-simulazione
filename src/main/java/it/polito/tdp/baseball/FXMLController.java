package it.polito.tdp.baseball;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import it.polito.tdp.baseball.model.Model;
import it.polito.tdp.baseball.model.People;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnConnesse;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnDreamTeam;

    @FXML
    private Button btnGradoMassimo;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextField txtSalary;

    @FXML
    private TextField txtYear;

    
    
    @FXML
    void doCalcolaConnesse(ActionEvent event) {
    	int nComponentiConnesse = model.getNumberOfConnectedComponents();
    	this.txtResult.appendText("\nNumero componenti connesse = "+nComponentiConnesse);
    }

    
    
    @FXML
    void doCreaGrafo(ActionEvent event) {
    	String inputAnno = this.txtYear.getText();
    	String inputSalary = this.txtSalary.getText();
    	Integer inputAnnoNUM;
    	Integer inputSalaryNUM;
    	try {
    		inputAnnoNUM = Integer.parseInt(inputAnno);
    		
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Inserire un valore numerico nel campo Year");
    		return;
    	}
    	try {
    		inputSalaryNUM = Integer.parseInt(inputSalary);//ricorda di verificare che l'anno inserito sia presente nel DB
    		
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Inserire un valore numerico nel campo Salary");
    		return;
    	}
    	String s = model.creaGrafo(inputAnnoNUM, inputSalaryNUM);
    	this.txtResult.setText(s);
    	
    	
    	this.btnConnesse.setDisable(false);
    	this.btnGradoMassimo.setDisable(false);
    	this.btnDreamTeam.setDisable(false);
    }

    
    @FXML
    void doDreamTeam(ActionEvent event) {
    	String input = this.txtYear.getText();
    	Integer inputAnnoNUM;
    	String s = "";
    	try {
    		inputAnnoNUM = Integer.parseInt(input);
    		//persiste un errore: lezione del 29-05-2023 per la soluzione
    		model.calcolaDreamTeam();
    		List<People> dreamTeam = model.getDreamTeam();
    		for(People p : dreamTeam) {
    			s += p.getNameFirst()+" "+p.getNameLast()+"\n";
    		}
    		this.txtResult.setText("Dream team :\n");
    		this.txtResult.appendText("Salario dream team : "+model.getSalarioMaggiore());
    		this.txtResult.appendText("\n"+s);
    		
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Inserire un valore numerico nel campo Year");
    		return;
    	}
    }

    
    @FXML
    void doGradoMassimo(ActionEvent event) {
    	String inputAnno = this.txtYear.getText();
    	Integer inputAnnoNUM;
    	try {
    		inputAnnoNUM = Integer.parseInt(inputAnno);
        	
    		People p = model.getVerticeGradoMassimo(inputAnnoNUM);
    		this.txtResult.appendText("\nIl vertice di grado massimo è : "+p.getNameFirst()+" "+p.getNameLast()+" avente peso = "+model.calcolaGrado(p, inputAnnoNUM));
    		
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Inserire un valore numerico nel campo Year");
    		return;
    	}
    	
    }

    
    @FXML
    void initialize() {
        assert btnConnesse != null : "fx:id=\"btnConnesse\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDreamTeam != null : "fx:id=\"btnDreamTeam\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnGradoMassimo != null : "fx:id=\"btnGradoMassimo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtSalary != null : "fx:id=\"txtSalary\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtYear != null : "fx:id=\"txtYear\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }

}
