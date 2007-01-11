/*
 * Created on Mar 3, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gphase.tools;

import gphase.model.ASVariation;
import gphase.model.SuperExon;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Vector;


/**
 * 
 * 
 * @author msammeth
 */
public class Arrays {

	public static String reverseComplement(String in) {
		return reverse(complement(in));
	}
	public static Vector addUnique(Collection c, Object[] a, Comparator compi) {
		Object[] ca= c.toArray();
		for (int i = 0; a!= null&& i < a.length; i++) {
			int p= java.util.Arrays.binarySearch(ca, a[i], compi);
			if (p< 0)
				ca= insert(ca, a[i], p);
		}
		Vector v= new Vector(ca.length);
		addAll(v, ca);
		return v;
	}
	/**
	 * assumes not sorted arrays
	 * @param c
	 * @param a
	 * @return
	 */
	public static Collection addUnique(Collection c, Object[] a) {
		Object[] ca= c.toArray();
		for (int i = 0; a!= null&& i < a.length; i++) {
			int j;
			for (j = 0; j < ca.length; j++) 
				if (a[i]== ca[j])
					break;
			if (j== ca.length)
				c.add(a[i]);
		}
		return c;
	}
	
	public static String complement(String in){
		String normal= "ACGTN-acgt";
		String reverse= "TGCAN-tgca";
		StringBuffer buffy= new StringBuffer(in.length());
		for (int i = 0; i < in.length(); i++) 
			buffy.append(reverse.charAt(normal.indexOf(in.charAt(i))));
		return buffy.toString();
	}
	public static String reverse(String in) {
		StringBuffer buffy= new StringBuffer(in.length());
		for (int i = 0; i < in.length(); i++) 
			buffy.append(in.charAt(in.length()- 1- i));
		return buffy.toString();
	}
	
	public static int[] reverse (int[] a) {
		if (a== null)
			return null;
		int[] b= new int[a.length];
		for (int i = 0; i < a.length; i++) {
			b[b.length- i- 1]= a[i];
		}
		return b;
	}
	public static String toString(int[] a) {
		String result= "";
		if (a== null|| a.length< 1)
			return result;
		for (int i = 0; i < a.length; i++) 
			result+= a[i]+",";
		return result.substring(0, result.length()- 1);
	}
	
	public static String toString(Object[][] o) {
		String s= "{";
		for (int i = 0; i < o.length; i++) {
			s+= "[";
			for (int j = 0; j < o[i].length; j++) 
				s+= o[i][j]+ ",";
			s= s.substring(0, s.length()- 1)+ "];";			
		}
		s= s.substring(0, s.length()- 1)+ "}";	
		return s;
	}
	
