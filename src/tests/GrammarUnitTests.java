/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tests;

import lgraphs.LGraph;
import lgraphs.ontology.Ontology;
import lgraphs.ontology.Sort;
import lgraphs.sampler.LGraphGrammarSampler;
import lgraphs.sampler.LGraphRewritingGrammar;

/**
 *
 * @author santi
 */
public class GrammarUnitTests {
    public static void main(String args[]) throws Exception {
        testEdgeNotFound();
        testCreatesAnyNodes();
        testIfCIsDeleted();
    }
    
    
    public static void testEdgeNotFound() throws Exception 
    {
        System.out.println("\n------------------------------------------------\n");
        Sort.clearSorts();
        Ontology ontology = new Ontology("data/sampleOntology2.xml");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/error_exception.txt");
        
        System.out.println(grammar);
        
        // Create an initial graph:
        LGraph graph = LGraph.fromString("N0:problem()");
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar);
                
        // Use the grammar to rewrite the graph:
        do{
            System.out.println("Current graph:");
            System.out.println("  " + graph);
            graph = generator.applyRuleStochastically();
        }while(graph!=null);
        
        generator.printRuleApplicationCounts();
    }


    public static void testCreatesAnyNodes() throws Exception 
    {
//        LGraph.DEBUG = 1;
        
        System.out.println("\n------------------------------------------------\n");
        Sort.clearSorts();
        Ontology ontology = new Ontology("data/sampleOntology2.xml");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/error_creates_any_nodes.txt");
        
        System.out.println(grammar);
        
        // Create an initial graph:
        LGraph graph = LGraph.fromString("N0:problem()");
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar);
                
        // Use the grammar to rewrite the graph:
        do{
            System.out.println("Current graph:");
            System.out.println("  " + graph);
            graph = generator.applyRuleStochastically();
        }while(graph!=null);
        
        generator.printRuleApplicationCounts();
    }

    
    public static void testIfCIsDeleted() throws Exception 
    {
//        LGraph.DEBUG = 1;
        
        System.out.println("\n------------------------------------------------\n");
        Sort.clearSorts();
        Ontology ontology = new Ontology("data/sampleOntology2.xml");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/error_shouldnt_c_be_deleted.txt");
        
        System.out.println(grammar);
        
        // Create an initial graph:
        LGraph graph = LGraph.fromString("N0:problem()");
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar);
                
        generator.addApplicationLimit("MAKE_SUBPROBLEM_ABST_SERIAL_TASKS", 1);
        
        // Use the grammar to rewrite the graph:
        do{
            System.out.println("Current graph:");
            System.out.println("  " + graph);
            graph = generator.applyRuleStochastically();
        }while(graph!=null);
        
        generator.printRuleApplicationCounts();
    }
    
}
