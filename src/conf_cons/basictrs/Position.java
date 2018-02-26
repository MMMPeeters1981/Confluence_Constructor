package conf_cons.basictrs;

import java.util.*;

import conf_cons.gui.*;
import conf_cons.conf.*;

/**
 * This class contains information about and methods using or altering positions.
 * A position contains an ArrayList of integers. It can be used to
 * point at a specific subterm of a term.
 * The term itself is position [0].
 * After that the first subterm is [0,0].
 * The length of the array can be seen as the 'depth' of the term.
 * The integer at a specific index of the array tells you which subterm to take at a certain depth.
 * for instance:
 * at position [0,1] in the term *(*(a,a),b) the subterm b is found.
 * 
 * @author Marieke Peeters
 *
 */
public class Position {
	// a position contains an array of integers
	public ArrayList <Integer> position = new ArrayList <Integer>();
	
	/**
	 * Constructor: create an empty position
	 */
	public Position () {
	}
	
	/**
	 * Constructor: create position containing a specific ArrayList of integers
	 * @param pos This is the position you want to save in the object.
	 */
	public Position (ArrayList <Integer> pos) {
		position = pos;
	}
	
	/**
	 * This method checks if two objects are equal.
	 * @param obj This is the object that is checked for equality with this position.
	 * @return The method returns false if the objects are not equal and true if they are equal.
	 */
	public boolean equals (Object obj) {
		boolean equal = true;
		// if the objects are the same object, they are equal
		if (this == obj)
			return equal;
		// if the object is empty or the objects are from different classes, they are not equal
		if ((obj == null) || (obj.getClass() != this.getClass()))
			equal=false;
		// if the position is empty, there is something wrong
		else if (position==null)
			equal = false;
		// check if the arrays of integers are the same, if not, the objects are not equal
		else {
			Position pos = (Position) obj;
			if (position.size()==pos.position.size()) {
				for (int i=0; i<position.size(); i++){
					if (position.get(i) == null) {
						equal=false;
						break;
					}
					else if (!(position.get(i)==pos.position.get(i))) {
						equal=false;
						break;
					}
				}
			} else
				equal=false;
		}
		return equal;
	}

	/**
	 * This is a method that returns the hashCode of an object
	 * by computing a number from its component hashCodes.
	 */
	public int hashCode() {
		int hash = 7;
		for (int nr: position) {
			hash = 31 * hash + nr;
		}
		return hash;
	}
	
	/**
	 * This method prints a position.
	 */
	public void printPos() {
		System.out.print("[");
		for (int i=0;i<position.size();i++){
			if(i<position.size()-1)
				System.out.print( position.get(i) + ", ");
			else
				System.out.print(position.get(i));
		}
		System.out.print("].");
	}

	/**
	 * This method is used to nest the position of a subterm into
	 * a context. It can only go 'up' one layer.
	 * @param s This is the index of the subterm in the bigger term.
	 * @return The method returns the new (nested) position.
	 */
	public Position nestPositions(int s){
		ArrayList<Integer> newPositionApplied = new ArrayList<Integer>();
		newPositionApplied.add(0);				// the position is always 0, followed by
		newPositionApplied.add(s);				// index of subterm
		for (int p: position)
			newPositionApplied.add(p);			// position applied in subterm
		newPositionApplied.remove(2);			// remove the former '0' start-off integer
		Position newPosition = new Position(newPositionApplied);
		return newPosition;
	}
	
	/**
	 * This is a method that will find the position of a subterm in the term.
	 * If the term does not contain the subterm, it will return null. Otherwise it will return 
	 * an array of integers. The length of the array is the depth at which the term is. The numbers
	 * in the array tell you at each depth what subterm you need to enter.
	 * @param t The term that is to be sought through.
	 * @param st The term that is to be searched for.
	 * @return a set of positions of the subterm in the term.
	 */
	public static ArrayList<Position> findPosition (Term t, Term st) {
		// create a collection of positions
		ArrayList <Position> positions = new ArrayList<Position>();
		if (t.subterms!=null) {
			for (int i=0;i<t.subterms.length;i++) {
				// Check if subterm contains wanted term.
				ArrayList <Position> foundpositions = findPosition(t.subterms[i],st);
				if (!(foundpositions.isEmpty())) {
					Position pos = new Position();
					// Create position starting with 0, and followed by index of subterm.
					pos.position.add(0);
					pos.position.add(i);
					for (Position foundpos: foundpositions) {
						foundpos.position.remove(0);
						// Nest found position.
						for (int j: foundpos.position)
								pos.position.add(j);
						positions.add(pos);
					}
				}
			}
		}
		// If the term itself is the wanted term, return position [0].
		if (t.equals(st)) {
			Position pos = new Position();
			pos.position.add(0);
			positions.add(pos);
		}
		return positions;
	}
	
	/**
	 * This method is a method that checks if a certain position is a
	 * prefix of another position.
	 * @param mightContainPrefix this is the position that might contain the prefix
	 * @return true if it is a prefix, false if it is not
	 */
	public boolean isPrefix (Position mightContainPrefix) {
		boolean check1 = false;
		if (this.position.size()<=mightContainPrefix.position.size())
			for (int i=0;i<this.position.size();i++)
				if (this.position.get(i).equals(mightContainPrefix.position.get(i)))
					check1=true;
				else
					return false;
		return check1;
	}
	
	/**
	 * This method is used to subtract a prefix-position from another position
	 * @param pos this is the position that is to be subtracted
	 * @return this is the position that remains
	 */
	public Position subtractPrefix (Position pos) {
		Position newpos = new Position(new ArrayList<Integer>());
		newpos.position.add(0);
		for (int i=0;i<position.size();i++)
			newpos.position.add(position.get(i));
		if (pos.isPrefix(this))
			for (int i=0;i<pos.position.size();i++)
				newpos.position.remove(1);
		return newpos;
	}
}
