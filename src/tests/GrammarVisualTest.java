/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import lgraphs.LGraph;
import lgraphs.LGraphNode;
import lgraphs.ontology.Ontology;
import lgraphs.ontology.Sort;
import lgraphs.sampler.LGraphGrammarSampler;
import lgraphs.sampler.LGraphRewritingGrammar;
import lgraphs.visualization.LGraphVisualizer;

/**
 *
 * @author santi
 */
public class GrammarVisualTest {
    public static void main(String args[]) throws Exception {
        Ontology ontology = new Ontology("data/sampleOntology.xml");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/sampleGrammar.txt");
        
        // Create an initial graph:
        LGraph graph = LGraph.fromString("N0:game()");
        LGraph lastGraph = graph;
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true);
        
        // Set some limits to ensure the graph is always finite:
        generator.addApplicationLimit("SEQUENTIAL-TASKS", 3);
        generator.addApplicationLimit("TASK-CHOICE", 3);
        generator.addApplicationLimit("PARALLEL-UNLOCK-TASKS", 3);
        
        // Use the grammar to rewrite the graph:
        do{
            System.out.println("Current graph:");
            System.out.println("  " + graph);
            graph = generator.applyRuleStochastically();
            if (graph!=null) lastGraph = graph;
        }while(graph!=null);
        
        generator.printRuleApplicationCounts();
        
        JFrame v = LGraphVisualizer.newWindow("Resulting graph", 1200, 600, lastGraph);
        List<Sort> ll = new LinkedList<Sort>();
        ll.add(Sort.getSort("location"));
        ll.add(Sort.getSort("lock"));
        LGraph locationGraph = lastGraph.cloneSubGraph(ll, new HashMap<LGraphNode, LGraphNode>());
        JFrame v2 = LGraphVisualizer.newWindow("Location graph", 1200, 600, locationGraph);
    }
}
