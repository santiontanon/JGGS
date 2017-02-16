/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs;

import lgraphs.ontology.Sort;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author santi
 */
public class LGraphNode {
    public LabelSet labelSet = new LabelSet();    
    protected List<LGraphEdge> edges = new ArrayList<LGraphEdge>();
    
    protected LGraphNode() {
    }

    public LGraphNode(LabelSet a_labels) {
        labelSet = new LabelSet(a_labels);
    }
        
    public LGraphNode(Sort a_label) {
        if (a_label==null) {
            System.err.println("LGraphNode constructor called with null label");
        }
        labelSet.addLabel(a_label);
    }

    public LGraphNode(Sort a_label, Sort a_label2) {
        if (a_label==null) System.err.println("LGraphNode constructor2 called with null label (first)");
        if (a_label2==null) System.err.println("LGraphNode constructor2 called with null label (second)");
        labelSet.addLabel(a_label);
        labelSet.addLabel(a_label2);
    }

    public LGraphNode(List<Sort> a_labels) {
        for(Sort l:a_labels) {
            if (l==null) System.err.println("LGraphNode constructor (list) called with null label");
            labelSet.addLabel(l);
        }
    }
    
    public LabelSet getLabelSet() {
        return labelSet;
    }

    public void setLabelSet(LabelSet ls) {
        labelSet = new LabelSet(ls);
    }


    public void setLabel(Sort a_label) {
        labelSet.setLabel(a_label);
    }

    public void addLabel(Sort a_label) {
        labelSet.addLabel(a_label);
    }
    
    public void addLabelSet(LabelSet ls) {
        labelSet.addLabelSet(ls);
    }

    public void addNotLabel(Sort a_label) {
        labelSet.addNotLabel(a_label);
    }

    public void setLabels(List<Sort> a_labels) {
        labelSet.setLabels(a_labels);
    }

    public void addEdge(Sort edgeLabel, LGraphNode node) {
        edges.add(new LGraphEdge(edgeLabel, this, node));
    }
        
    public void addEdge(LabelSet edgeLabel, LGraphNode node) {
        edges.add(new LGraphEdge(edgeLabel, this, node));
    }

    public void addEdge(LGraphEdge e) {
        e.start = this;
        edges.add(e);
    }

    public void removeEdge(LGraphEdge e) {
        edges.remove(e);
    }
    
    public LGraphNode getFirstChild(Sort edgeLabel) {
        for(LGraphEdge e:edges) {
            if (e.labelSet.subsumedBy(edgeLabel)) return e.end;
        }
        return null;
    }
    
    public LGraphNode getFirstChild(Sort edgeLabel, Sort endLabel) {
        for(LGraphEdge e:edges) {
            if (e.labelSet.subsumedBy(edgeLabel) && e.end.subsumedBy(endLabel)) return e.end;
        }
        return null;
    }
    

    public List<LGraphEdge> getChildren(Sort edgeLabel) {
        List<LGraphEdge> l = new ArrayList<LGraphEdge>();
        for(LGraphEdge e:edges) {
            if (e.labelSet.subsumedBy(edgeLabel)) l.add(e);
        }
        return l;
    }
    
    public List<LGraphNode> getChildren(Sort edgeLabel, Sort endLabel) {
        List<LGraphNode> l = new ArrayList<LGraphNode>();
        for(LGraphEdge e:edges) {
            if (e.labelSet.subsumedBy(edgeLabel) && e.end.subsumedBy(endLabel)) l.add(e.end);
        }
        return l;
    }
    
    
    public LGraphEdge getEdge(LGraphNode target) {
        for(LGraphEdge e:edges) {
            if (e.end == target) return e;
        }
        return null;
    }
    
    public List<LGraphEdge> getEdgesTo(LGraphNode target) {
        List<LGraphEdge> l = new ArrayList<LGraphEdge>();
        for(LGraphEdge e:edges) {
            if (e.end == target) l.add(e);
        }
        return l;
    }

    public List<LGraphEdge> getEdges() {
        return edges;
    }

    public boolean subsumedBy(Sort s) {
        return labelSet.subsumedBy(s);
    }

    public boolean subsumes(Sort s) {
        return labelSet.subsumes(s);
    }

    public boolean subsumes(LGraphNode n) {
        return labelSet.subsumes(n.labelSet);
    }

    public String toString() {
        return "node(" + toStringLabel() + ")";
    }

    public String toStringLabel() {
        return labelSet.toString();
    }
}
