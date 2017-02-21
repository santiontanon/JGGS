/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tests;

import java.util.Random;
import lgraphs.LGraph;
import lgraphs.ontology.Ontology;
import lgraphs.sampler.LGraphGrammarSampler;
import lgraphs.sampler.LGraphGrammarSystematicDFSIterator;
import lgraphs.sampler.LGraphRewritingGrammar;

/**
 *
 * @author santi
 */
public class SystematicApplicationTest {
    public static void main(String args[]) throws Exception {       
        long randomSeed = 0;
        Ontology ontology = new Ontology("data/ppppOntology4.xml");
        
        LGraph g1 = LGraph.fromString("N0:problem()");
        LGraphRewritingGrammar grammar = LGraphRewritingGrammar.load("data/ppppGrammar4a.txt");
        LGraphGrammarSampler generator = new LGraphGrammarSampler(g1, grammar, true, new Random(randomSeed));

        LGraphGrammarSystematicDFSIterator iterator = new LGraphGrammarSystematicDFSIterator(generator, false);
        LGraphGrammarSampler next = iterator.getNext();
        int count = 0;
        while(next!=null) {
            System.out.println("---- " + count + " ----");
            System.out.println(next.getRuleApplicationCounts());
            System.out.println(next.getCurrentGraph());
            count++;
            next = iterator.getNext();
        }
    }
}
