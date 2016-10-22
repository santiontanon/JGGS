/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs.sampler;

import lgraphs.LGraph;
import lgraphs.LGraphNode;
import lgraphs.ontology.Sort;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author santi
 */
public class LGraphRewritingGrammar {
    List<LGraphRewritingRule> rules;
    
    public LGraphRewritingGrammar() {
        rules = new LinkedList<>();
    }
    
    
    public void addRule(LGraphRewritingRule rule) {
        rules.add(rule);
    }
    
    
    public static LGraphRewritingGrammar load(String fileName) throws Exception {
        LGraphRewritingGrammar grammar = new LGraphRewritingGrammar();
        
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        
        do{
            String line = br.readLine();
            if (line==null) break;
            StringTokenizer st = new StringTokenizer(line," ");
            if (!st.hasMoreTokens()) continue;
            String token = st.nextToken();
            if (token.equals("Label")) {
                // load the label:
                Sort.newSort(st.nextToken(), st.nextToken());
            } else if (token.equals("Rule")) {
                // Load the rule:
                String ruleName = st.nextToken();
                double weight = Double.parseDouble(st.nextToken());
                double decay = Double.parseDouble(st.nextToken());
                String patternString = br.readLine().trim();
                String resultString = br.readLine().trim();
                Map<String, LGraphNode> patternMap = new HashMap<>();
                Map<String, LGraphNode> resultMap = new HashMap<>();
                LGraph pattern = LGraph.fromString(patternString, patternMap);
                LGraph result = LGraph.fromString(resultString, resultMap);
                Map<LGraphNode, LGraphNode> map = new HashMap<>();
                for(String nodeName:resultMap.keySet()) {
                    if (patternMap.containsKey(nodeName)) {
                        map.put(resultMap.get(nodeName), patternMap.get(nodeName));
                    }
                }
                grammar.addRule(new LGraphRewritingRule(ruleName, weight, decay, pattern, result, map));
            }
            
        }while(true);
        
        return grammar;
    }
    
}
