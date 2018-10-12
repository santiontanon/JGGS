/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

import java.util.ArrayList;
import lgraphs.LGraph;
import lgraphs.LGraphEdge;
import lgraphs.LGraphNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author santi
 */
public class LGraphRewritingRule {
    public static int DEBUG = 0;
    String name;            // rules can have a name, with the purposes ot informing the user
                            // which rules were fired.
    public double weight;   // If the generation algorithm is stochastic, use this weight to 
                            // compute the probability of firing this rule versus other rules
                            // The probability will be "weight/sum of weights"
    double decay;           // The decay with which the weight decreases after each application
    int applicationLimit;   // Optional ruleApplicationLimit from grammar file, -1 for no limit
    List<String> tags;           // Optional string to group the rules
    
    LGraph pattern;
    List<LGraph> negatedPatterns;
    List<Map<LGraphNode, LGraphNode>> mapsFromNegatedPatterns;    // mapping between the nodes of negated patterns to pattern
    
    LGraph replacement;
    Map<LGraphNode, LGraphNode> replacementMap;    // mapping between the nodes of replacement to pattern
    List<LGraphNode> nodesInPatternAndNotInReplacement;
    
    public LGraphRewritingRule(LGraph a_pattern, LGraph a_result, Map<LGraphNode, LGraphNode> a_map) {
        name = null;
        pattern = a_pattern;
        negatedPatterns = null;
        mapsFromNegatedPatterns = null;
        replacement = a_result;
        replacementMap = a_map;
        tags = new ArrayList();
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
        tags = new ArrayList();
        
        nodesInPatternAndNotInReplacement = new ArrayList<LGraphNode>();
        for(LGraphNode n:pattern.getNodes()) {
            LGraphNode found = null;
            for(LGraphNode n2:replacement.getNodes()) {
                if (replacementMap.get(n2)==n) {
                    found = n2;
                    break;
                }
            }
            if (found==null) nodesInPatternAndNotInReplacement.add(n);
        }
    }
    

    public LGraphRewritingRule(String a_name, double a_weight, double a_decay, LGraph a_pattern, 
                               List<LGraph> a_negatedPatterns, List<Map<LGraphNode, LGraphNode>> a_mapsFromNegatedPatterns,
                               LGraph a_replacement, Map<LGraphNode, LGraphNode> a_replacementMap, int a_applicationLimit, List<String> a_tags) {
        name = a_name;
        weight = a_weight;
        decay = a_decay;
        pattern = a_pattern;
        negatedPatterns = a_negatedPatterns;
        mapsFromNegatedPatterns = a_mapsFromNegatedPatterns;
        replacement = a_replacement;
        replacementMap = a_replacementMap;
        applicationLimit = a_applicationLimit;
        tags = a_tags;

        nodesInPatternAndNotInReplacement = new ArrayList<LGraphNode>();
        for(LGraphNode n:pattern.getNodes()) {
            LGraphNode found = null;
            for(LGraphNode n2:replacement.getNodes()) {
                if (replacementMap.get(n2)==n) {
                    found = n2;
                    break;
                }
            }
            if (found==null) nodesInPatternAndNotInReplacement.add(n);
        }
    }


