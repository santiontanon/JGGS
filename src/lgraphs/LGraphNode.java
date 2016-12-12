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
    protected List<Sort> labels = new ArrayList<Sort>();
    protected List<LGraphEdge> edges = new ArrayList<LGraphEdge>();
    
    // only for patterns:
    public List<Sort> notLabels = new ArrayList<Sort>();  // To indicate that the label is NOT "notLabel" (useful for patterns)

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
        n.notLabels.add(l);
        return n;
    }

    public void setLabel(Sort a_label) {
        if (a_label==null) System.err.println("LGraphNode.setLabel called with null label");
        labels.clear();
        labels.add(a_label);
    }

    public void addLabel(Sort a_label) {
        if (a_label==null) System.err.println("LGraphNode.addLabel called with null label");
        List<Sort> toDelete = new ArrayList<Sort>();
        for(Sort s:labels) {
            if (s.subsumes(a_label)) toDelete.add(s);
        }
        labels.removeAll(toDelete);
        labels.add(a_label);
    }

    public void addNotLabel(Sort a_label) {
        if (a_label==null) System.err.println("LGraphNode.notLabels called with null label");
        notLabels.add(a_label);
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
        List<LGraphEdge> l = new ArrayList<LGraphEdge>();
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
        List<LGraphEdge> l = new ArrayList<LGraphEdge>();
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
            if (s.subsumes(s2)) return true;
        }
        return false;
    }

    public boolean subsumes(Sort s) {
        for(Sort s2:labels) {
            if (!s2.subsumes(s)) return false;
        }
        for(Sort s2:notLabels) {
            if (s2.subsumes(s)) return false;
        }
        return true;
    }

    public boolean subsumes(LGraphNode n) {
        for(Sort s2:labels) {
            if (!n.subsumedBy(s2)) return false;
        }        
        for(Sort s:notLabels) {
            if (n.subsumedBy(s)) return false;
        }
        return true;
    }

    public String toString() {
        return "node(" + toStringLabel() + ")";
    }

    public String toStringLabel() {
        List<String> tmp = new ArrayList<String>();
        
        for(Sort l:labels) tmp.add("" + l);
        for(Sort l:notLabels) {
            tmp.add("~" + l);
        }
        
        if (tmp.size() == 1) {
            return tmp.get(0);
        } else {
            String buffer = "{";
            for(int i = 0;i<tmp.size();i++) {
                String l = tmp.get(i);
                if (i==0) {
                    buffer += l;
                } else {
                    buffer += "," + l;
                }
            }
            return buffer + "}";
        }
    }
}
