/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

import lgraphs.LGraph;
import lgraphs.LGraphEdge;
import lgraphs.LGraphNode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author santi
 */
public class LGraphRewritingRule {
    String name;    // rules can have a name, with the purposes ot informing the user
                    // which rules were fired.
    double weight;  // If the generation algorithm is stochastic, use this weight to 
                    // compute the probability of firing this rule versus other rules
                    // The probability will be "weight/sum of weights"
    double decay;   // The decay with which the weight decreases after each application
    
    LGraph pattern;
    LGraph result;
    Map<LGraphNode, LGraphNode> map;    // mapping between the nodes of result to pattern
    
    public LGraphRewritingRule(LGraph a_pattern, LGraph a_result, Map<LGraphNode, LGraphNode> a_map) {
        name = null;
        pattern = a_pattern;
        result = a_result;
        map = a_map;
    }

    public LGraphRewritingRule(String a_name, double a_weight, double a_decay, LGraph a_pattern, LGraph a_result, Map<LGraphNode, LGraphNode> a_map) {
        name = a_name;
        weight = a_weight;
        decay = a_decay;
        pattern = a_pattern;
        result = a_result;
        map = a_map;
    }
    
    public String getName() {
        return name;
    }
    
    public LGraph applyRule(LGraph graph, Map<LGraphNode, LGraphNode> matching) {
        Map<LGraphNode, LGraphNode> cloneMap = new HashMap<LGraphNode, LGraphNode>();
        LGraph clone = graph.clone(cloneMap);
        
        // Step 1: remove all the edges from the pattern in the clone:
        for(LGraphNode np:pattern.getNodes()) {
            for(LGraphEdge ep:np.getEdges()) {
                LGraphNode nodeClone = cloneMap.get(matching.get(np));
                LGraphEdge found = null;
                for(LGraphEdge edgeClone:nodeClone.getEdges()) {
                    if (ep.label.subsumes(edgeClone.label) &&
                        cloneMap.get(matching.get(ep.end))==edgeClone.end) {
                        found = edgeClone;
                        break;
                    }
                }
                if (found==null) {
                    System.out.flush();
                    System.err.println("LGraphRewritingRule.applyRule: matching edge not found!!");
                    System.err.println("Pattern:");
                    System.err.println("  " + pattern);
                    System.err.println("Graph:");
                    System.err.println("  " + graph.toString("M"));
                    System.err.println("Matching:");
                    for(LGraphNode n:matching.keySet()) {
                        System.err.println("  N" + pattern.getNodes().indexOf(n) + " -> M" + graph.getNodes().indexOf(matching.get(n)));
                    }
                    throw new Error("matching edge not found!");
                }
                nodeClone.getEdges().remove(found);
            }
        }
        
        // Step 2: add all the nodes from result to the clone:
        Map<LGraphNode, LGraphNode> resultMap = new HashMap<LGraphNode, LGraphNode>();
        for(LGraphNode nr:result.getNodes()) {
            LGraphNode nodeClone = map.get(nr);
            if (nodeClone==null) {
                nodeClone = clone.addNode(nr.getLabels());
            } else {
                nodeClone = cloneMap.get(matching.get(nodeClone));
                if (!nr.subsumes(nodeClone)) nodeClone.setLabels(nr.getLabels());
            }      
            resultMap.put(nr, nodeClone);
        }

        // Step 3: add all the edges from result to the clone:
        for(LGraphNode nr:result.getNodes()) {
            LGraphNode nodeClone = resultMap.get(nr);
            for(LGraphEdge edge:nr.getEdges()) {
                clone.addEdge(nodeClone, edge.label, resultMap.get(edge.end));
            }
        }        
        
        
        return clone;
    }
}
