/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author santi
 * 
 */
public class LGraphEquivalence {
        
    public static boolean equivalents(LGraph g1, LGraph g2) {        
        int m = g1.getNodes().size();
        
        if (g2.getNodes().size()!=m) return false;
        
        List<LGraphNode> []options = new List[m];
        int []status = null;
        for(int i = 0;i<m;i++) {
            LGraphNode pn = g1.getNodes().get(i);
            options[i] = new ArrayList<LGraphNode>();
            for(LGraphNode rn:g2.getNodes()) {
                if (pn.subsumes(rn) && rn.subsumes(pn) &&
                    pn.getEdges().size() == rn.getEdges().size()) {
                    options[i].add(rn);
                }
            }
            if (options[i].isEmpty()) return false;
        }
        List<LGraphNode> []toRemoveOptions = new List[m];
        for(int i = 0;i<m;i++) {
            toRemoveOptions[i] = new ArrayList<LGraphNode>();
            for(LGraphEdge e1:g1.getNodes().get(i).getEdges()) {
                boolean found = false;
                LGraphNode g1n2 = e1.end;
                int idxg1n2 = g1.getNodes().indexOf(g1n2);
                for(LGraphNode g2n1:options[i]) {
                    for(LGraphEdge e2:g2n1.getEdges()) {
                        LGraphNode g2n2 = e2.end;
                        if (e2.labelSet.subsumes(e1.labelSet) &&
                            e1.labelSet.subsumes(e2.labelSet)) {
                            if (options[idxg1n2].contains(g2n2)) {
                                found = true;
                                break;
                            }
                        }
                    }                 
                    if (!found) {
                        toRemoveOptions[i].add(g2n1);
                    }
                }
            }            
        }
        for(int i = 0;i<m;i++) {
            options[i].removeAll(toRemoveOptions[i]);
            if (options[i].isEmpty()) return false;
        }
//        System.out.println(Arrays.toString(options));

        status = new int[m];
        for(int i = 0;i<m;i++) status[i] = 0;
        if (!statusSatisfiesObjectIdentity(status, options)) {
            return incrementStatus(status, options, g1, g2);
        }
        return true;
    }
    
    
    public static  boolean statusSatisfiesObjectIdentity(int []status, List<LGraphNode> []options)
    {
        for(int i = 0;i<status.length;i++) {
            for(int j = i+1;j<status.length;j++) {
                if (options[i].get(status[i]) == options[j].get(status[j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    public static boolean incrementStatus(int []status, List<LGraphNode> []options, LGraph g1, LGraph g2) {
        do {
            if (!incrementStatusInternal(status, options)) return false;
            if (!statusSatisfiesObjectIdentity(status, options)) continue;
            Map<LGraphNode, LGraphNode> map = mappingFromStatus(status, options, g1);
            if (g1.subsumesWithMapping(g2, map)) return true;
        }while(true);
    }

    
    public static boolean incrementStatusInternal(int []status, List<LGraphNode> []options) {
        int current = 0;
        int m = status.length;
        do{
            status[current]++;
            if (status[current]<options[current].size()) break;
            status[current] = 0;
            current++;
        }while(current<m);
        if (current==m) return false;
//        System.out.println(Arrays.toString(status));
        return true;
    }
    
    
    
    public static Map<LGraphNode, LGraphNode> mappingFromStatus(int []status, List<LGraphNode> []options, LGraph g1) {
        Map<LGraphNode, LGraphNode> map = new LinkedHashMap<LGraphNode, LGraphNode>();
        for(int i = 0;i<status.length;i++) {
            map.put(g1.getNodes().get(i), options[i].get(status[i]));
        }
        return map;
    }    
}
