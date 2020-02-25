package com.ofbiz.automation.libraries;

import cucumber.api.testng.PickleEventWrapper;
import gherkin.events.PickleEvent;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleStep;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExamplesBuilder implements PickleEventWrapper {

    private final PickleEvent pickleEvent;

    public ExamplesBuilder(PickleEvent pickleEvent) {
        this.pickleEvent = pickleEvent;
    }

    public PickleEvent getPickleEvent() {
        return pickleEvent;
    }

    @Override
    public String toString() {
        return "\"" + pickleEvent.pickle.getName() + "\"";
    }
    
    public List<PickleEvent> createPickle(){
    	//Get the pickle event
    	PickleEvent pickleEvent = this.pickleEvent;
    	Pickle pickle = pickleEvent.pickle;
    	//Get the steps from the pickle
    	List<PickleStep> pickleSteps = pickle.getSteps();
    	//Create a new pickle steps instance where all steps will be written
    	
    	List<Pickle> newPickles = new ArrayList<Pickle>();
    	List<PickleEvent> newPickleEvents = new ArrayList<PickleEvent>();
    	//Added all steps to make them writable
    	//newPickleSteps.addAll(0, pickleSteps);
    	List<List<PickleStep>> rowPickleSteps = new ArrayList<List<PickleStep>>();
    	//Iterate over the current steps and update the writable steps accordingly
    	pickle.getSteps().forEach(step->{
    		List<PickleStep> cellPickleStep = new ArrayList<PickleStep>();
    		//See which steps have a call to file
    		if(step.getText().contains("@file:")){
    			//Get the rows based on file provided and iterate over that to create new steps for the new pickle
    			for(String dataRow : this.readDataFromTextFile(step.getText())){
    				//Method returns the updated text with file replaced with rows from file
    				String newStep = dataRow;//step.getText().replace(step.getText(), "this is \"my\" step");
        			PickleStep pickleStep = new PickleStep(newStep, step.getArgument(), step.getLocations());
        			//Added a new pickle step to replace the @file
        	    	cellPickleStep.add(pickleStep);
    			}
    		}
    		else{
    			cellPickleStep.add(step);
    		}
    		rowPickleSteps.add(cellPickleStep);
    	});
    	int rowItr=0;
    	for(PickleStep updateStep:rowPickleSteps.get(0)){
    		List<PickleStep> newPickleSteps = new ArrayList<PickleStep>();
    		for(int stpCount=0;stpCount<rowPickleSteps.size();stpCount++){
        		if(pickleSteps.get(stpCount).getText().contains("@file:")){
        			newPickleSteps.add(rowPickleSteps.get(stpCount).get(rowItr));
        		}
        		else{
        			newPickleSteps.add(pickleSteps.get(stpCount));
        		}
        	}
    		rowItr++;
    		newPickles.add(new Pickle(pickle.getName(), pickle.getLanguage(), newPickleSteps, pickle.getTags(), pickle.getLocations()));
    	}
    	
    	/*for(PickleStep rowPickleStep:rowPickleSteps){
    		newPickleSteps.addAll(0, pickleSteps);
    		for(PickleStep newPickleStep:newPickleSteps){
    			if(newPickleStep.getText().contains("@file:")){
    				newPickleSteps.remove(newPickleStep);
    				newPickleSteps.add(rowPickleStep);
    			}
    		}
    		newPickles.add(new Pickle(pickle.getName(), pickle.getLanguage(), newPickleSteps, pickle.getTags(), pickle.getLocations()));
    	}*/
    	//Create the new pickle to return it
    	
    	//Pickle newpickle = new Pickle(pickle.getName(), pickle.getLanguage(), newPickleSteps, pickle.getTags(), pickle.getLocations());
    	//Pickle newpickle = newPickles.get(0);
    	//PickleEvent newPickleEvent = new PickleEvent(pickleEvent.uri, newpickle);
    	for(Pickle newPickle : newPickles){
    		newPickleEvents.add(new PickleEvent(pickleEvent.uri, newPickle));
    	}
    	return newPickleEvents;
    }
    
    public List<String> readDataFromTextFile(String step){
    	List<Map<String,String>> dataRowMap = new ArrayList<Map<String,String>>();
    	List<String> stepText = new ArrayList<String>();
    	//Get the arguments from the step to replace the @file with text from file cell
    	Pattern p = Pattern.compile("\"([^\"]*)\"");
    	Matcher m = p.matcher(step);
    	List<String> dataFiles=new ArrayList<String>();
    	Map<String,List<String>> stepMap = new HashMap<String,List<String>>();
    	while (m.find()) {
    	  dataFiles.add(m.group(1));  
    	}
    	//Iterate over the number of matches in a step and replace all @file with respective values from file
    	//for(String dataFile : dataFiles){
    		//Get the file name
	    	String dataFilePath=dataFiles.get(0).split(":")[1];
	    	
	    	try {
				BufferedReader in = new BufferedReader( new FileReader(dataFilePath));
				String headerRow = in.readLine();
				List<String> columnNames = splitDataColumns(headerRow);
				List<List<String>> rowData = new ArrayList<List<String>>();
				String dataRow=null;
				while((dataRow=in.readLine())!=null){
					rowData.add(splitDataColumns(dataRow));
				}
				in.close();
				
				for(List<String> dataRows : rowData){
					Map<String,String> cells = new HashMap<String,String>();
					for(int column=0;column<columnNames.size();column++){
						cells.put(columnNames.get(column), dataRows.get(column));
					}
					dataRowMap.add(cells);
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    //Read the values and update the step with cell values from table
    	for(Map<String,String> cell:dataRowMap){
    		String newStep=step;
    		for(int col=0;col<dataFiles.size();col++){
    			String colName=dataFiles.get(col).split(":")[2];
    			newStep=newStep.replace(dataFiles.get(col), cell.get(colName));
    		}
    		stepText.add(newStep);
    	}
		stepMap.put("steps", stepText);
		return stepMap.get("steps");
    }
    
    public List<String> splitDataColumns(String line){
    	List<String> data = new ArrayList<String>();
    	//Split by | and read values
    	for(String column : line.split("\\|")){
    		if(!column.trim().isEmpty()){
    			data.add(column);
    		}
    	}
    	return data;
    }
}
