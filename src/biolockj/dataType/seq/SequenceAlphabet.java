package biolockj.dataType.seq;

/**
 * Represents the alphabet of sequence data.
 * @author Ivory Blakley
 *
 */
public interface SequenceAlphabet {
	
	/**
	 * Get the set of characters in the alphabet.
	 * @return
	 */
	public default char[] getCharacters() {
		// An all-chars default; 
		// this allows for implementing class that have an alphabet, but open-ended and/or not known.
		return "abcdefjhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-.?1234567890*".toCharArray();
	}
	
	/**
	 * Is a given character a member of this alphabet ?
	 * @param c
	 * @return
	 */
	public default boolean isMember(char c) {
		boolean isMember = false;
		for (char member : getCharacters() ) if ( c == member ) {
			isMember = true;
			break;
		}
		return isMember;
	}
	
}
