/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

import java.util.LinkedHashMap;
import lgraphs.LGraphMatcher;
import lgraphs.LGraph;
import lgraphs.LGraphNode;
import java.util.Map;

/**
 *
 * @author santi
 */
public class LGraphRuleMatcher {
    LGraph graph;
    LGraphRewritingRule rule;
    LGraphMatcher matcher;
    
    public LGraphRuleMatcher(LGraph a_graph, LGraphRewritingRule a_rule) {
        graph = a_graph;
        rule = a_rule;
        matcher = new LGraphMatcher(a_rule.pattern, a_graph);
    }
    
    public LGraph getNextResult() {
        boolean negatedPatternMatches;
        do{
            Map<LGraphNode, LGraphNode> map = matcher.nextMatch();
            if (map==null) return null; 

            negatedPatternMatches = false;
            for(int i = 0;i<rule.negatedPatterns.size();i++) {
                LGraph negatedPattern = rule.negatedPatterns.get(i);
                Map<LGraphNode, LGraphNode> map2 = new LinkedHashMap<LGraphNode, LGraphNode>();
                for(LGraphNode n:negatedPattern.getNodes()) {
                    LGraphNode originalPatternNode = rule.mapsFromNegatedPatterns.get(i).get(n);
                    if (originalPatternNode!=null) {
                        LGraphNode graphNode = map.get(originalPatternNode);
                        map2.put(n,graphNode);
                    }
                }

                LGraphMatcher matcher2 = new LGraphMatcher(negatedPattern, graph, map2);
                Map<LGraphNode, LGraphNode> tmp_map = matcher2.nextMatch();
                if (tmp_map!=null) {
                    negatedPatternMatches = true;
                    break;
                }
            }
            if (!negatedPatternMatches) return rule.applyRule(graph, map);        
        } while(negatedPatternMatches);
        
        // we should never get here
        return null;
    }
    
}
