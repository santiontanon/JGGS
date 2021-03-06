/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tests;

import java.util.Random;
import lgraphs.LGraph;
import lgraphs.ontology.Ontology;
import lgraphs.ontology.Sort;
import lgraphs.sampler.LGraphGrammarSampler;
import lgraphs.sampler.LGraphRewritingGrammar;
import lgraphs.sampler.LGraphRewritingRule;

/**
 *
 * @author santi
 */
public class GrammarUnitTests {
    public static void main(String args[]) throws Exception {
//        testEdgeNotFound();
//        testCreatesAnyNodes();
//        testIfCIsDeleted();
        testNodeShouldNotBeDeleted();
//        testShouldGenerateOnlyOneFirst();
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
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true);
                
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
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true);
                
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
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true);
                
        generator.addApplicationLimit("MAKE_SUBPROBLEM_ABST_SERIAL_TASKS", 1);
        
        // Use the grammar to rewrite the graph:
        do{
            System.out.println("Current graph:");
            System.out.println("  " + graph);
            graph = generator.applyRuleStochastically();
        }while(graph!=null);
        
        generator.printRuleApplicationCounts();
    }
    
    
    /*
    To see if this test succeeds, make sure that the rule THIS_WORKS_NOT does not delete any node.
    */
     public static void testNodeShouldNotBeDeleted() throws Exception 
    {
//        LGraph.DEBUG = 1;
        
        System.out.println("\n------------------------------------------------\n");
        Sort.clearSorts();
        Ontology ontology = new Ontology("data/sampleOntology2.xml");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/error_not_deleting.txt");
        
        System.out.println(grammar);
        
        // Create an initial graph:
        LGraph graph = LGraph.fromString("N0:problem()");
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true);
                
        generator.addApplicationLimit("THIS_WORKS", 1);
        generator.addApplicationLimit("THIS_WORKS_NOT", 1);
        
//        LGraphRewritingRule.DEBUG = 1;
        
        // Use the grammar to rewrite the graph:
        do{
            System.out.println("Current graph:");
            System.out.println("  " + graph);
            graph = generator.applyRuleStochastically();
        }while(graph!=null);
        
        generator.printRuleApplicationCounts();
    }
    
     
     
    public static void testShouldGenerateOnlyOneFirst() throws Exception {       
        Ontology ontology = new Ontology("data/ppppOntology4.xml");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/ppppGrammar4c.txt");

        System.out.println(grammar);
        
        // Create an initial graph:
        LGraph graph = LGraph.fromString("A:fork(to:B,to:C),B:track(),C:track()");
        LGraph lastGraph = graph;
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true, new Random(0L));
                
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
