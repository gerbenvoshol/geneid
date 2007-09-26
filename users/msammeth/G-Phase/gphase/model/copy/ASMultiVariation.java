/*
 * Created on Mar 2, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gphase.model.copy;

import gphase.graph.Tuple;
import gphase.tools.Arrays;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

/**
 * 
 * 
 * @author msammeth
 */
public class ASMultiVariation implements Serializable {

	public static class PositionComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			ASMultiVariation var0= (ASMultiVariation) arg0;
			ASMultiVariation var1= (ASMultiVariation) arg1;
			
			SpliceSite[][] sc0= var0.getSpliceChains();	// are sorted! optimize			
			SpliceSite[][] sc1= var1.getSpliceChains();
			
			if (sc0.length!= sc1.length)
				return -1;
			
			int i;
			for (i = 0; i < sc0.length; i++) {
				int j;
				for (j = 0; j < sc1.length; j++) {
					if (sc0[i].length!= sc1[j].length)
						continue;
					int k;
					for (k = 0; k < sc0[i].length; k++) {
						if (sc0[i][k]!= sc1[j][k])
							break;
					}
					if (k== sc0[i].length)
						break;
				}
				if (j== sc1.length)
					break;
			}
			if (i< sc0.length)
				return -1;
			return 0;	// cross-check not necessary if both equally large
		}
	}
	public static class SpliceChainComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			SpliceSite[] s0= (SpliceSite[]) arg0;
			SpliceSite[] s1= (SpliceSite[]) arg1;
			
				// exclude null cases
			if (s0== null) {
				if (s1 == null)
					return 0;
				else
					return -1;
			} else if (s1== null)
				return 1;
			
				// compare chains
			for (int i = 0; i < s0.length&& i< s1.length; i++) {
				if (s0[i].getPos()< s1[i].getPos())
					return -1;
				if (s0[i].getPos()> s1[i].getPos())
					return 1;
			}
				
				// longer chain first
			if (s0.length> s1.length)
				return -1;
			if (s1.length> s0.length)
				return 1;
			return 0;
		}
	}
	public static ASVariation[][] clusterIdenticalEvents(ASVariation[] inVars) {
		if (inVars== null)
			return null;
		
		Comparator compi= new ASVariation.IdentityComparator();
		Vector jumpV= new Vector();
		Vector clusterV= new Vector();
		for (int i = 0; i < inVars.length; i++) {
			int x;
			for (x = 0; x < jumpV.size(); x++) 
				if (jumpV.elementAt(x)== inVars[i])
					break;
			if (x< jumpV.size())
				continue;
			
			Vector thisClusterV= new Vector();
			thisClusterV.add(inVars[i]);
			for (int j = i+1; j < inVars.length; j++) {
				if (compi.compare(inVars[i], inVars[j])== 0) {
					thisClusterV.add(inVars[j]);
					jumpV.add(inVars[j]);
				}
			}
			clusterV.add(thisClusterV);
		}
		
		return (ASVariation[][]) Arrays.toField(clusterV);
	}
	

	public static final int FILTER_NONE= 0;
	public static final int FILTER_HIERARCHICALLY= 1;
	public static final int FILTER_CODING_REDUNDANT= 2;
	public static final int FILTER_STRUCTURALLY= 3;
	public static final int FILTER_CONTAINED_IN_CDS= 4;
	public static final int FILTER_IDENTICALLY= 5;
	public static String[] FILTER_TO_STRING= {"none", "hierarchically", "coding_redundant", "structurally"};
	
	ASVariation[] asVariations= null;
	SpliceSite[][] spliceChains= null;
	String relPosStr= null;
	HashMap transHash= null;	// maps schains (ss[]) to transcripts
	AbstractSite[] flanks= null;
	
	public SpliceSite[] getSpliceUniverse() {
		if (spliceChains== null|| spliceChains.length< 1)
			return null;
		
		Vector ssV= new Vector();
		for (int i = 0; i < spliceChains.length; i++) 
			for (int j = 0; j < spliceChains[i].length; j++) 
				ssV.add(spliceChains[i][j]);

		SpliceSite[] ss= (SpliceSite[]) Arrays.toField(ssV);
		java.util.Arrays.sort(ss, new SpliceSite.PositionComparator());
		return ss;
	}
	
	public ASMultiVariation(ASVariation[] newASVariations) {
		this.asVariations= newASVariations;
	}
	
	public ASMultiVariation(SpliceSite[][] schains, Transcript[] trans) {
		this.spliceChains= schains;
	}
	
	public ASMultiVariation(SpliceSite[][] schains, HashMap newTransHash) {
		setSpliceChains(schains);
		this.transHash= newTransHash;
	}
	
	public ASMultiVariation(HashMap newTransHash) {
		this.transHash= newTransHash;
		Object[] o= newTransHash.keySet().toArray();
		SpliceSite[][] schains= new SpliceSite[o.length][];
		for (int i = 0; i < schains.length; i++) 
			schains[i]= (SpliceSite[]) o[i];
		setSpliceChains(schains);
	}
	
	public Tuple[] getSeparationTuples() {
		Transcript[][] part= (Transcript[][]) Arrays.toField(transHash.values());
		Vector v= new Vector();
		for (int i = 0; i < part.length; i++) 
			for (int j = i+1; j < part.length; j++) 
				for (int k = 0; k < part[i].length; k++) 
					for (int m = 0; m < part[j].length; m++) {
						Tuple t= new Tuple(part[i][k].getTranscriptID(), part[j][m].getTranscriptID());
						v.add(t);
					}
		return (Tuple[]) Arrays.toField(v);
	}
	
	void setSpliceChains(SpliceSite[][] schains) {
		Comparator compi= new SpliceChainComparator();
		java.util.Arrays.sort(schains, compi);
		spliceChains= schains;
	}
	
	public static ASVariation[] removeRedundancyHierachically(ASVariation[] vars, Comparator compi) {
		
		Vector v= new Vector(vars.length);
		for (int i = 0; i < vars.length; i++) 
			v.add(vars[i]);
		
		
		for (int i = 0; i < v.size(); i++) 
			for (int j = i+1; j < v.size(); j++) {
				int c= compi.compare(v.elementAt(i), v.elementAt(j));
				if (c== 0|| c== 1) {
					((ASVariation) v.elementAt(j)).removeFromASS();
					v.remove(j--);
				} 
				if (c== 2) {
					((ASVariation) v.elementAt(i)).removeFromASS();
					v.remove(i--);
					break;
				}
				// case of (-1), keep both (different in structure!)
			}
		
		return (ASVariation[]) Arrays.toField(v);
	}

	public static ASVariation[] filterNonGTAG(ASVariation[] vars) {
		Vector v= new Vector();
		for (int i = 0; i < vars.length; i++) 
			if (!vars[i].hasNonGTAGintron())
				v.add(vars[i]);
		return (ASVariation[]) Arrays.toField(v);
	}
	public static ASVariation[] removeRedundancy(ASVariation[] vars, Comparator compi) {
		if (vars== null)
			return null;
		Vector v= new Vector(vars.length);
		for (int i = 0; i < vars.length; i++) 
			v.add(vars[i]);
		
		
		for (int i = 0; i < v.size(); i++) 
			for (int j = i+1; j < v.size(); j++) {
				if (compi.compare(v.elementAt(i), v.elementAt(j))== 0) {
					((ASVariation) v.elementAt(j)).removeFromSpliceSites();
					v.remove(j--);					
				}
			}
		
		return (ASVariation[]) Arrays.toField(v);
	}
	
	public ASVariation[] getASVariationsAll() {
		return asVariations;
	}
	
		public ASVariation[] getASVariationsHierarchicallyFiltered() {
			Comparator compi= null;
			//compi= new ASVariation.CodingHierarchyFilter();
			compi= new ASVariation.HierarchyFilter();

		return removeRedundancyHierachically(asVariations, compi);
	}
		
		public ASVariation[] getASVariationsStructurallyFiltered() {
			return removeRedundancy(asVariations, new ASVariation.IdentityComparator());
		}
		
	public ASVariation[] getASVariations(Transcript transcriptID_1, Transcript transcriptID_2) {
		if (asVariations== null)
			return null;
		
		Vector v= new Vector();
		for (int i = 0; i < asVariations.length; i++) 
			if (asVariations[i].containsTranscripts(transcriptID_1, transcriptID_2))
				v.add(asVariations[i]);
		
		if (v.size()< 1)
			return null;
		ASVariation[] result= new ASVariation[v.size()];
		for (int i = 0; i < result.length; i++) 
			result[i]= (ASVariation) v.elementAt(i);
		
		return result;
	}

	public ASVariation[] getASVariationsClusteredCoding() {
		return removeRedundancy(asVariations, new ASVariation.CodingComparator());
	}


	public String toDegreeString() {
		String result= getDegree()+ ": ( ";
		for (int i = 0; i < asVariations.length; i++) {
			result+= asVariations[i]+"; ";
		}
		result= result.substring(0,result.length()- 2)+ " )";
		return result;
	}
	
	public String toString() {
		
		if (relPosStr == null) {
			if (spliceChains== null|| spliceChains.length< 1)
				return "error";
			
			String[] c= new String[spliceChains.length];	// better: StringBuffer
			for (int i = 0; i < c.length; i++) 
				c[i]= "";
			
			int[] pos= new int[spliceChains.length];	// pos in the sChain
			Vector[] relPosV= new Vector[spliceChains.length];
			for (int i = 0; i < pos.length; i++) {
				pos[i]= 0;
				relPosV[i]= new Vector();
			}
			int currPos= 0;
			
			boolean finished= false;
			while(!finished) {
				int min= Integer.MAX_VALUE;
				Vector minSCV= new Vector();
				for (int i = 0; i < spliceChains.length; i++) {	// find next min
					if (spliceChains[i]== null|| spliceChains[i].length< 1|| pos[i]>= spliceChains[i].length)
						continue;
					if (spliceChains[i][pos[i]].getPos()< min) {
						min= spliceChains[i][pos[i]].getPos();	// work with +/- values
						minSCV= new Vector();
						minSCV.add(new Integer(i));
					} else if (spliceChains[i][pos[i]].getPos()== min) {
						minSCV.add(new Integer(i));
					}
				}
				
				++currPos;	// add next rel Pos
				for (int i = 0; i < minSCV.size(); i++) {
					int minSC= ((Integer) minSCV.elementAt(i)).intValue();
					c[minSC]+= currPos+ (spliceChains[minSC][pos[minSC]].isDonor()?"^":"-");
					++pos[minSC];
					relPosV[minSC].add(new Integer(currPos));
				}
				
				int x;
				for (x = 0; x < pos.length; x++) 
					if (spliceChains[x]!= null&& pos[x]< spliceChains[x].length)
						break;
				if (x>= pos.length)
					finished= true;
			}
			
			String result= "";
			for (int i = 0; i < c.length; i++) 
				result+= c[i]+ " , ";
			result= result.substring(0, result.length()- 3);
			
			relPosStr= result;
		}

		return relPosStr;
	}

	public int getDegree() {
		int deg= 0;
		for (int i = 0; i < asVariations.length; i++) 
			deg+= asVariations[i].getDegree();
		
		return deg;
	}
	
	public Gene getGene() {
		if (transHash== null)
			return null;
		return ((Transcript[]) transHash.values().toArray()[0])[0].getGene();
	}

	public SpliceSite[][] getSpliceChains() {
		return spliceChains;
	}

	public HashMap getTransHash() {
		return transHash;
	}
	
	public Transcript[][] getTranscriptsFromHash() {
		return (Transcript[][]) Arrays.toField(transHash.values());
	}
	
	public SpliceSite[][] getSpliceChainsFromHash() {
		return (SpliceSite[][]) Arrays.toField(transHash.keySet());
	}
	
	public SpliceSite getMinSpliceSite() {
		SpliceSite[][] schains= getSpliceChainsFromHash();
		int min= Integer.MAX_VALUE;
		SpliceSite result= null;
		for (int i = 0; i < schains.length; i++) {
			if (schains[i]== null)
				continue;
			if (schains[i][0].getPos()< min) {
				min= schains[i][0].getPos();
				result= schains[i][0];
			}
		}
		return result;
	}

	public AbstractSite[] getFlanks() {
		return flanks;
	}
	
	public DirectedRegion getRegion() {
		DirectedRegion reg= new DirectedRegion(flanks[0].getPos(), flanks[1].getPos(), getGene().getStrand());
		return reg;
	}

	public void setFlanks(AbstractSite[] flanks) {
		this.flanks = flanks;
	}
}
