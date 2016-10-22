/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.ontology;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author santi
 */
public class Sort {
    
    static HashMap<String, Sort> table = new HashMap<>();
    static {
        try {
            Sort.newSort("any");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    String name;
    List<Sort> parents = new LinkedList<>();
    List<Sort> children = new LinkedList<>();
    
    public static Sort getOrNewSort(String a_name) throws Exception {
        Sort l = table.get(a_name);
        if (l==null) {
            l = new Sort(a_name);
            table.put(a_name, l);
        }
        return l;
    }
    
    public static Sort getOrNewSort(String a_name, String a_super) throws Exception {
        Sort l = table.get(a_name);
        Sort s2 = table.get(a_super);
        if (s2==null) throw new Exception("Sort " + a_super + " (specified as parent of "+a_name+") is undefined!");
        if (l==null) {
            l = new Sort(a_name, s2);
            table.put(a_name, l);
        } else {
            if (!l.parents.contains(s2)) l.parents.add(s2);
        }
        return l;
    }

    public static Sort newSort(String a_name) throws Exception {
        if (table.get(a_name)!=null) throw new Exception("Sort " + a_name + " already exists!");
        Sort l = new Sort(a_name);
        table.put(a_name, l);
        return l;
    }

    public static Sort newSort(String a_name, String parent) throws Exception {
        if (table.get(a_name)!=null) throw new Exception("Sort " + a_name + " already exists!");
        Sort l = new Sort(a_name, table.get(parent));
        table.put(a_name, l);
        return l;
    }
    
    public static Sort newSort(String a_name, Sort parent) throws Exception {
        if (table.get(a_name)!=null) throw new Exception("Sort " + a_name + " already exists!");
        Sort l = new Sort(a_name, parent);
        table.put(a_name, l);
        return l;
    }
    
    public static Sort getSort(String name) throws Exception {
        Sort l = table.get(name);
        if (l==null) //System.err.println("Sort '" + name + "' is not defined.");
            throw new Exception("Sort '" + name + "' is not defined.");
        return l;
    }
    
    public static Sort sortExistsP(String name) {
        Sort l = table.get(name);
        return l;
    }

    protected Sort(String a_name) {
        name = a_name;
    }
    
    
    protected Sort(String a_name, Sort parent) {
        name = a_name;
        parents.add(parent);
        if (parent==null) System.err.println("Parent is null when defining '" + a_name + "'");
        parent.children.add(this);
    }
    
    public String getName() {
        return name;
    }
    
    public List<Sort> getChildren() {
        return children;
    }
    
    public List<Sort> getAllChildren() {
        List<Sort> ret = new LinkedList<>();
        ret.addAll(children);
        for(Sort p:children) {
            for(Sort s:p.getAllChildren()) {
                if (!ret.contains(s)) ret.add(s);
            }
        }
        return ret;
    }

    public List<Sort> getParents() {
        return parents;
    }

    public List<Sort> getAllParents() {
        List<Sort> ret = new LinkedList<>();
        ret.addAll(parents);
        for(Sort p:parents) {
            ret.addAll(p.getAllParents());
        }
        return ret;
    }


    public void addParent(Sort parent) {
        if (!parents.contains(parent)) parents.add(parent);
        parent.children.add(this);        
    }
    
    
    public boolean subsumes(Sort l) {
        if (l==this) return true;
        for(Sort parent:l.parents) {
            if (subsumes(parent)) return true;
        }
        return false;
    }
    
    public boolean subsumes(String sortName) {
        Sort l = table.get(sortName);
        if (l==this) return true;
        for(Sort parent:l.parents) {
            if (subsumes(parent)) return true;
        }
        return false;
    }

    
    public String toString() {
        return name;
    }
}
