/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs;

import lgraphs.ontology.Sort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author santi
 */
public class LGraph {
    List<LGraphNode> nodes = new ArrayList<>();

    public static LGraph fromString(String string) throws Exception {
        return fromString(string, new HashMap<>());
    }

    public static LGraph fromString(String string, Map<String, LGraphNode> nodes) throws Exception {
        LGraph graph = new LGraph();
        int idx = 0;
        int l = string.length();
        do{
            // get node:
            if (idx>=l) break;
            while(string.charAt(idx)==',' || string.charAt(idx)==' ') idx++;
            LGraphNode node = null;
            {
                boolean negation = false;
                String nodeName = "";
                String nodeLabel = "";
                while(string.charAt(idx)!=':') nodeName+=string.charAt(idx++);
                idx++;
                while(string.charAt(idx)!='(') nodeLabel+=string.charAt(idx++);
                idx++;
                if (nodeLabel.startsWith("~")) {
                    nodeLabel = nodeLabel.substring(1);
                    negation = true;
                }
                node = nodes.get(nodeName);
    //            System.out.println("label: " + nodeLabel + (negation ? "(negated)":""));
    //            System.out.println("Node name: " + nodeName);
                if (node==null) {
                    if (negation) {
                        node = graph.addNotNode(Sort.getSort(nodeLabel));
                    } else {
                        node = graph.addNode(Sort.getSort(nodeLabel));
                    }
                    nodes.put(nodeName, node);
                } else {
                    if (negation) {
                        node.notLabel = Sort.getSort(nodeLabel);
                    } else {
                        node.setLabel(Sort.getSort(nodeLabel));
                    }
                }
            }

            do{
                if (idx>=l) break;
                if (string.charAt(idx)==')') {
                    idx++;
                    break;
                }
                boolean negation = false;
                String edgeLabel = "";
                String targetNodeName = "";
                while(string.charAt(idx)!=':') edgeLabel+=string.charAt(idx++);
                idx++;
                while(string.charAt(idx)!=',' && string.charAt(idx)!=')') targetNodeName+=string.charAt(idx++);
                if (string.charAt(idx)==',') idx++;
//                System.out.println("Target node name: " + targetNodeName);
                LGraphNode targetNode = nodes.get(targetNodeName);
                if (targetNode==null) {
                    targetNode = graph.addNode(Sort.getSort("any"));
                    nodes.put(targetNodeName, targetNode);
                }
                if (edgeLabel.startsWith("~")) {
                    edgeLabel = edgeLabel.substring(1);
                    negation = true;
                }
                if (negation) {
                    node.addNegationEdge(Sort.getSort(edgeLabel), targetNode);
                } else {
                    node.addEdge(Sort.getSort(edgeLabel), targetNode);
                }
            }while(true);
        }while(true);

//        System.out.println(graph);
        return graph;
    }


    public LGraph() {

    }

    public List<LGraphNode> getNodes() {
        return nodes;
    }

    public LGraphNode getNode(int n) {
        return nodes.get(n);
    }

    public LGraphNode addNode(Sort l) {
        LGraphNode n = new LGraphNode(l);
        nodes.add(n);
        return n;
    }

    public LGraphNode addNode(Sort s1, Sort s2) {
        LGraphNode n = new LGraphNode(s1, s2);
        nodes.add(n);
        return n;
    }
    public LGraphNode addNotNode(Sort l) {
        LGraphNode n = LGraphNode.newNegaTiveLGraphNode(l);
        nodes.add(n);
        return n;
    }

    public LGraphNode addNode(List<Sort> l) {
        LGraphNode n = new LGraphNode(l);
        nodes.add(n);
        return n;
    }

    public void addEdge(LGraphNode n1, Sort l, LGraphNode n2) {
        n1.addEdge(l,n2);
    }

