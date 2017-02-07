/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lgraphs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import lgraphs.ontology.Sort;

/**
 *
 * @author santi
 */
public class LabelSet {

    List<Sort> labels = new ArrayList<Sort>();
    List<Sort> notLabels = new ArrayList<Sort>();

    public static LabelSet fromString(String string) throws Exception {
        LabelSet ls = new LabelSet();
        
        List<String> labelStrings = new ArrayList<String>();
        if (string.startsWith("{")) {
            StringTokenizer st = new StringTokenizer(string, "{,}");
            while (st.hasMoreTokens()) {
                labelStrings.add(st.nextToken());
            }
        } else {
            labelStrings.add(string);
        }

        for (String labelString : labelStrings) {
            if (labelString.startsWith("~")) {
                labelString = labelString.substring(1);
                ls.notLabels.add(Sort.getSort(labelString));
            } else {
                Sort s = Sort.getSort(labelString);
                if (s == null) System.err.println("null sort for `" + labelString + "'");
                ls.labels.add(s);
            }
        }
        
        return ls;
    }

    public LabelSet() {
    }

    public LabelSet(Sort l) {
        labels.add(l);
    }

    public LabelSet(LabelSet ls) {
        labels.addAll(ls.labels);
        notLabels.addAll(ls.notLabels);
    }
    
    
    public Sort getLabel() throws Exception 
    {
        if (notLabels.isEmpty() && labels.size()==1) return labels.get(0);
        throw new Exception("LabelSet does not contain a single label!");
    }


    public List<Sort> getLabels() throws Exception 
    {
        if (notLabels.isEmpty()) return labels;
        throw new Exception("LabelSet contains some notLabels!!");
    }

    
    public void setLabel(Sort a_label) {
        if (a_label == null) {
            System.err.println("LGraphNode.setLabel called with null label");
        }
        labels.clear();
        notLabels.clear();
        labels.add(a_label);
    }

    public void setLabels(List<Sort> a_labels) {
        labels.clear();
        notLabels.clear();
        for (Sort l : a_labels) {
            if (l == null) {
                System.err.println("LGraphNode setLabels called with null label");
            }
            labels.add(l);
        }
    }

    public void addLabel(Sort a_label) {
        if (a_label == null) {
            System.err.println("LGraphNode.addLabel called with null label");
        }
        List<Sort> toDelete = new ArrayList<Sort>();
        for (Sort s : labels) {
            if (s.subsumes(a_label)) {
                toDelete.add(s);
            }
        }
        labels.removeAll(toDelete);
        labels.add(a_label);
    }

    public void addNotLabel(Sort a_label) {
        if (a_label == null) {
            System.err.println("LGraphNode.notLabels called with null label");
        }
        List<Sort> toDelete = new ArrayList<Sort>();
        for (Sort s : notLabels) {
            if (s.subsumes(a_label)) {
                toDelete.add(s);
            }
        }
        notLabels.removeAll(toDelete);
        notLabels.add(a_label);
    }
    
    public void addLabelSet(LabelSet ls) {
        for(Sort l:ls.labels) {
            addLabel(l);
        }
        for(Sort l:ls.notLabels) {
            addNotLabel(l);
        }
    }    

    public boolean subsumedBy(Sort s) {
        for (Sort s2 : labels) {
            if (s.subsumes(s2)) {
                return true;
            }
        }
        return false;
    }

    public boolean subsumes(Sort s) {
        for (Sort s2 : labels) {
            if (!s2.subsumes(s)) {
                return false;
            }
        }
        for (Sort s2 : notLabels) {
            if (s2.subsumes(s)) {
                return false;
            }
        }
        return true;
    }

    public boolean subsumes(LabelSet n) {
        for (Sort s2 : labels) {
            if (!n.subsumedBy(s2)) {
                return false;
            }
        }
        for (Sort s : notLabels) {
            if (n.subsumedBy(s)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        List<String> tmp = new ArrayList<String>();

        for (Sort l : labels) {
            tmp.add("" + l);
        }
        for (Sort l : notLabels) {
            tmp.add("~" + l);
        }

        if (tmp.size() == 1) {
            return tmp.get(0);
        } else {
            String buffer = "{";
            for (int i = 0; i < tmp.size(); i++) {
                String l = tmp.get(i);
                if (i == 0) {
                    buffer += l;
                } else {
                    buffer += "," + l;
                }
            }
            return buffer + "}";
        }
    }

}
