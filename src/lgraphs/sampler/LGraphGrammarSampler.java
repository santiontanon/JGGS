/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

import java.util.ArrayList;
import java.util.Arrays;
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
    public static int DEBUG = 0;
    
    LGraphRewritingGrammar grammar;
    LGraph currentGraph;
    Random r;
    Sampler s;
    boolean objectIdentity;
    
    HashMap<String,Integer> ruleApplicationCounts = new LinkedHashMap<String,Integer>();
    HashMap<String,Double> currentRuleWeights = new LinkedHashMap<String,Double>();
    HashMap<String,Double> currentRuleDecay = new LinkedHashMap<String,Double>();    
    HashMap<String,Integer> ruleApplicationLimit = new LinkedHashMap<String,Integer>();

        

    public LGraphGrammarSampler(LGraphGrammarSampler lggs) {
        this(lggs.currentGraph, lggs.grammar, lggs.objectIdentity, lggs.r);
        ruleApplicationCounts.putAll(lggs.ruleApplicationCounts);
        currentRuleWeights.putAll(lggs.currentRuleWeights);
        currentRuleDecay.putAll(lggs.currentRuleDecay);
        ruleApplicationLimit.putAll(lggs.ruleApplicationLimit);
    }


    public LGraphGrammarSampler(LGraph a_graph, LGraphRewritingGrammar a_grammar, boolean a_objectIdentity) {
        this(a_graph, a_grammar, a_objectIdentity, new Random());
    }
    
    
    public LGraphGrammarSampler(LGraph a_graph, LGraphRewritingGrammar a_grammar, boolean a_objectIdentity, Random a_r) {
        currentGraph = a_graph;
        grammar = a_grammar;
        r = a_r;
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
    
    
    public HashMap<String, Integer> getRuleApplicationCounts() {
        return ruleApplicationCounts;
    }

    
    public void setRuleApplicationCounts(HashMap<String, Integer> ruleApplicationCounts) {
        this.ruleApplicationCounts = ruleApplicationCounts;
    }
    
    
    public LGraph getCurrentGraph()
    {
        return currentGraph;
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
    
    
    public boolean ruleWithTagWasTriggered(String tag) {
        for(LGraphRewritingRule rule:grammar.getRules()) {
            if (ruleApplicationCounts.get(rule.name) != null &&
                ruleApplicationCounts.get(rule.name) > 0) {
                if (rule.tags.contains(tag)) {
                    return true;
                }
            }
        }
        return false;
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
        if (DEBUG>=1) System.out.println("Getting pottential applications:");
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
        if (DEBUG>=1) System.out.println("results: " + results.size());

        if (results.isEmpty()) return null;
        
        double probabilities[] = new double[ruleWeights.size()];
        for(int i = 0;i<probabilities.length;i++) {
            probabilities[i] = ruleWeights.get(i)/totalWeight;
        }            
        if (DEBUG>=1) System.out.println("Applicable rules: " + differentRuleNames);
        if (DEBUG>=1) System.out.println("Weights: " + ruleWeights);
        int selected = s.weighted(probabilities);
        String selectedRuleName = differentRuleNames.get(selected);
        if (DEBUG>=1) System.out.println("Rule fired: " + selectedRuleName);
        
        List<LGraph> filteredResults = new LinkedList<LGraph>();
        for(int i = 0;i<results.size();i++) {
            if (ruleNames.get(i).equals(selectedRuleName)) filteredResults.add(results.get(i));
        }
        
        int idx = r.nextInt(filteredResults.size());
        currentGraph = filteredResults.get(idx);
        addRuleApplication(selectedRuleName);
        if (DEBUG>=1) System.out.println("   rule selected: " + selectedRuleName);
        currentRuleWeights.put(selectedRuleName, 
                               currentRuleWeights.get(selectedRuleName) * currentRuleDecay.get(selectedRuleName));
        return currentGraph;
    }
    
    
    public List<LGraphGrammarSampler> allPossibleSuccessors() throws Exception
    {
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
                String ruleName = matcher.getLastRuleFired().getName();
                ruleNames.add(ruleName);
            }
        } while(result!=null);

        if (results.isEmpty()) return new ArrayList<LGraphGrammarSampler>();
        

        List<LGraphGrammarSampler> l = new ArrayList<LGraphGrammarSampler>();
        
        //System.out.println(ruleNames);
        
        for(int idx = 0;idx<results.size();idx++) {
            LGraphGrammarSampler child = new LGraphGrammarSampler(this);
            String selectedRuleName = ruleNames.get(idx);
            child.currentGraph = results.get(idx);
            child.addRuleApplication(selectedRuleName);
            if (DEBUG>=1) System.out.println("   rule selected: " + selectedRuleName);
            child.currentRuleWeights.put(selectedRuleName, 
                                         child.currentRuleWeights.get(selectedRuleName) * child.currentRuleDecay.get(selectedRuleName));            
            l.add(child);
        }
        
        return l;
    }    
    
}