    public boolean subsumesWithMapping(LGraph reference, Map<LGraphNode, LGraphNode> map) {
        for(LGraphNode n:nodes) {
            LGraphNode rn = map.get(n);
            if (!n.subsumes(rn)) return false;
            if (n.notLabel!=null) {
                // node label negation:
                if (rn.subsumedBy(n.notLabel)) return false;
            }
            for(LGraphEdge e:n.edges) {
                boolean found = false;
                for(LGraphEdge re:rn.edges) {
                    if (e.label.subsumes(re.label) &&
                        re.end == map.get(e.end)) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
            // edge negation:
            if (n.notEdges!=null) {
                for(LGraphEdge e:n.notEdges) {
                    for(LGraphEdge re:rn.edges) {
                        if (e.label.subsumes(re.label) &&
                            re.end == map.get(e.end)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public LGraph clone() {
        LGraph clone = new LGraph();

        for(LGraphNode node:nodes)
            clone.addNode(node.getLabels());

        int idx = 0;
        for(LGraphNode node:nodes) {
            for(LGraphEdge edge:node.edges) {
                clone.addEdge(clone.nodes.get(idx), edge.label, clone.nodes.get(nodes.indexOf(edge.end)));
            }
            idx++;
        }

        return clone;
    }

    public LGraph clone(Map<LGraphNode, LGraphNode> map) {
        LGraph clone = new LGraph();

        for(LGraphNode node:nodes) {
            map.put(node, clone.addNode(node.getLabels()));
        }

        for(LGraphNode node:nodes) {
            for(LGraphEdge edge:node.edges) {
                clone.addEdge(map.get(node), edge.label, map.get(edge.end));
            }
        }

        return clone;
    }


    public LGraph cloneSubGraph(List<Sort> nodesOfInterest, Map<LGraphNode, LGraphNode> map) {
        LGraph clone = new LGraph();

        for(LGraphNode node:nodes) {
            boolean consider = false;
            for(Sort l:nodesOfInterest) {
                if (node.subsumedBy(l)) {
                    consider = true;
                    break;
                }
            }
            if (consider) map.put(node, clone.addNode(node.getLabels()));
        }

        for(LGraphNode node:nodes) {
            if (map.containsKey(node)) {
                for(LGraphEdge edge:node.edges) {
                    if (map.containsKey(edge.end))
                        clone.addEdge(map.get(node), edge.label, map.get(edge.end));
                }
            }
        }

        return clone;
    }


    public int[][] adjacencyMatrix() {
        int n = nodes.size();
        int matrix[][] = new int[n][n];

        for(int i = 0;i<n;i++) {
            for(int j = 0;j<n;j++) {
                matrix[i][j] = 0;
            }
        }
        for(LGraphNode node:nodes) {
            for(LGraphEdge edge:node.edges) {
                matrix[nodes.indexOf(node)][nodes.indexOf(edge.end)] = 1;
            }
        }

        return matrix;
    }

    public int[][] adjacencyMatrix(List<LGraphNode> nodeSubset) {
        int n = nodeSubset.size();
        int matrix[][] = new int[n][n];

        for(int i = 0;i<n;i++) {
            for(int j = 0;j<n;j++) {
                matrix[i][j] = 0;
            }
        }
        for(LGraphNode node:nodeSubset) {
            for(LGraphEdge edge:node.edges) {
                int idx = nodeSubset.indexOf(edge.end);
                if (idx!=-1) {
                    matrix[nodeSubset.indexOf(node)][idx] = 1;
                }
            }
        }

        return matrix;
    }


    public int[][] undirectedAdjacencyMatrix() {
        int n = nodes.size();
        int matrix[][] = new int[n][n];

        for(int i = 0;i<n;i++) {
            for(int j = 0;j<n;j++) {
                matrix[i][j] = 0;
            }
        }
        for(LGraphNode node:nodes) {
            for(LGraphEdge edge:node.edges) {
                matrix[nodes.indexOf(node)][nodes.indexOf(edge.end)] = 1;
                matrix[nodes.indexOf(edge.end)][nodes.indexOf(node)] = 1;
            }
        }

        return matrix;
    }


    public int[][] undirectedAdjacencyMatrix(List<LGraphNode> nodeSubset) {
        int n = nodeSubset.size();
        int matrix[][] = new int[n][n];

        for(int i = 0;i<n;i++) {
            for(int j = 0;j<n;j++) {
                matrix[i][j] = 0;
            }
        }
        for(LGraphNode node:nodeSubset) {
            for(LGraphEdge edge:node.edges) {
                int idx = nodeSubset.indexOf(edge.end);
                if (idx!=-1) {
                    matrix[nodeSubset.indexOf(node)][idx] = 1;
                    matrix[idx][nodeSubset.indexOf(node)] = 1;
                }
            }
        }

        return matrix;
    }


    public String toString() {
        return toString("N");
    }


    public String toString(String nodePrefix) {
        String tmp = "";
        boolean first = true;
        for(LGraphNode node:nodes) {
            if (!first) tmp+=",";
            first = false;
            tmp += nodePrefix + nodes.indexOf(node) + ":";
            boolean first2 = true;
            for(Sort s:node.labels) {
                if (first2) {
                    tmp+=s.getName();
                    first2 = false;
                } else {
                    tmp+="," + s.getName();
                }
            }
            if (node.notLabel!=null) tmp += "~" + node.notLabel;
            tmp += "(";
            boolean first3 = true;
            for(LGraphEdge edge:node.edges) {
                if (!first3) tmp+=",";
                first3 = false;
                tmp+= edge.label + ":" + nodePrefix + nodes.indexOf(edge.end);
            }
            if (node.notEdges!=null) {
                for(LGraphEdge edge:node.notEdges) {
                    if (!first3) tmp+=",";
                    first3 = false;
                    tmp+= "~" + edge.label + ":" + nodePrefix + nodes.indexOf(edge.end);
                }
            }
            tmp += ")";
        }
        return tmp;
    }

}
