/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lgraphs.sampler;

import java.util.ArrayList;
import java.util.List;
import lgraphs.LGraph;
import lgraphs.LGraphEquivalence;

/**
 *
 * @author santi
 */
public class LGraphGrammarSystematicDFSIterator {
    List<LGraphGrammarSampler> open;
    boolean filterEquivalents = false;
    
    List<LGraph> previousSolutions = new ArrayList<LGraph>();
    
    
    public LGraphGrammarSystematicDFSIterator(LGraphGrammarSampler sampler, boolean a_filterEquivalents)
    {
        open = new ArrayList<LGraphGrammarSampler>();
        open.add(sampler);
        filterEquivalents = a_filterEquivalents;
    }
    
    
    public LGraphGrammarSampler getNext() throws Exception 
    {
        while(true) {
            if (open.isEmpty()) return null;
            LGraphGrammarSampler current = open.remove(0);
            List<LGraphGrammarSampler> successors = current.allPossibleSuccessors();
            if (successors.isEmpty()) {
                if (filterEquivalents) {
                    boolean equivalentFound = false;
                    for(LGraph g:previousSolutions) {
//                        if (g.equivalent(current.getCurrentGraph())) {
                        if (LGraphEquivalence.equivalents(g, current.getCurrentGraph())) {
                            equivalentFound = true;
                            System.out.println("filtering equivalent!");
                            break;
                        }
                    }
                    if (!equivalentFound) {
                        previousSolutions.add(current.getCurrentGraph());
                        return current;
                    }
                } else {                
                    return current;
                }
            } else {
                open.addAll(0, successors);
            }
        }
    }
}
