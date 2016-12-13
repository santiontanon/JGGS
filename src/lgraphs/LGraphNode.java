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
    protected LabelSet labels = new LabelSet();    
    protected List<LGraphEdge> edges = new ArrayList<LGraphEdge>();
    
    protected LGraphNode() {
    }

    public LGraphNode(LabelSet a_labels) {
        labels = new LabelSet(a_labels);
    }
        
    public LGraphNode(Sort a_label) {
        if (a_label==null) System.err.println("LGraphNode constructor called with null label");
        labels.addLabel(a_label);
    }

    public LGraphNode(Sort a_label, Sort a_label2) {
        if (a_label==null) System.err.println("LGraphNode constructor2 called with null label (first)");
        if (a_label2==null) System.err.println("LGraphNode constructor2 called with null label (second)");
        labels.addLabel(a_label);
        labels.addLabel(a_label2);
    }

    public LGraphNode(List<Sort> a_labels) {
        for(Sort l:a_labels) {
            if (l==null) System.err.println("LGraphNode constructor (list) called with null label");
            labels.addLabel(l);
        }
    }
    
    public LabelSet getLabelSet() {
        return labels;
    }

    public void setLabelSet(LabelSet ls) {
        labels = new LabelSet(ls);
    }


    public void setLabel(Sort a_label) {
        labels.setLabel(a_label);
    }

    public void addLabel(Sort a_label) {
        labels.addLabel(a_label);
    }
    
    public void addLabelSet(LabelSet ls) {
        labels.addLabelSet(ls);
    }

    public void addNotLabel(Sort a_label) {
        labels.addNotLabel(a_label);
    }

    public void setLabels(List<Sort> a_labels) {
        labels.setLabels(a_labels);
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
            if (e.labels.subsumedBy(edgeLabel)) return e.end;
        }
        return null;
    }

    public List<LGraphEdge> getChildren(Sort edgeLabel) {
        List<LGraphEdge> l = new ArrayList<LGraphEdge>();
        for(LGraphEdge e:edges) {
            if (e.labels.subsumedBy(edgeLabel)) l.add(e);
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
        return labels.subsumedBy(s);
    }

    public boolean subsumes(Sort s) {
        return labels.subsumes(s);
    }

    public boolean subsumes(LGraphNode n) {
        return labels.subsumes(n.labels);
    }

    public String toString() {
        return "node(" + toStringLabel() + ")";
    }

    public String toStringLabel() {
        return labels.toString();
    }
}
