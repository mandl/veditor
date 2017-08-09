/*******************************************************************************
 * Copyright (c) 2017 Stefan Mandl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Mandl 
 *******************************************************************************/


package net.sourceforge.veditor.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class UCFContentAssistProcessor implements IContentAssistProcessor {
	
	

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    	  int index = offset - 1;
          StringBuffer prefix = new StringBuffer();
          IDocument document = viewer.getDocument();
          while (index > 0) {
              try {
                  char prev = document.getChar(index);
                  if (Character.isWhitespace(prev)) {
                      break;
                  }
                  prefix.insert(0, prev);
                  index--;
              } catch (BadLocationException e) {
              }
          }
          
          List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
          final String[] keywords = UCFReconciler.UCFKeywords;
          if (prefix.length() > 0) {
              String word = prefix.toString();
              for (int i = 0; i < keywords.length; i++) {
                  String keyword = keywords[i];
                  if (keyword.startsWith((word.toUpperCase())) && word.length() < keyword.length()) {
                      proposals.add(new CompletionProposal(keyword + " ", index + 1, offset - (index + 1), keyword.length() + 1));
                  }
              }
          } else {
              // propose all keywords
              for (int i = 0; i < keywords.length; i++) {
                  String keyword = keywords[i];
                  proposals.add(new CompletionProposal(keyword + " ", offset, 0, keyword.length() + 1));
              }
          }
          if (!proposals.isEmpty()) {
              return proposals.toArray(new ICompletionProposal[proposals.size()]);
          }
          return null;
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return null;
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

}