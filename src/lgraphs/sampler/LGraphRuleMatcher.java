/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

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
        Map<LGraphNode, LGraphNode> map = matcher.nextMatch();
        if (map==null) return null;        
        return rule.applyRule(graph, map);
    }
    
}
