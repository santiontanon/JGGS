/********************************************************************************
Organization		: Drexel University
Authors			: Santiago Ontanon
Class			: Sampler
Function		: This class contains methods to sample
                          from a given distribution. Including support
                          for exploration vs exploitation.
 *********************************************************************************/
package util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Sampler {
    public Random r = null;
    
    public Sampler() {
        r = new Random();
    }
    
    
    public Sampler(long randomSeed)
    {
        r = new Random(randomSeed);
    }

    
    public Sampler(Random a_r)
    {
        r = a_r;
    }

    
    /*
     * Returns a random element in the distribution
     */
    public int random(double []distribution) {
        return r.nextInt(distribution.length);
    }

    /*
     * Returns the element with maximum probability (ties are resolved randomly)
     */
    public int max(double []distribution) throws Exception {
        List<Integer> best = new LinkedList<Integer>();
        double max = distribution[0];

        for (int i = 0; i < distribution.length; i++) {
            Double f = distribution[i];
            if (f == max) {
                best.add(new Integer(i));
            } else {
                if (f > max) {
                    best.clear();
                    best.add(new Integer(i));
                    max = f;
                }
            }
        }

        if (best.size() > 0) {
            return best.get(r.nextInt(best.size()));
        }

        throw new Exception("Input distribution empty in Sampler.max!");
    }

    /*
     * Returns the score with maximum probability (ties are resolved randomly)
     */
    public static Double maxScore(double []distribution) {
        List<Integer> best = new LinkedList<Integer>();
        double max = distribution[0];

        for (int i = 0; i < distribution.length; i++) {
            Double f = distribution[i];
            if (f == max) {
                best.add(new Integer(i));
            } else {
                if (f > max) {
                    best.clear();
                    best.add(new Integer(i));
                    max = f;
                }
            }
        }

        return max;

    }

    /*
     * Returns an element in the distribution, using the weights as their relative probabilities
     */
    public int weighted(double []distribution) throws Exception {
        double total = 0, accum = 0, tmp;

        for (double f : distribution) {
            total += f;
        }

        double rnd = r.nextDouble();
        tmp = total*rnd;
        for (int i = 0; i < distribution.length; i++) {
            accum += distribution[i];
            if (accum >= tmp) {
                return i;
            }
        }

        throw new Exception("Input distribution empty in Sampler.weighted (array)!");
    }
    
    
    /*
     * Returns an element in the distribution, using the weights as their relative probabilities
     */
    public int weighted(List<Double> distribution) throws Exception {
        double total = 0, accum = 0, tmp;

        for (double f : distribution) {
            total += f;
        }

        tmp = r.nextDouble() * total;
        int i = 0;
        for (double f : distribution) {
            accum += f;
            if (accum >= tmp) {
                return i;
            }
            i++;
        }

        throw new Exception("Input distribution empty in Sampler.weighted (list)!");
    }
    

    /*
     * Returns an element in the distribution following the probabilities, but using 'e' as the exploration factor.
     * For instance:
     * If "e" = 1.0, then it has the same effect as the "max" method
     * If "e" = 0.5, then it has the same effect as the "weighted" method
     * If "e" = 0, then it has the same effect as the "random" method
     */
    public int explorationWeighted(double []distribution, double e) throws Exception {
        /*
         * exponent = 1/(1-e)-1
         */

        double exponent = 0;
        double quotient = 1 - e;
        if (quotient != 0) {
            exponent = 1 / quotient - 1;
        } else {
            exponent = 1000;
        }
        double []exponentiated = new double[distribution.length];

        for(int i = 0;i<distribution.length;i++)
            exponentiated[i] = Math.pow(distribution[i],exponent);

        return weighted(exponentiated);
    }
    
    /*
    Returns a random distribution with n groups
     */
    public List<Double> createDistribution(int groups) {
        return createDistribution(1.0, groups);
    }

    /*
    Returns a random distribution with n groups that adds to a size
     */
    public List<Double> createDistribution(double population_size, int groups) {
        List<Double> lst = new LinkedList();
        double total = 0.0;
        for (int i = 0; i < groups; i++) {
            double d = r.nextDouble();
            lst.add(d);
            total += d;
        }
        for (int i = 0; i < groups; i++) {
            lst.set(i, lst.get(i) / total * population_size);
        }
        return lst;
    }

    public List<Integer> createDistribution(int population_size, int groups) {
        List<Integer> lst = new LinkedList();
        int total = 0;
        for (double d : createDistribution(groups)) {
            int i = new Double(population_size * d).intValue();
            total += i;
            lst.add(i);
        }
        // Fix rounding errors
        while (total < population_size) {
            int i = r.nextInt(groups);
            lst.set(i, lst.get(i) + 1);
            total += 1;
        }
        while (total > population_size) {
            int i = r.nextInt(groups);
            if (lst.get(i) > 0) {
                lst.set(i, lst.get(i) - 1);
                total -= 1;
            }
        }
        return lst;
    }
}
