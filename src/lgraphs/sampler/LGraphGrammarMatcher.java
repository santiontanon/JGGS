/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

import lgraphs.LGraph;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author santi
 */
public class LGraphGrammarMatcher {
    LGraph graph;
    LGraphRewritingGrammar grammar;
    int currentRule;
    LGraphRuleMatcher matcher;
    List<String> forbiddenRules = new LinkedList<String>();
    
    public LGraphGrammarMatcher(LGraph a_graph, LGraphRewritingGrammar a_grammar) {
        graph = a_graph;
        grammar = a_grammar;
        currentRule = -1;
        matcher = null;
    }
    
    
    public void forbidRule(String ruleName) {
        forbiddenRules.add(ruleName);
    }
    
    
    public LGraph getNextResult() {
        if (currentRule>=grammar.rules.size()) return null;
        do{
            // matcher is null either the first time, or when we have run out of matches for the current rule, so move on to the next rule:
            if (matcher==null) {
                LGraphRewritingRule rule = null;
                do {
                    currentRule++;
                    if (currentRule>=grammar.rules.size()) return null;
                    rule = grammar.rules.get(currentRule);
                } while(forbiddenRules.contains(rule.name));
                matcher = new LGraphRuleMatcher(graph, rule);
            }
        
            LGraph next = matcher.getNextResult();
            if (next!=null) return next;
            matcher = null;
        }while(true);
    }
    
    public LGraphRewritingRule getLastRuleFired() {
        if (currentRule<0) return null;
        if (currentRule>=grammar.rules.size()) return null;
        return grammar.rules.get(currentRule);
    }
    
}
