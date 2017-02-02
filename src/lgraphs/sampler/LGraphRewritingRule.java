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
import java.util.List;
import java.util.Map;

/**
 *
 * @author santi
 */
public class LGraphRewritingRule {
    String name;            // rules can have a name, with the purposes ot informing the user
                            // which rules were fired.
    double weight;          // If the generation algorithm is stochastic, use this weight to 
                            // compute the probability of firing this rule versus other rules
                            // The probability will be "weight/sum of weights"
    double decay;           // The decay with which the weight decreases after each application
    int applicationLimit;   // Optional ruleApplicationLimit from grammar file, -1 for no limit
    String topic;           // Optional string to group the rules
    
    LGraph pattern;
    List<LGraph> negatedPatterns;
    List<Map<LGraphNode, LGraphNode>> mapsFromNegatedPatterns;    // mapping between the nodes of negated patterns to pattern
    
    LGraph replacement;
    Map<LGraphNode, LGraphNode> replacementMap;    // mapping between the nodes of replacement to pattern
    
    public LGraphRewritingRule(LGraph a_pattern, LGraph a_result, Map<LGraphNode, LGraphNode> a_map) {
        name = null;
        pattern = a_pattern;
        negatedPatterns = null;
        mapsFromNegatedPatterns = null;
        replacement = a_result;
        replacementMap = a_map;
    }

    public LGraphRewritingRule(String a_name, double a_weight, double a_decay, LGraph a_pattern, LGraph a_result, Map<LGraphNode, LGraphNode> a_map) {
        name = a_name;
        weight = a_weight;
        decay = a_decay;
        pattern = a_pattern;
        negatedPatterns = null;
        mapsFromNegatedPatterns = null;
        replacement = a_result;
        replacementMap = a_map;
        applicationLimit = -1;
        topic = null;
    }
    

    public LGraphRewritingRule(String a_name, double a_weight, double a_decay, LGraph a_pattern, 
                               List<LGraph> a_negatedPatterns, List<Map<LGraphNode, LGraphNode>> a_mapsFromNegatedPatterns,
                               LGraph a_replacement, Map<LGraphNode, LGraphNode> a_replacementMap, int a_applicationLimit, String a_topic) {
        name = a_name;
        weight = a_weight;
        decay = a_decay;
        pattern = a_pattern;
        negatedPatterns = a_negatedPatterns;
        mapsFromNegatedPatterns = a_mapsFromNegatedPatterns;
        replacement = a_replacement;
        replacementMap = a_replacementMap;
        applicationLimit = a_applicationLimit;
        topic = a_topic;
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
                    if (ep.labelSet.subsumes(edgeClone.labelSet) &&
                        cloneMap.get(matching.get(ep.end))==edgeClone.end) {
                        found = edgeClone;
                        break;
                    }
                }
                if (found==null) {
                    System.out.flush();
                    System.err.println("LGraphRewritingRule.applyRule: matching edge not found!!");
                    System.err.println("In: "+name);
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
        
        // Step 2: add all the nodes from replacement to the clone:
        Map<LGraphNode, LGraphNode> resultMap = new HashMap<LGraphNode, LGraphNode>();
//        System.out.println("replacementMap size: " + replacementMap.size());
        for(LGraphNode nr:replacement.getNodes()) {
            LGraphNode nodeClone = replacementMap.get(nr);
//            System.out.println("    nr: " + nr);
//            System.out.println("    nodeClone: " + nodeClone);
            if (nodeClone==null) {
                nodeClone = clone.addNode(nr.getLabelSet());
            } else {
                nodeClone = cloneMap.get(matching.get(nodeClone));
                if (!nr.subsumes(nodeClone)) {
//                    System.out.println("replacing " + nodeClone.getLabelSet() + " by " + nr.getLabelSet());
                    nodeClone.setLabelSet(nr.getLabelSet());
                } else {
//                    System.out.println(nr.getLabelSet() + " subsumes " + nodeClone.getLabelSet());
                }
            }      
            resultMap.put(nr, nodeClone);
        }

        // Step 3: add all the edges from replacement to the clone:
        for(LGraphNode nr:replacement.getNodes()) {
            LGraphNode nodeClone = resultMap.get(nr);
            for(LGraphEdge edge:nr.getEdges()) {
                clone.addEdge(nodeClone, edge.labelSet, resultMap.get(edge.end));
            }
        }        
        
        
        return clone;
    }
    
    
    public String toString() {
        String tmp =  "RULE " + name + " " + weight + " " + decay + "\n" + 
                      "    PATTERN " + pattern.toString() + "\n";
        for(LGraph g:negatedPatterns) {
            tmp +=    "    NEGATEDPATTERN " + g.toString() + "\n";
        }
        tmp +=        "    REPLACEMENT " + replacement.toString() + "\n";
        return tmp;
    }
}
