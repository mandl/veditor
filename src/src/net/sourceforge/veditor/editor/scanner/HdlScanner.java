/*******************************************************************************
 * Copyright (c) 2004, 2006 KOBAYASHI Tadashi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    KOBAYASHI Tadashi - initial API and implementation
 *******************************************************************************/

package net.sourceforge.veditor.editor.scanner;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.veditor.VerilogPlugin;
import net.sourceforge.veditor.document.HdlDocument;
import net.sourceforge.veditor.editor.ColorManager;
import net.sourceforge.veditor.editor.HdlTextAttribute;
import net.sourceforge.veditor.editor.scanner.verilog.VerilogWordRule;
import net.sourceforge.veditor.editor.scanner.vhdl.VhdlWordRule;
import net.sourceforge.veditor.preference.PreferenceStrings;

import org.eclipse.jface.text.rules.*;


/**
 * find reserved word
 */
public class HdlScanner extends RuleBasedScanner
{
	public static HdlScanner createForVerilog(ColorManager manager)
	{
		return new HdlScanner(manager, true);
	}
	public static HdlScanner createForVhdl(ColorManager manager)
	{
		return new HdlScanner(manager, false);
	}

	private static final String[] verilogWords = {"always", "and", "assign",
			"attribute", "begin", "buf", "bufif0", "bufif1", "case", "casex",
			"casez", "cmos", "deassign", "default", "defparam", "disable",
			"edge", "else", "end", "endattribute", "endcase", "endfunction", "endmodule",
			"endprimitive", "endspecify", "endtable", "endtask", "event",
			"for", "force", "forever", "fork", "function", "highz0", "highz1",
			"if", "ifnone", "initial", "inout", "input", "integer", "join",
			"medium", "module", "large", "macromodule", "nand", "negedge",
			"nmos", "nor", "not", "notif0", "notif1", "or", "output",
			"parameter", "pmos", "posedge", "primitive", "pull0", "pull1",
			"pulldown", "pullup", "rcmos", "real", "realtime", "reg",
			"release", "repeat", "rnmos", "rpmos", "rtran", "rtranif0",
			"rtranif1", "scalared", "signed", "small", "specify", "specparam",
			"strenght", "strong0", "strong1", "supply0", "supply1", "table",
			"task", "time", "tran", "tranif0", "tranif1", "tri", "tri0",
			"tri1", "triand", "trior", "trireg", "unsigned", "vectored",
			"wait", "wand", "weak0", "weak1", "while", "wire", "wor", "xnor",
			"xor", "generate", "endgenerate", "genvar", "localparam"};

	private static final String[] verilogDirectives = { "`ifdef", "`else",
			"`endif", "`if", "`define", "`undef", "`timescale", "`include" };
	
	private static final String[] verilogAmsWords = { "abs", "absdelay", "acos", "acosh", "ac_stim", "analog",
			"analysis", "asin", "asinh", "atan", "atan2", "atanh", "branch", "ceil", "connectrules", "cos", "cosh",
			"cross", "ddt", "discipline", "driver_update", "enddiscipline", "endconnectrules", "endnature", "exclude",
			"exp", "final_step", "flicker_noise", "floor", "flow", "from", "ground", "hypot", "idt", "idtmod", "inf",
			"initial_step", "laplace_nd", "laplace_np", "laplace_zd", "laplace_zp", "last_crossing", "limexp", "ln",
			"log", "max", "min", "nature", "net_resolution", "noise_table", "potential", "pow", "sin", "sinh", "slew",
			"sqrt", "tan", "tanh", "timer", "transition", "wreal", "zi_nd", "zi_np", "zi_zd", "zi_zp" };

	public static final String[] vhdlWords = { "abs", "access", "across", "after",
			"alias", "all", "and", "architecture", "array", "assert",
			"attribute", "begin", "block", "body", "break", "buffer", "bus", "case", "context",
			"component", "configuration", "constant", "disconnect", "downto","default",
			"else", "elsif", "end", "entity", "exit", "file", "for", "force",
			"function", "generate", "generic", "group", "guarded", "if", "impure", "in",
			"inertial", "inout", "is", "label", "library", "linkage",
			"literal", "loop", "map", "mod", "nand", "nature", "new", "next", "noise", "nor",
			"not", "null", "of", "on", "open", "or", "others", "out",
			"package", "port", "postponed", "procedural", "procedure", "process", "protected", "pure",
			"quantity", "range", "record", "register", "reject", "rem", "release","report", "return",
			"rol", "ror", "select", "severity", "shared", "signal", "sla",
			"sll", "spectrum", "sra", "srl", "subnature", "subtype", "terminal", "then",
			"through", "to", "tolerance", "transport", "type",
			"unaffected", "units", "until", "use", "variable", "wait", "when",
			"while", "with", "xnor", "xor" };
	
	private static final String[] vhdlTypes = { "bit", "bit_vector", "character", 
			"boolean", "integer", "real", "time", "string",	"severity_level", 
			"positive", "natural", "signed", "unsigned", "line", "text",
			"std_logic", "std_logic_vector", "std_ulogic", "std_ulogic_vector", 
			"qsim_state", "qsim_state_vector", "qsim_12state",
			"qsim_12state_vector", "qsim_strength", "mux_bit", "mux_vector", 
			"reg_bit", "reg_vector", "wor_bit",	"wor_vector"};

	private HdlScanner(ColorManager manager, boolean isVerilog) {
		IToken keyword = new Token(HdlTextAttribute.KEY_WORD
				.getTextAttribute(manager));
		IToken directive = new Token(HdlTextAttribute.DIRECTIVE
				.getTextAttribute(manager));
		IToken other = new Token(HdlTextAttribute.DEFAULT
				.getTextAttribute(manager));
		IToken types = new Token(HdlTextAttribute.TYPES.getTextAttribute(manager));

		List<IRule> rules = new ArrayList<IRule>();

		WordRule wordRule;
		IWordDetector detector;
		if (isVerilog) {
			detector = new net.sourceforge.veditor.editor.scanner.verilog.WordDetector();
			wordRule = new VerilogWordRule(detector, other, manager);
			for (int i = 0; i < verilogDirectives.length; i++)
				wordRule.addWord(verilogDirectives[i], directive);
			for (int i = 0; i < verilogWords.length; i++)
				wordRule.addWord(verilogWords[i], keyword);
			if (VerilogPlugin.getPreferenceBoolean(PreferenceStrings.VERILOG_AMS)) {
				for (int i = 0; i < verilogAmsWords.length; i++)
					wordRule.addWord(verilogAmsWords[i], keyword);
			}
		} else {
			detector = new net.sourceforge.veditor.editor.scanner.vhdl.WordDetector();
			wordRule = new VhdlWordRule(detector, other, manager);
			for (int i = 0; i < vhdlWords.length; i++)
				wordRule.addWord(vhdlWords[i], keyword);
			// it is possible to use upper case in VHDL
			for (int i = 0; i < vhdlWords.length; i++)
				wordRule.addWord(vhdlWords[i].toUpperCase(), keyword);

			for (int i = 0; i < vhdlTypes.length; i++)
				wordRule.addWord(vhdlTypes[i], types);
			// it is possible to use upper case in VHDL
			for (int i = 0; i < vhdlTypes.length; i++)
				wordRule.addWord(vhdlTypes[i].toUpperCase(), types);
		}
		
		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
	
	public HdlDocument getHdlDocument() {
		if (fDocument instanceof HdlDocument) {
			return (HdlDocument)fDocument;
		} else {
			return null;
		}
	}
}

