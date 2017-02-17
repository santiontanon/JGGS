/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

import lgraphs.LGraph;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import util.Sampler;

/**
 *
 * @author santi
 */
public class LGraphGrammarSampler {
    LGraphRewritingGrammar grammar;
    LGraph currentGraph;
    Random r;
    Sampler s;
    boolean objectIdentity;
    
    HashMap<String,Integer> ruleApplicationCounts = new LinkedHashMap<String,Integer>();
    HashMap<String,Double> currentRuleWeights = new LinkedHashMap<String,Double>();
    HashMap<String,Double> currentRuleDecay = new LinkedHashMap<String,Double>();
    
    HashMap<String,Integer> ruleApplicationLimit = new LinkedHashMap<String,Integer>();
    
    public LGraphGrammarSampler(LGraph a_graph, LGraphRewritingGrammar a_grammar, boolean a_objectIdentity) {
        this(a_graph, a_grammar, a_objectIdentity, null);
    }
    
    public LGraphGrammarSampler(LGraph a_graph, LGraphRewritingGrammar a_grammar, boolean a_objectIdentity, Long randomSeed) {
        currentGraph = a_graph;
        grammar = a_grammar;
        if(randomSeed!=null){
            r = new Random(randomSeed);
        } else {
            r = new Random();
        }
        s = new Sampler(r);
        objectIdentity = a_objectIdentity;
        for(LGraphRewritingRule rule:grammar.rules) {
            currentRuleWeights.put(rule.getName(), rule.weight);
            currentRuleDecay.put(rule.getName(), rule.decay);
            if(rule.applicationLimit>-1){
                ruleApplicationLimit.put(rule.name, rule.applicationLimit);
            }
        }
    }
    

    
    public void addApplicationLimit(String ruleName, int limit) {
        ruleApplicationLimit.put(ruleName, limit);
    }
    
    
    public void printRuleApplicationCounts() {
        System.out.println("Rule application summary:");
        for(String rule:ruleApplicationCounts.keySet()) {
            System.out.println("  " + rule + ": " + ruleApplicationCounts.get(rule));
        }
    }
    
    
    protected void addRuleApplication(String name) {
        Integer count = ruleApplicationCounts.get(name);
        if (count==null) {
            ruleApplicationCounts.put(name,1);
        } else {
            ruleApplicationCounts.put(name, ruleApplicationCounts.get(name)+1);
        }
    }
    
    
    // Applies the first rule that can be applied:
    // Does not update weights
    public LGraph applyFirstRule() {
        LGraphGrammarMatcher matcher = new LGraphGrammarMatcher(currentGraph, grammar, objectIdentity);
        // enforce the maximum number of applications of a given rule:
        for(String ruleName:ruleApplicationLimit.keySet()) {
            Integer count = ruleApplicationCounts.get(ruleName);
            if (count==null) count = 0;
            if (count>=ruleApplicationLimit.get(ruleName)) {
                matcher.forbidRule(ruleName);
            }
        }        
        LGraph result = matcher.getNextResult();
        if (result!=null) {
            currentGraph = result;
            addRuleApplication(matcher.getLastRuleFired().getName());
        }
        return result;
    }
    
    // Generates all the possible applications of rules, and then selects one at random
    // Does not update weights
    public LGraph applyRuleRandomly() {
        List<LGraph> results = new LinkedList<LGraph>();
        List<String> ruleNames = new LinkedList<String>();
        LGraphGrammarMatcher matcher = new LGraphGrammarMatcher(currentGraph, grammar, objectIdentity);
        // enforce the maximum number of applications of a given rule:
        for(String ruleName:ruleApplicationLimit.keySet()) {
            Integer count = ruleApplicationCounts.get(ruleName);
            if (count==null) count = 0;
            if (count>=ruleApplicationLimit.get(ruleName)) {
                matcher.forbidRule(ruleName);
            }
        }        
        LGraph result = null;
        do {
            result = matcher.getNextResult();
            if (result!=null) {
                results.add(result);
                ruleNames.add(matcher.getLastRuleFired().getName());
            }
        } while(result!=null);
        if (results.isEmpty()) return null;
        int idx = r.nextInt(results.size());
        currentGraph = results.get(idx);
        addRuleApplication(ruleNames.get(idx));
        return currentGraph;
    }
    
    // Generates all the possible applications of rules, and then selects one stochastically,
    // based on the current rule weights. Then updates the weights with their respective decays
    public LGraph applyRuleStochastically() throws Exception {
        List<LGraph> results = new LinkedList<LGraph>();
        List<String> ruleNames = new LinkedList<String>();
        List<String> differentRuleNames = new LinkedList<String>();
        List<Double> ruleWeights = new LinkedList<Double>();
        double totalWeight = 0;
        LGraphGrammarMatcher matcher = new LGraphGrammarMatcher(currentGraph, grammar, objectIdentity);
        // enforce the maximum number of applications of a given rule:
        for(String ruleName:ruleApplicationLimit.keySet()) {
            Integer count = ruleApplicationCounts.get(ruleName);
            if (count==null) count = 0;
            if (count>=ruleApplicationLimit.get(ruleName)) {
                matcher.forbidRule(ruleName);
            }
        }        
        LGraph result = null;
        do {
            result = matcher.getNextResult();
            if (result!=null) {
                results.add(result);
                String ruleName = matcher.getLastRuleFired().getName();
                ruleNames.add(ruleName);
                if (!differentRuleNames.contains(ruleName)) {
                    differentRuleNames.add(ruleName);
                    double weight = currentRuleWeights.get(ruleName);
                    ruleWeights.add(weight);
                    totalWeight += weight;
                }
            }
        } while(result!=null);

        if (results.isEmpty()) return null;
        
        double probabilities[] = new double[ruleWeights.size()];
        for(int i = 0;i<probabilities.length;i++) {
            probabilities[i] = ruleWeights.get(i)/totalWeight;
        }            
//        System.out.println(differentRuleNames);
//        System.out.println(ruleWeights);
        int selected = s.weighted(probabilities);
        String selectedRuleName = differentRuleNames.get(selected);
        
        List<LGraph> filteredResults = new LinkedList<LGraph>();
        for(int i = 0;i<results.size();i++) {
            if (ruleNames.get(i).equals(selectedRuleName)) filteredResults.add(results.get(i));
        }
        
        int idx = r.nextInt(filteredResults.size());
        currentGraph = filteredResults.get(idx);
        addRuleApplication(selectedRuleName);
        System.out.println("   rule selected: " + selectedRuleName);
        currentRuleWeights.put(selectedRuleName, 
                               currentRuleWeights.get(selectedRuleName) * currentRuleDecay.get(selectedRuleName));
        return currentGraph;
    }
    
}
