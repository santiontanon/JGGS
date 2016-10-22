/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lgraphs.ontology;

import java.util.StringTokenizer;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class Ontology {
    
    public static Sort in, has, partof, typelabel, isLabel, ownerLabel, ownedBy;
    public static Sort arrive, kill, investigateKill, rescue, obtain, dispatcherLabel,kidnapper;
    public static Sort friendlyLabel, unfriendlyLabel;
    public static Sort player, goal;
    public static Sort locationLabel,areaLabel;
    public static Sort pathLabel, onewaypath, singleentrancepath, interareapath;
    public static Sort lockLabel, opens, tradeFor, tradeWith, message;
    public static Sort npcLabel, friendlyNpcLabel, enemyNpcLabel;
    public static Sort characterTypeLabel;
    public static Sort itemLabel, keyLabel, potionLabel, hppotionLabel, mppotionLabel;
    public static Sort weaponLabel, offhandLabel, ringLabel, scrollLabel, containerLabel;
    public static Sort smallLabel, mediumLabel, largeLabel, hugeLabel;

    public Ontology(String fileName) throws Exception
    {
        loadOntology(fileName);
        
        in = Sort.getSort("in");
        has = Sort.getSort("has");
        partof = Sort.getSort("partof");
        typelabel = Sort.getSort("type");
        isLabel = Sort.getSort("is");
        ownerLabel = Sort.getSort("cp-owner");
        ownedBy = Sort.getSort("owned-by");
        arrive = Sort.getSort("arrive");
        kill = Sort.getSort("kill");
        investigateKill = Sort.getSort("investigate-and-kill");
        rescue = Sort.getSort("rescue");
        kidnapper = Sort.getSort("kidnapper");
        obtain = Sort.getSort("obtain");
        dispatcherLabel = Sort.getSort("dispatcher");
        friendlyLabel = Sort.getSort("friendly");
        unfriendlyLabel = Sort.getSort("unfriendly");
        player = Sort.getSort("player");
        goal = Sort.getSort("goal");
        locationLabel = Sort.getSort("location");
        areaLabel = Sort.getSort("area");
        pathLabel = Sort.getSort("path");
        interareapath = Sort.getSort("interareapath");
        onewaypath = Sort.getSort("onewaypath");
        singleentrancepath = Sort.getSort("singleentrancepath");
        lockLabel = Sort.getSort("lock");
        opens = Sort.getSort("opens");
        tradeFor = Sort.getSort("tradeFor");
        tradeWith = Sort.getSort("tradeWith");
        message = Sort.getSort("message");
        npcLabel = Sort.getSort("npc");
        friendlyNpcLabel = Sort.getSort("friendly-npc");
        enemyNpcLabel = Sort.getSort("enemy-npc");
        itemLabel = Sort.getSort("item");
        keyLabel = Sort.getSort("key");
        potionLabel = Sort.getSort("potion");
        hppotionLabel = Sort.getSort("hppotion");
        mppotionLabel = Sort.getSort("mppotion");
        weaponLabel = Sort.getSort("weapon");
        offhandLabel = Sort.getSort("offhand");
        ringLabel = Sort.getSort("ring");
        scrollLabel = Sort.getSort("scroll");
        containerLabel = Sort.getSort("container");
        characterTypeLabel = Sort.getSort("character-type");
        smallLabel = Sort.getSort("small");
        mediumLabel = Sort.getSort("medium");
        largeLabel = Sort.getSort("large"); 
        hugeLabel = Sort.getSort("huge"); 
    }
    
    public void loadOntology(String fileName) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Element root = builder.build(fileName).getRootElement();

        for(Object o:root.getChildren("sort")) {
            Element e = (Element)o;
            String parents = e.getAttributeValue("super");
            StringTokenizer st = new StringTokenizer(parents,",");
            Sort s = Sort.newSort(e.getAttributeValue("name"), st.nextToken());
            while(st.hasMoreTokens()) {
                s.addParent(Sort.getSort(st.nextToken()));
            }
        }
    }
}