    public LGraph applyRule(LGraph graph, Map<LGraphNode, LGraphNode> matching) {
        Map<LGraphNode, LGraphNode> cloneMap = new LinkedHashMap<LGraphNode, LGraphNode>();
        LGraph clone = graph.clone(cloneMap);

        if (DEBUG>=1) System.out.println("applyRule: Applying rule " + name);
        
        // Step 1: remove all the edges from the pattern in the clone:
        List<LGraphEdge> edgesToRemove = new ArrayList<LGraphEdge>();
        for(LGraphNode patternNode:pattern.getNodes()) {
            LGraphNode nodeClone = cloneMap.get(matching.get(patternNode));
            for(LGraphEdge patternEdge:patternNode.getEdges()) {
                LGraphEdge found = null;
                for(LGraphEdge edgeClone:nodeClone.getEdges()) {
                    if (patternEdge.labelSet.subsumes(edgeClone.labelSet) &&
                        cloneMap.get(matching.get(patternEdge.end))==edgeClone.end) {
                        found = edgeClone;
                        break;
                    }
                }
                if (found==null) {
                    System.out.flush();
                    System.err.println("LGraphRewritingRule.applyRule: matching edge for N"+pattern.getNodes().indexOf(patternEdge.start)+
                                       "->N"+pattern.getNodes().indexOf(patternEdge.end)+" not found!!");
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
                edgesToRemove.add(found);
            }
        }
        if (DEBUG>=1) System.out.println("Step 1: removing edges:");
        for(LGraphEdge e:edgesToRemove) {
            if (DEBUG>=1) System.out.println("    " + e);
            e.start.getEdges().removeAll(edgesToRemove);
        }
        
        // Step 2: add all the nodes from replacement to the clone:
        if (DEBUG>=1) System.out.println("Step 2: adding nodes: " + replacement.getNodes().size());
        Map<LGraphNode, LGraphNode> resultMap = new LinkedHashMap<LGraphNode, LGraphNode>();
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
            if (DEBUG>=1) System.out.println("    Step 2 "+name+": RN" + +replacement.getNodes().indexOf(nr) + "("+nr.labelSet+") maps to " + clone.getNodes().indexOf(nodeClone));
        }
        
        // Step 3: remove nodes that are in the pattern, but not in the replacement 
        // (Except if they have links to nodes outside of the nodes matched by the pattern):
        // (another exception is when object identity is not enforced, and we have nodes that map
        //  to the same node in the original graph)
        if (DEBUG>=1) System.out.println("Step 3 "+name+": removing nodes, testing " + nodesInPatternAndNotInReplacement.size() + " nodes");
        List<LGraphNode> image = new ArrayList<LGraphNode>();
        image.addAll(matching.values());
        for(LGraphNode n:nodesInPatternAndNotInReplacement) {
            boolean remove = true;
            LGraphNode toRemove = matching.get(n);
            for(LGraphEdge e:toRemove.getEdges()) {
                if (!image.contains(e.end)) {
                    // the node has an edge to outside of the image:
                    remove = false;
                    break;
                }
            }
            
            for(LGraphNode n2:graph.getNodes()) {
                if (image.contains(n2)) continue;
                for(LGraphEdge e:n2.getEdges()) {
                    if (e.end == toRemove) {
                        // the node has an incoming edge from outside of the image:
                        remove = false;
                        break;
                    }
                }
            }
            
            for(LGraphNode n2:pattern.getNodes()) {
                if (matching.get(n2) == matching.get(n)) {
                    if (!nodesInPatternAndNotInReplacement.contains(n2)) {
                        // another node 'n2' maps to the same node in the graph that 'n' maps to,
                        // and 'n2' does not have to be removed
                        // (this can only happen when object identity is not enforced)
                        remove = false;
                        break;
                    }
                }
            }
            
            if (remove) {
                if (DEBUG>=1) System.out.println("    removing node");
                LGraphNode clone_n = cloneMap.get(matching.get(n));
                if (DEBUG>=1) System.out.println("    Step 3 "+name+": remove pattern node " + pattern.getNodes().indexOf(n) + "(graph node: " + clone.getNodes().indexOf(clone_n) + ")");
                clone.removeNodeAndAllConnections(clone_n);
            }
        }

        // Step 4: add all the edges from replacement to the clone:
        for(LGraphNode nr:replacement.getNodes()) {
            LGraphNode nodeClone = resultMap.get(nr);
            for(LGraphEdge edge:nr.getEdges()) {
                LGraphNode nodeClone2 = resultMap.get(edge.end);
                if (DEBUG>=1) System.out.println("    Step 4 "+name+": adding edge " + clone.getNodes().indexOf(nodeClone) + " -("+edge.labelSet+")-> " + clone.getNodes().indexOf(nodeClone2));
                clone.addEdge(nodeClone, edge.labelSet, nodeClone2);
            }
        }        
        
        if (DEBUG>=1) System.out.println(" + ---------------- + ");
        
        return clone;
    }
    
    
    public String toString() {
        String tmp =  "RULE " + name + " " + weight + " " + decay + "\n" + 
                      "    PATTERN " + pattern.toString() + "\n";
        for(int i = 0;i<negatedPatterns.size();i++) {
            LGraph g = negatedPatterns.get(i);
            tmp +=    "    NEGATEDPATTERN " + g.toString() + "\n";
            Map<LGraphNode, LGraphNode> m = mapsFromNegatedPatterns.get(i);
            int j = 0;
            for(LGraphNode n:m.keySet()) {
                tmp += ("        N" + j + " --> N" + pattern.getNodes().indexOf(m.get(n))) + "\n";
                j++;
            }
        }
        tmp +=        "    REPLACEMENT " + replacement.toString() + "\n";
        int j = 0;
        for(LGraphNode n:replacementMap.keySet()) {
            tmp += ("        N" + j + " --> N" + pattern.getNodes().indexOf(replacementMap.get(n))) + "\n";
            j++;
        }
        tmp += "        to remove: ";
        for(LGraphNode n:nodesInPatternAndNotInReplacement) {
            tmp += "N" + pattern.getNodes().indexOf(n) + " ";
        }
        tmp += "\n";
        return tmp;
    }
    
    
    public String getName()
    {
        return name;
    }
    
    
    public List<String> getTags()
    {
        return tags;
    }
    
    
    public LGraph getPattern()
    {
        return pattern;
    }

    
    public List<LGraph> getNegatedPatterns()
    {
        return negatedPatterns;
    }

    
    public LGraph getReplacement()
    {
        return replacement;
    }

}
