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

package net.sourceforge.veditor.editor;

import java.util.ResourceBundle;

import net.sourceforge.veditor.actions.CollapseAll;
import net.sourceforge.veditor.actions.CompileAction;
import net.sourceforge.veditor.actions.ExpandAll;
import net.sourceforge.veditor.actions.FormatAction;
import net.sourceforge.veditor.actions.GotoMatchingBracketAction;
import net.sourceforge.veditor.actions.OpenDeclarationAction;
import net.sourceforge.veditor.actions.CommentAction;
import net.sourceforge.veditor.actions.ShowInHierarchy;
import net.sourceforge.veditor.actions.ShowInOutline;
import net.sourceforge.veditor.actions.SynthesizeAction;
import net.sourceforge.veditor.actions.UnCommentAction;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

public class HdlActionContributor extends TextEditorActionContributor
{
	private RetargetTextEditorAction contentAssistProposal;
	private IAction gotoMatchingBracket;
	private IAction openDeclaration;
	private IAction collapseAllAction;
	private IAction expandAllAction;
	private IAction format;
	private IAction compile;
	private IAction synthesize;
	private IAction comment;
	private IAction uncomment;
	private IAction showInHierarchy;
	private IAction showInOutline;

	private static final String CONTENT_ASSIST_PROPOSAL = "ContentAssistProposal";

	private ResourceBundle resource;

	public HdlActionContributor()
	{
		super();

		resource = EditorMessages.getResourceBundle();

		contentAssistProposal =
			createAction(
				CONTENT_ASSIST_PROPOSAL,
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		
		gotoMatchingBracket = new GotoMatchingBracketAction();
		openDeclaration = new OpenDeclarationAction();
		collapseAllAction = new CollapseAll();
		expandAllAction   = new ExpandAll();
		format = new FormatAction();
		compile = new CompileAction(); 
		synthesize = new SynthesizeAction();
		comment = new CommentAction();
		uncomment = new UnCommentAction();
		showInHierarchy=new ShowInHierarchy();
		showInOutline=new ShowInOutline();
		
	}

	private RetargetTextEditorAction createAction(String name, String id)
	{
		RetargetTextEditorAction action = new RetargetTextEditorAction(
				resource, name + ".");
		action.setActionDefinitionId(id);
		return action;
	}

	public void init(IActionBars bars)
	{
		super.init(bars);

		IMenuManager menuManager = bars.getMenuManager();

		IMenuManager editMenu = menuManager
				.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null)
		{
			editMenu.add(new Separator());
			editMenu.add(contentAssistProposal);
			editMenu.add(format);
			editMenu.add(comment);
			editMenu.add(uncomment);
		}

		IMenuManager projectMenu = menuManager
		.findMenuUsingPath(IWorkbenchActionConstants.M_PROJECT);
		if (projectMenu != null)
		{			
			projectMenu.insertAfter("buildProject", compile);
			projectMenu.insertAfter(compile.getId(), synthesize);
		}

		IMenuManager navigateMenu = menuManager
				.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null)
		{
			MenuManager showIn=null;
			IMenuManager gotoMenu = navigateMenu
					.findMenuUsingPath(IWorkbenchActionConstants.GO_TO);
			gotoMenu.add(gotoMatchingBracket);

			navigateMenu.add(openDeclaration);
			navigateMenu.add(collapseAllAction);
			navigateMenu.add(expandAllAction);
						
			IContributionItem[] contributionItems=navigateMenu.getItems();
			for(IContributionItem item: contributionItems){
				if (item instanceof MenuManager) {
					MenuManager menu = (MenuManager) item;
					if(menu.getMenuText().startsWith("Sho&w In")){
						showIn=menu;
						break;
					}
				}
			}
			if(showIn!=null){
				//FIXME Need to figure out how to properly add stuff to the "Show In"
				showIn.removeAll();
				showIn.add(showInHierarchy);
				showIn.add(showInOutline);
			}
			
		}
		
	}

	private void setEditorAction(ITextEditor editor,
			final RetargetTextEditorAction action, String id)
	{
		action.setAction(getAction(editor, id));
	}

	private void doSetActiveEditor(IEditorPart part)
	{
		super.setActiveEditor(part);

		ITextEditor editor = null;
		if (part instanceof ITextEditor)
			editor = (ITextEditor)part;

		setEditorAction(editor, contentAssistProposal, CONTENT_ASSIST_PROPOSAL);
	}

	public void setActiveEditor(IEditorPart part)
	{
		super.setActiveEditor(part);
		doSetActiveEditor(part);
	}

	public void dispose()
	{
		doSetActiveEditor(null);
		super.dispose();
	}
}