	public static void output(double[][] a, PrintStream p) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				p.print(((float) a[i][j])+ "\t");
			}
			p.println();
		}
	}
	
	public static class FieldSizeRevComparator extends FieldSizeComparator{
		public int compare(Object arg0, Object arg1) {
			return -super.compare(arg0, arg1);
		}
	}
	public static class FieldSizeComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			
			int size1= -1, size2= -1;
			
			if (arg0 instanceof Vector) 
				size1= ((Vector) arg0).size();
			else if (arg0 instanceof Object[])
				size1= ((Object[]) arg0).length;

			if (arg1 instanceof Vector) 
				size2= ((Vector) arg1).size();
			else if (arg1 instanceof Object[])
				size2= ((Object[]) arg1).length;
			
			if (size1< size2)
				return (-1);
			if (size2< size1)
				return 1;			
			return 0;
		}
	}
	

	
	public static void main(String[] args) {
		Integer[] a= new Integer[] {new Integer(2),new Integer(3),new Integer(5)};
		a= (Integer[]) insert(a, new Integer(4), 2);
		for (int i = 0; i < a.length; i++) 
			System.out.println(a[i].intValue());
	}

	public static Object[] sort2DField(Object o) {
		
		Object[] oo;
		if (o instanceof Collection)
			oo= (Object[]) ((Collection) o).toArray();
		else
			oo= (Object[]) o;
		
		java.util.Arrays.sort(oo, new Arrays.FieldSizeComparator());
		return oo;
	}
	
	
	public static Object[] sort2DFieldRev(Object o) {
		
		if (o== null)
			return null;
		
		Object[] oo;
		if (o instanceof Collection)
			oo= (Object[]) ((Collection) o).toArray();
		else  
			oo= (Object[]) o;
		
		java.util.Arrays.sort(oo, new Arrays.FieldSizeRevComparator());
		return oo;
	}	
	
	
	public static Object[] add(Object[] a, Object o) {
		if (a== null)
			return insert(a, o, 0);
		else 
			return insert(a, o, a.length);
	}
	
	public static Collection addAll(Collection c, Object[] a) {
		for (int i = 0; a!= null&& i < a.length; i++) 
			c.add(a[i]);
		return c;
	}
	
	public static Object[] addAll(Object[] a, Object[] b) {
		if (b== null)
			return a;
		if (a== null)
			return b;
		Vector v= new Vector(a.length+ b.length);
		v= (Vector) addAll(v, a);
		v= (Vector) addAll(v, b);
		return (Object[]) toField(v);
	}
	
	public static Object[] addUnique(Object[] a, Object o) {
		for (int i = 0; a!= null&& i < a.length; i++) {
			if (a[i]== o)
				return a;
		}
		return add(a, o);
	}
	
	public static Object[] extendField(Object[] a, Object o) {
		if (a== null) {
			Object[] newA= (Object[]) Array.newInstance(o.getClass(), 1);
			newA[0]= o;
			return newA;
		}
		
		return insert(a, o, a.length);	
	}
	
	/**
	 * Assuming that a and o share the same class and some other things, this
	 * inserts an object in an array at the specified position. Automatically 
	 * converts (negative) insertion points as proposed by for instance 
	 * <code>Arrays.binarySearch</code>.
	 * 
	 * @param a
	 * @param o
	 * @param p
	 * @return
	 */
	public static Object[] insert(Object[] a, Object o, int p) {
		
		if (a== null&& p== 0) {
			Object[] newA= (Object[]) Array.newInstance(o.getClass(), 1);
			newA[0]= o;
			return newA;
		}
			
		if (p< 0)
			p= (p+1)* (-1);
		
		Object[] newA= (Object[]) Array.newInstance(o.getClass(), a.length+ 1);
		for (int i = 0; i < p; i++) 
			newA[i]= a[i];
		newA[p]= o;
		for (int i = p+1; i < newA.length; i++) 
			newA[i]= a[i-1];
		
		return newA;
	}
	
	public static Object[] remove(Object[] a, Object o) {
		if (a== null)
			return null;
		
		Vector v= new Vector();
		for (int i = 0; i < a.length; i++) 
			if (a[i]!= o)
				v.add(a[i]);
		
		return (Object[]) toField(v);
	}
	
	public static void swap(Object[] o) {
		if (o== null|| o.length!= 2)
			return;

		Object oh= o[0];
		o[0]= o[1];
		o[1]= oh;
	}
	
	/**
	 * Converts an eventually highdimensional Array or Vector to an Array.
	 * @param base
	 * @return
	 */
	public static Object toField(Object base) {

		if (!(base instanceof Collection)&& !(base instanceof Object[])) {
			return base;
		}
		
		Object[] o= null;
		if (base instanceof Collection) 
			o= ((Collection) base).toArray();
		else 						// (base instanceof Object[]) 
			o= ((Object[]) base);

		if (o.length< 1)	// empty array
			return null; //cannot guess the basic class for vectors anyway
		
		Object r= null;	// find reference
		//int x= 0;
		//while (r== null&& x< o.length) {
		for (int x = 0; x < o.length; x++) {
			Object ro= toField(o[x]);
			if (ro== null) 
				continue;
			if (r== null|| ro.getClass().isAssignableFrom(r.getClass()))
				r= ro;
		}
		Object[] result= null;
		if (r!= null) {
			result= (Object[]) Array.newInstance(r.getClass(), o.length);
			for (int i = 0; i < o.length; i++) 
				result[i]= toField(o[i]);
		} 

		return result;
	}
		
	public static int[] toPrimitive(Integer[] a) {
		if (a== null)
			return null;
		int[] i= new int[a.length];
		for (int j = 0; j < i.length; j++) 
			i[j]= a[j].intValue();
		return i;
	}
	
	/**
	 * Converts an eventually highdimensional Array or Vector to an Array.
	 * @param base
	 * @return
	 */
	public static Object toPrimitiveField(Object base, Class type) {

		if (!(base instanceof Collection)&& !(base instanceof Object[])) {
			;
		}
	
		Object[] o= null;
		if (base instanceof Collection) 
			o= ((Collection) base).toArray();
		else 						// (base instanceof Object[]) 
			o= ((Object[]) base);

		if (o.length< 1)	// empty array
			return null; //cannot guess the basic class for vectors anyway
		
		Object r= null;	// find reference
		int x= 0;
		while (r== null&& x< o.length) {
			r= toField(o[x++]);
		}
		Object[] result= null;
		if (r!= null) {
			result= (Object[]) Array.newInstance(type, );
			for (int i = 0; i < o.length; i++) 
			result[i]= toPrimitiveField(o.length);
		}
		
		return result;
	}
	
	public static Collection merge(Object o1, Object o2) {
		Object[] oa1= null;
		if (o1 instanceof Collection)
			oa1= ((Collection) o1).toArray();
		else
			oa1= (Object[]) o1;
		
		Object[] oa2= null;
		if (o2 instanceof Collection)
			oa2= ((Collection) o2).toArray();
		else
			oa2= (Object[]) o2;
		
		//Object r= toField(oa1[0]);
		Object[] result= (Object[]) Array.newInstance(oa1[0].getClass(), oa1.length+ oa2.length);
		Vector resVec= new Vector(result.length);
		for (int i = 0; i < oa1.length; i++) 
			resVec.add(oa1[i]);
		for (int i = 0; i < oa2.length; i++) 
			resVec.add(oa2[i]);
		
		return resVec;
	}
	public static Object[] filter(Object[] hits, Method target) {
		Vector v= new Vector(hits.length);
		for (int j = 0; j < hits.length; j++) {
			Object o= null;
			try {
				o= target.invoke(hits[j], null);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (((Boolean) o).booleanValue())
				v.add(hits[j]);
		}
		return (Object[]) Arrays.toField(v);
	}
	public static Object[] filter(Object[] hits, String methodName) {
		
		if (hits== null|| hits.length< 0)
			return hits;
		
		Method m= null;
		try {
			m = hits[0].getClass().getMethod(methodName, null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} 
		return filter(hits, m);
	}
	public static Object[][] filter(Object[][] hits, Method target) {
		if (hits== null)
			return null;
		
		Vector result= new Vector(hits.length);
		for (int i = 0; i < hits.length; i++) {
			Object[] filtered= filter(hits[i], target);
			if (filtered!= null&& filtered.length> 0)
				result.add(filtered);
		}
		
		return (Object[][]) Arrays.toField(result);
	}
	public static Object[][] filter(Object[][] hits, String methodName) {
		
		if (hits== null|| hits.length< 0|| hits[0]== null|| hits[0].length< 0)
			return hits;
		
		Method m= null;
		try {
			m = hits[0][0].getClass().getMethod(methodName, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return filter(hits, m);
	}
}