package moara.mention.functions;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import moara.mention.entities.GeneMention;
import moara.mention.entities.Token;
import moara.util.Constant;
import moara.util.text.Tokenizer;

public class MentionTagger {
	
	public MentionTagger() {
		
	}
	
	public Vector<Token> tagText(String text, ArrayList<GeneMention> indexes) {
		Vector<Token> tokens = new Vector<Token>();
		GeneMention gm = null;
		int startMention = 0;
		int endMention = 0;
		int index = 0;
		if (indexes.size()>0){
			gm = indexes.get(index);
			startMention = gm.Start();
			endMention = gm.End();
			index++;
		}
		// get the words of the document
		Tokenizer t = new Tokenizer();
		// get the tokens
		t.tokenize(text);
		ArrayList<String> words = t.getTokens();
		Vector<Token> wordstext = new Vector<Token>(words.size());
		for (int i=0; i<words.size(); i++) {
			wordstext.add(new Token(words.get(i),null,null));
		}
		//System.err.println(tokenized);
		// search for the gene mentions, and save the cases
		int position = 0;
		boolean geneMentionFound = false;
		Enumeration<Token> enumer2 = wordstext.elements();
		while (enumer2.hasMoreElements()) {
			Token token = enumer2.nextElement();
			// no more gene mention or no gene mention at all
			if (gm==null) {
				Token tk = new Token(token.TokenText(),null,token.Tag());
				if (tk.Tag()==null)
					tokens = addToken(tokens,tk);
				else {
					tk.setCategory(Constant.CATEGORY_FALSE);
					tokens.add(tk);
				}
				//System.err.println("nogm " + token.TokenText());
			}
			else {
				// variable of position
				int startToken = position;
				int endToken = startToken + token.TokenText().length()-1;
				while (token.TokenText().length()>0) {
					// no more gene mention
					if (gm==null) {
						Token tk = new Token(token.TokenText(),Constant.CATEGORY_FALSE,token.Tag());
						if (tk.Tag()==null)
							tokens = addToken(tokens,tk);
						else 
							tokens.add(tk);
						//System.err.println("gm1 " + token.TokenText());
						position += token.TokenText().length();
						token.setTokenText("");
					}
					else {
						// 1) no gene mention found and no part of gene mention in the token
						// Ex: Comparison with alkaline/GM
						if (!geneMentionFound && endToken<startMention) {
							Token tk = new Token(token.TokenText(),Constant.CATEGORY_FALSE,token.Tag());
							if (tk.Tag()==null)
								tokens = addToken(tokens,tk);
							else 
								tokens.add(tk);
							//System.err.println("gm2 " + token.TokenText());
							position += token.TokenText().length();
							token.setTokenText("");
						}
						else {
							// 2) start of gene mention inside the token
							// Ex: and,5-nucleotidase/GM,
							if (!geneMentionFound && startMention>=startToken && startMention<=endToken) {
								// if there is token before gene mention
								if ((startMention-startToken)>0) {
									Token tk = new Token(token.TokenText().substring(0,startMention-position),Constant.CATEGORY_FALSE,token.Tag());
									if (tk.Tag()==null)
										tokens = addToken(tokens,tk);
									else 
										tokens.add(tk);
									//System.err.println("gm3 " + token.TokenText().substring(0,startMention-position));
								}
								geneMentionFound = true;
								token.setTokenText(token.TokenText().substring(startMention-position,token.TokenText().length()));
								position += startMention-startToken;
								startToken = startMention;
							}
							// 3) gene mention found and end not inside the token
							// Ex: alkaline/GM phosphatases/GM
							if (geneMentionFound && endMention>endToken) {
								Token tk = new Token(token.TokenText(),Constant.CATEGORY_TRUE,token.Tag());
								tokens.add(tk);
								//System.err.println("gm4 " + token.TokenText());
								position += token.TokenText().length();
								token.setTokenText("");
							}
							// 4) gene mention found and its end inside the token
							// Ex: alkaline/GM phosphatases/GM
							else if (geneMentionFound && endMention>=startToken && endMention<=endToken) {
								Token tk = new Token(token.TokenText().substring(0,endMention-position+1),Constant.CATEGORY_TRUE,token.Tag());
								tokens.add(tk);
								//System.err.println("gm5 " + token.TokenText().substring(0,endMention-position+1));
								geneMentionFound = false;
								token.setTokenText(token.TokenText().substring(endMention-position+1,token.TokenText().length()));
								position += endMention-startToken+1;
								startToken = endMention+1;
								// get next gene mention, if there is any
								if (index<indexes.size()){
									gm = indexes.get(index);
									startMention = gm.Start();
									endMention = gm.End();
									index++;
								}
								else {
									gm = null;
								}
							}
						}
					}
				}
			}
		}
		return tokens;
	}
	
	private Vector<Token> addToken(Vector<Token> tokens, Token token) {
		tokens.add(new Token(token.TokenText(),Constant.CATEGORY_FALSE,null));
		return tokens;
	}
	
	/*private Vector addToken(Vector tokens, Token token) {
		Tokenizer t = new Tokenizer();
		Vector ts = t.separateFinalTokens(token.TokenText());
		Enumeration enumer = ts.elements();
		while (enumer.hasMoreElements()) {
			String tk = (String)enumer.nextElement();
			tokens.add(new Token(tk,MentionConstant.CATEGORY_FALSE,null));
		}
		return tokens;
	}*/
	
}
