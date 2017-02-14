/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tests;

import lgraphs.LGraph;
import lgraphs.ontology.Ontology;
import lgraphs.sampler.LGraphGrammarSampler;
import lgraphs.sampler.LGraphRewritingGrammar;

/**
 *
 * @author santi
 */
public class GrammarTest {
    public static void main(String args[]) throws Exception {       
        Ontology ontology = new Ontology("data/sampleOntology.xml");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/sampleGrammar.txt");

        System.out.println(grammar);
        
        // Create an initial graph:
        LGraph graph = LGraph.fromString("N0:game()");
        LGraph lastGraph = graph;
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true, 0);
        
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
    }
}
