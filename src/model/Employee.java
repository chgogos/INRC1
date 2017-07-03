package model;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    int index;
    String contractId;
    public String id;
    private Contract contract;
    public String name;
    public List<String> skills;

    public Employee() {

    }

    public Employee(int index, String id) {
        this.id = id;
        skills = new ArrayList<String>();
        this.index=index;
    }

    public void setName(String aName) {
        this.name = aName;
    }

    public void setContractId(String cid) {
        this.contractId = cid;
    }

    public void addSkill(String skill) {
        skills.add(skill);
    }

    public String toString() {
        return String.format("ID=%s NAME=%s SKILLS=%s CONTRACT=%s", id, name,
                skills, contractId);
    }

    public int getIndex() {
        return index;
    }
    
    public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public boolean canSubstitute(Employee other){
		//Should we use equals???
    	if(other.getContract()!=contract) return false;
    	if (skills.size() != other.skills.size()) return false;	
		for (String es1 : skills) {
			if(!other.skills.contains(es1)) return false;
		}
		return true;
    }
}
