/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs;

import lgraphs.ontology.Sort;

/**
 *
 * @author santi
 */
public class LGraphEdge {
    public LabelSet labels = new LabelSet();    
    public LGraphNode start;
    public LGraphNode end;
    
    public LGraphEdge(Sort a_label, LGraphNode a_start, LGraphNode a_end) {
        labels = new LabelSet(a_label);
        start = a_start;
        end = a_end;
    }

    public LGraphEdge(LabelSet a_labels, LGraphNode a_start, LGraphNode a_end) {
        labels = new LabelSet(a_labels);
        start = a_start;
        end = a_end;
    }

}
