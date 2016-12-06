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
    
    public Ontology(String fileName) throws Exception
    {
        loadOntology(fileName);
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
