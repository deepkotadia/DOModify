package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		Stack<TagNode> stk= new Stack<TagNode>(); //stack containing pointers to tree to keep track of position
		
		root= new TagNode("html", null, null); //creating the html node as the root of the tree
		stk.push(root);
		
		int counter= 0;
		while(sc.hasNextLine() == true){
			String line= sc.nextLine();
			
			if(counter == 0){
				counter++;
				continue;
			}
			
			if(line.startsWith("<") == true && line.endsWith(">") == true && line.charAt(1) != '/'){ //line is an opening html tag
				
				TagNode opentag= new TagNode(line.substring(1, line.lastIndexOf('>')), null, null);
				
				if(stk.peek().firstChild == null){
					stk.peek().firstChild = opentag;
					stk.push(opentag);
				}
				else{
					TagNode currentptr= stk.peek().firstChild;
					while(currentptr.sibling != null){
						currentptr= currentptr.sibling;
					}
					currentptr.sibling= opentag;
					stk.push(opentag);
				}
			}
			
			else if(line.startsWith("<") == true && line.endsWith(">") == true && line.charAt(1) == '/'){ //line is a closing html tag
				stk.pop();
				continue;
			}
			
			else{ //line is not a tag
				
				TagNode linenottag= new TagNode(line, null, null);
				
				if(stk.peek().firstChild == null){
					stk.peek().firstChild= linenottag;
				}
				else{
					TagNode currentptr= stk.peek().firstChild;
					while(currentptr.sibling != null){
						currentptr= currentptr.sibling;
					}
					currentptr.sibling= linenottag;
				}
			}
		}
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		if(oldTag == null || newTag == null){
			return;
		}
		
		replacerecursively(root, oldTag, newTag);
	}
	
	private void replacerecursively(TagNode ptr, String oldTag, String newTag){
		if(ptr == null){
			return;
		}
		
		if(ptr.tag.equals(oldTag)){
			ptr.tag= newTag;
		}
		
		replacerecursively(ptr.firstChild, oldTag, newTag);
		replacerecursively(ptr.sibling, oldTag, newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		boldRowRec(root, row);
	}
	
	private void boldRowRec(TagNode ptr, int row){
		if(ptr == null){
			return;
		}
		
		if(ptr.tag.equals("table")){
			TagNode start= ptr.firstChild;
			for(int count= 1; count< row; count++){
				start= start.sibling;
			}
			TagNode bRow= start;
			
			TagNode colnum= bRow.firstChild;
			
			while(colnum.sibling != null){
				TagNode newNode= new TagNode("b", null, null);
				newNode.firstChild= colnum.firstChild;
				colnum.firstChild= newNode;
				colnum= colnum.sibling;
			}
			TagNode newNode= new TagNode("b", null, null);
			newNode.firstChild= colnum.firstChild;
			colnum.firstChild= newNode;
		}
		
		boldRowRec(ptr.firstChild, row);
		boldRowRec(ptr.sibling, row);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if(tag.equals("p") || tag.equals("em") || tag.equals("b")){
			removecategory1(root, tag);
		}
		
		else if(tag.equals("ol") || tag.equals("ul")){
			removecategory2(root, tag);
		}
	}
	
	private void removecategory1(TagNode ptr, String tag){
		if(ptr == null){
			return;
		}
		
		if(ptr.sibling != null && ptr.sibling.tag.equals(tag)){
			TagNode tbd= ptr.sibling;
			if(tbd.sibling != null){
				ptr.sibling= tbd.firstChild;
				TagNode lastnode= tbd.firstChild;
				while(lastnode.sibling != null){
					lastnode= lastnode.sibling;
				}
				lastnode.sibling= tbd.sibling;
			}
			else{
				ptr.sibling= tbd.firstChild;
			}
		}
		
		if(ptr.firstChild != null && ptr.firstChild.tag.equals(tag)){
			TagNode tbd= ptr.firstChild;
			if(tbd.sibling == null){
				ptr.firstChild= tbd.firstChild;
			}
			else{
				ptr.firstChild= tbd.firstChild;
				TagNode lastn= tbd.firstChild;
				while(lastn.sibling != null){
					lastn= lastn.sibling;
				}
				lastn.sibling= tbd.sibling;
			}
		}
		
		removecategory1(ptr.firstChild, tag);
		removecategory1(ptr.sibling, tag);
	}
	
	
	private void removecategory2(TagNode ptr, String tag){
		if(ptr == null){
			return;
		}
		
		if(ptr.sibling != null && ptr.sibling.tag.equals(tag)){
			TagNode tbd= ptr.sibling;
			if(tbd.sibling != null){
				ptr.sibling= tbd.firstChild;
				TagNode lastnode= tbd.firstChild;
				while(lastnode.sibling != null){
					if(lastnode.tag.equals("li")){
					lastnode.tag= "p";
					}
					lastnode= lastnode.sibling;
				}
				if(lastnode.tag.equals("li")){
					lastnode.tag= "p";
				}
				lastnode.sibling= tbd.sibling;
			}
			else{
				ptr.sibling= tbd.firstChild;
				TagNode lastnode= tbd.firstChild;
				while(lastnode.sibling != null && lastnode.sibling.tag.equals("li")){
					lastnode.tag= "p";
					lastnode= lastnode.sibling;
				}
				if(lastnode.tag.equals("li")){
					lastnode.tag= "p";
				}
			}
		}
		
		if(ptr.firstChild != null && ptr.firstChild.tag.equals(tag)){
			TagNode tbd= ptr.firstChild;
			if(tbd.sibling == null){
				ptr.firstChild= tbd.firstChild;
				TagNode lastn= tbd.firstChild;
				while(lastn.sibling != null && lastn.sibling.tag.equals("li")){
					lastn.tag= "p";
					lastn= lastn.sibling;
				}
				if(lastn.tag.equals("li")){
					lastn.tag= "p";
				}
			}
			else{
				ptr.firstChild= tbd.firstChild;
				TagNode lastn= tbd.firstChild;
				while(lastn.sibling != null){
					if(lastn.tag.equals("li")){
					lastn.tag= "p";
					}
					lastn= lastn.sibling;
				}
				if(lastn.tag.equals("li")){
					lastn.tag= "p";
				}
				lastn.sibling= tbd.sibling;
			}
		}
		
		removecategory2(ptr.firstChild, tag);
		removecategory2(ptr.sibling, tag);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		if(word == null || tag == null){
			return;
		}
		
		addTagRec(root, word, tag);
	}
	
	private void addTagRec(TagNode ptr, String word, String tag){
		if(ptr == null){
			return;
		}
		
		if(ptr.sibling != null && ptr.sibling.firstChild == null){ //node tag is plain text
			String sentence= ptr.sibling.tag;
			
			if(sentence.equalsIgnoreCase(word)){ //node tag is exactly the same as the word
				TagNode newNode= new TagNode(tag, null, null);
				TagNode tbc= ptr.sibling;
				newNode.firstChild= tbc;
				ptr.sibling= newNode;
				
				if(tbc.sibling != null){
				newNode.sibling= tbc.sibling;
				tbc.sibling= null;
				}		
			}	
		}
		
		else if (ptr.tag.toUpperCase().contains(word.toUpperCase())){
			TagNode sibling = ptr.sibling;

			String backstring= ptr.tag.substring(0, ptr.tag.toLowerCase().indexOf(word.toLowerCase()));
			
			String empty= "";
			
			String a= ptr.tag.substring(ptr.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
			
			String o= ptr.tag.substring(ptr.tag.toLowerCase().indexOf(word.toLowerCase()), ptr.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());

			if (a.length() > 0){
				if (a.length() > 1 && (this.Punctuation(a.charAt(0)) && !Punctuation(a.charAt(1)))) {
					empty= "" + empty.charAt(0);
					a= a.substring(1);
				}
			}
			if (a.length() == 0 || (a.length() >= 1 && (a.charAt(0) == ' ' || Punctuation(a.charAt(0))))){
				if (a.equals("!") || a.equals(",") || a.equals(".") || a.equals("?")) {
					o = o + a;
					a= "";
				}
				ptr.tag = backstring;
				ptr.sibling = new TagNode(tag, new TagNode(o+empty, null, null), null);

				if (a.length() > 0){
					if (sibling != null)
						ptr.sibling.sibling = new TagNode(a, null, sibling);
					else
						ptr.sibling.sibling = new TagNode(a, null, null);
				} 
				else if (sibling != null){
					ptr.sibling.sibling = sibling;
				} 
			} 
		
			addTagRec(ptr.sibling.sibling, word, tag);
		}
		
		else{
			addTagRec(ptr.firstChild, word, tag);
			addTagRec(ptr.sibling, word, tag);
		}
	}
	
	
	private boolean Punctuation(char c) {
		if(c == '.' || c == ',' || c == '?' || c == '!' || c == ':' || c == ';'){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
