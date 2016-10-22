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
    protected List<Sort> labels = new ArrayList<>();
    protected List<LGraphEdge> edges = new ArrayList<>();
    
    // only for patterns:
    public Sort notLabel = null;   // To indicate that the label is NOT "notLabel" (useful for patterns)
    public List<LGraphEdge> notEdges = null;    // To indicate edges that should NOT be in the graph (useful for patterns)

    protected LGraphNode() {
    }

    public LGraphNode(Sort a_label) {
        if (a_label==null) System.err.println("LGraphNode constructor called with null label");
        labels.add(a_label);
    }

    public LGraphNode(Sort a_label, Sort a_label2) {
        if (a_label==null) System.err.println("LGraphNode constructor2 called with null label (first)");
        if (a_label2==null) System.err.println("LGraphNode constructor2 called with null label (second)");
        labels.add(a_label);
        labels.add(a_label2);
    }

    public LGraphNode(List<Sort> a_labels) {
        for(Sort l:a_labels) {
            if (l==null) System.err.println("LGraphNode constructor (list) called with null label");
            labels.add(l);
        }
    }

    public static LGraphNode newNegaTiveLGraphNode(Sort l) {
        LGraphNode n = new LGraphNode();
        n.notLabel = l;
        return n;
    }

    public void setLabel(Sort a_label) {
        if (a_label==null) System.err.println("LGraphNode.setLabel called with null label");
        labels.clear();
        labels.add(a_label);
    }

    public void setLabels(List<Sort> a_labels) {
        labels.clear();
        for(Sort l:a_labels) {
            if (l==null) System.err.println("LGraphNode setLabels called with null label");
            labels.add(l);
        }
    }

    public void addEdge(Sort edgeLabel, LGraphNode node) {
        edges.add(new LGraphEdge(edgeLabel, this, node));
    }
    
    public void addNegationEdge(Sort edgeLabel, LGraphNode node) {
        if (notEdges==null) notEdges = new ArrayList<>();
        notEdges.add(new LGraphEdge(edgeLabel, this, node));
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
            if (edgeLabel.subsumes(e.label)) return e.end;
        }
        return null;
    }

    public List<LGraphEdge> getChildren(Sort edgeLabel) {
        List<LGraphEdge> l = new ArrayList<>();
        for(LGraphEdge e:edges) {
            if (edgeLabel.subsumes(e.label)) l.add(e);
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
        List<LGraphEdge> l = new ArrayList<>();
        for(LGraphEdge e:edges) {
            if (e.end == target) l.add(e);
        }
        return l;
    }

    public List<Sort> getLabels() {
        return labels;
    }

    public Sort getLabel()
    {
        assert(labels.size()==1);
        return labels.get(0);
    }

    public List<LGraphEdge> getEdges() {
        return edges;
    }

    public boolean subsumedBy(Sort s) {
        for(Sort s2:labels) {
            if (!s.subsumes(s2)) return false;
        }
        return true;
    }

    public boolean subsumes(Sort s) {
        for(Sort s2:labels) {
            if (!s2.subsumes(s)) return false;
        }
        return true;
    }

    public boolean subsumes(LGraphNode n) {
        for(Sort s:labels) {
            if (!n.subsumedBy(s)) return false;
        }
        return true;
    }

    public String toString() {
        if (labels.size()==1) {
            return "node(" + labels.get(0) + ")";
        } else {
            return "node(" + labels + ")";
        }
    }

    public String toStringLabel() {
        if (labels.size() == 1) {
            return labels.get(0).getName();
        } else {
            return labels.toString();
        }
    }
}
