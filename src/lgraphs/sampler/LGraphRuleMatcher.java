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
    boolean objectIdentity;
    
    public LGraphRuleMatcher(LGraph a_graph, LGraphRewritingRule a_rule, boolean a_objectIdentity) {
        graph = a_graph;
        rule = a_rule;
        objectIdentity = a_objectIdentity;
        matcher = new LGraphMatcher(a_rule.pattern, a_graph, objectIdentity);
    }
    
    public LGraph getNextResult() {
        do{
            Map<LGraphNode, LGraphNode> map = matcher.nextMatch();
            if (map==null) return null; 

            boolean negatedPatternMatches = false;
//            System.out.println("/----");
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

//                System.out.println("i: " + i + ", map2: " + map2);
                LGraphMatcher matcher2 = new LGraphMatcher(negatedPattern, graph, objectIdentity, map2);
                Map<LGraphNode, LGraphNode> tmp_map = matcher2.nextMatch();
//                System.out.println("i: " + i + ", tmp_map: " + tmp_map);
                if (tmp_map!=null) {
                    negatedPatternMatches = true;
                    break;
                }
            }
            if (!negatedPatternMatches) {
/*                
                int i = 0;
                for(LGraphNode n:map.keySet()) {
                    System.out.println("N" + i +" --> N" + graph.getNodes().indexOf(map.get(n)));
                    i++;
                }
                System.out.println("\\----");
*/                
                return rule.applyRule(graph, map);
            }        
        } while(true);        
    }
    
}
