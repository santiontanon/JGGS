/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tests;

import java.util.Random;
import javax.swing.JFrame;
import lgraphs.LGraph;
import lgraphs.ontology.Ontology;
import lgraphs.sampler.LGraphGrammarSampler;
import lgraphs.sampler.LGraphRewritingGrammar;
import lgraphs.visualization.LGraphVisualizer;

/**
 *
 * @author santi
 */
public class SequentialGrammarTest {
    public static void main(String args[]) throws Exception {       
        long randomSeed = 0;
        Ontology ontology = new Ontology("data/ppppOntology4.xml");
        
        LGraph g1 = LGraph.fromString("N0:problem()");
                
        LGraph g2 = applyGrammar(ontology, "data/ppppGrammar4a.txt", g1, randomSeed);
        LGraph g3 = applyGrammar(ontology, "data/ppppGrammar4b.txt", g2, randomSeed);
        LGraph g4 = applyGrammar(ontology, "data/ppppGrammar4c.txt", g3, randomSeed);
        
        JFrame v = LGraphVisualizer.newWindow("Resulting graph", 1200, 600, g4);
//        List<Sort> ll = new LinkedList<Sort>();
//        ll.add(Sort.getSort("location"));
//        ll.add(Sort.getSort("lock"));
//        LGraph locationGraph = g4.cloneSubGraph(ll, new HashMap<LGraphNode, LGraphNode>());
//        JFrame v2 = LGraphVisualizer.newWindow("Location graph", 1200, 600, locationGraph);
    }
    
    
    public static LGraph applyGrammar(Ontology ontology, String grammarFile, LGraph graph, long randomSeed) throws Exception {
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load(grammarFile);
        
//        System.out.println(grammar);
        
        LGraph lastGraph = graph;
        LGraphGrammarSampler generator = new LGraphGrammarSampler(graph, grammar, true, new Random(randomSeed));
                
        // Use the grammar to rewrite the graph:
        do{
            System.out.println("Current graph:");
            System.out.println("  " + graph);
            graph = generator.applyRuleStochastically();
            if (graph!=null) lastGraph = graph;
        }while(graph!=null);
        
        System.out.println("------------------------------------------");
        System.out.println(lastGraph);

        return lastGraph;
    }
}
