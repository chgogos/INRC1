/** George Goulas
 *  Computer Systems Laboratory, 2010
 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Employee;
import model.InrcProblem;
import model.Schedule;
import model.TimeUnit;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author goulas
 *
 */
public class ScheduleImporter extends DefaultHandler {
	static Logger log = Logger.getLogger(ScheduleImporter.class.getName());
	static SimpleDateFormat sdf = InrcDateFormat.dateFormat;
	
	InrcProblem problem;
	Schedule schedule;
	
	String tmpAssignmentDate;
	Employee tmpAssignmentEmployee;
	String tmpAssignmentShiftType;
	
    Stack<String> tagsXmlStack;
    String tempVal;
    

    public ScheduleImporter(InrcProblem problem) {
        this.problem = problem;
    }

    
    public void loadXML(String filename) throws ParserConfigurationException, SAXException, IOException {
    	File f = new File(filename);
    	loadXML(f);
    }
    
    public void loadXML(File file) throws ParserConfigurationException, SAXException, IOException {
    	FileInputStream fis = new FileInputStream(file);
    	loadXML(fis);
    }

    
    public void loadXML(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		schedule = new Schedule(problem);
		tagsXmlStack = new Stack<String>();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		sp.parse(is, this);
}

    
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
    	tempVal = "";
        tagsXmlStack.push(qName);
        if(qName.equals("Assignment")) {
        	tmpAssignmentDate = null;
        	tmpAssignmentEmployee = null;
        	tmpAssignmentShiftType = null;
        }
    }
    
    
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if("SchedulingPeriodID".equals(qName)) {
        	if(!tempVal.equals(problem.getId())) {
        		throw new IllegalArgumentException("SchedulingPeriodID does not match with the loaded problem ID");
        	}
        } else if("Date".equals(qName)) {
        	tmpAssignmentDate = tempVal;
    	} else if("Employee".equals(qName)) {
    		// TODO: replace with HASHset
    		for(Employee e : problem.employees) {
    			if(e.id.equals(tempVal)) {
    				tmpAssignmentEmployee = e;
    				break;
    			}
    		}
    	} else if("ShiftType".equals(qName)) {
    			tmpAssignmentShiftType = tempVal;
    	} else if ("Assignment".equals(qName)) {
    		// TODO: Consider a hashset for timeunits to search quickly or do binary search
    		for(int i=0; i<problem.timeunits.size(); i++) {
    			TimeUnit tu = problem.timeunits.get(i);
    			if(tmpAssignmentDate.equals(sdf.format(tu.date))) {
    				if(tmpAssignmentShiftType.equals(tu.shiftType.id)) {
    					log.fine("Importing Employee " + tmpAssignmentEmployee + " at timeunit " + i);
    					schedule.schedule(tmpAssignmentEmployee, i);
    					//schedule.timeunits[i].add(tmpAssignmentEmployee);
    				}
    			}
    		}
    	}
    }


    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }

    
    public Schedule getSchedule() {
        return schedule;
    }

}
