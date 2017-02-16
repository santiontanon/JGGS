/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lgraphs;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author santi
 * 
 */
public class LGraphMatcher {
    LGraph reference;
    LGraph pattern;
    boolean objectIdentity = false;
    
    int n,m;
    int []status;
    List<LGraphNode> []options;
    
    boolean moreSolutions = true;
    
    public LGraphMatcher(LGraph a_pattern, LGraph a_reference, boolean a_objectIdentity) {
        reference = a_reference;
        pattern = a_pattern;
        objectIdentity = a_objectIdentity;
        
        n = a_reference.getNodes().size();
        m = a_pattern.getNodes().size();
        
        options = new List[m];
        status = null;
        for(int i = 0;i<m;i++) {
            LGraphNode pn = pattern.getNodes().get(i);
            options[i] = new LinkedList<LGraphNode>();
            for(LGraphNode rn:reference.getNodes()) {
                if (pn.subsumes(rn)) options[i].add(rn);
            }
            if (options[i].isEmpty()) {
                moreSolutions = false;
                return;
            }
        }
    }
    
    public LGraphMatcher(LGraph a_pattern, LGraph a_reference, boolean a_objectIdentity, Map<LGraphNode, LGraphNode> map) {
        reference = a_reference;
        pattern = a_pattern;
        objectIdentity = a_objectIdentity;
        
        n = a_reference.getNodes().size();
        m = a_pattern.getNodes().size();
        
        options = new List[m];
        status = null;
        for(int i = 0;i<m;i++) {
            LGraphNode pn = pattern.getNodes().get(i);
            LGraphNode r_tmp = map.get(pn);
            options[i] = new LinkedList<LGraphNode>();
            if (r_tmp!=null) {
                // if it's int he "map", there is only one option for this node:
                options[i].add(r_tmp);
            } else {
                for(LGraphNode rn:reference.getNodes()) {
                    if (pn.subsumes(rn)) options[i].add(rn);
                }
            }
//            System.out.println("    " + options[i].size());
            if (options[i].isEmpty()) {
                moreSolutions = false;
                return;
            }
        }

    }    
    
    public Map<LGraphNode,LGraphNode> nextMatch() {
        if (!moreSolutions) return null;
        if (status == null) {
            status = new int[m];
            for(int i = 0;i<m;i++) {
                status[i] = 0;
                if (options[i].isEmpty()) {
                    moreSolutions = false;
                    return null;
                }
            }
            if (objectIdentity) {
                if (!statusSatisfiesObjectIdentity()) {
                    if (!incrementStatus()) {
                        moreSolutions = false;
                        return null;
                    }
                }
            }

        } else {
            if (!incrementStatus()) {
                moreSolutions = false;
                return null;
            }
        }
//        System.out.println("      " + Arrays.toString(status));
        Map<LGraphNode, LGraphNode> map = mappingFromStatus();
        while(!pattern.subsumesWithMapping(reference, map)) {
            if (!incrementStatus()) {
                moreSolutions = false;
                return null;
            }
    //        System.out.println("      " + Arrays.toString(status));
            map = mappingFromStatus();
        }
        
        return map;
    }
    
    
    public boolean statusSatisfiesObjectIdentity()
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
    
    
    public boolean incrementStatus() {
        if (objectIdentity) {
            do {
                if (!incrementStatusInternal()) return false;
                if (statusSatisfiesObjectIdentity()) return true;
            }while(true);
        } else {
            return incrementStatusInternal();
        }
    }

    
    public boolean incrementStatusInternal() {
        int current = 0;
        do{
            status[current]++;
            if (status[current]<options[current].size()) break;
            status[current] = 0;
            current++;
        }while(current<m);
        if (current==m) return false;
        return true;
    }


    
    public Map<LGraphNode, LGraphNode> mappingFromStatus() {
        Map<LGraphNode, LGraphNode> map = new LinkedHashMap<LGraphNode, LGraphNode>();
        for(int i = 0;i<m;i++) {
            map.put(pattern.getNodes().get(i), options[i].get(status[i]));
        }
        return map;
    }
    
}
