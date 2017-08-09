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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import net.sourceforge.veditor.VerilogPlugin;
import net.sourceforge.veditor.preference.PreferenceStrings;

public class UCFReconciler extends PresentationReconciler {

	public final static String[] UCFKeywords = new String[] { "NET", "LOC", "IOSTANDARD", "SLEW", "DRIVE", "CONFIG", "PROHIBIT", "PERIOD",
			"CLOCK_DEDICATED_ROUTE", "PIN","LVTTL","FAST","SLOW","PULLDOWN","PULLUP","FALSE","TRUE","TIMESPEC" };

	
	class UCFWordDetector implements IWordDetector {
		@Override
		public boolean isWordStart(char c) {

			return Character.isLetter(c);
		}

		@Override
		public boolean isWordPart(char c) {
			return Character.isLetter(c);
		}
	};

	public UCFReconciler() {

		TextAttribute comment = new TextAttribute(new Color(Display.getCurrent(),
				VerilogPlugin.getPreferenceColor("Color." + PreferenceStrings.MULTI_LINE_COMMENT)));
		TextAttribute keyword = new TextAttribute(new Color(Display.getCurrent(),
				VerilogPlugin.getPreferenceColor("Color." + PreferenceStrings.KEYWORD)));
		TextAttribute stringAtt = new TextAttribute(
				new Color(Display.getCurrent(), VerilogPlugin.getPreferenceColor("Color." + PreferenceStrings.STRING)));

		RuleBasedScanner scanner = new RuleBasedScanner();

		ArrayList<IRule> rules = new ArrayList<IRule>();

		rules.add(new EndOfLineRule("#", new Token(comment)));
		rules.add(new SingleLineRule("\"", "\"", new Token(stringAtt)));

		// Add word rule for keywords.
		WordRule wordRule = new WordRule(new UCFWordDetector(), Token.UNDEFINED, true);
		for (int i = 0; i < UCFKeywords.length; i++) {
			wordRule.addWord(UCFKeywords[i], new Token(keyword));
		}
		rules.add(wordRule);
		scanner.setRules(rules.toArray(new IRule[rules.size()]));
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	}

}