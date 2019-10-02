package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.rpgquesting.quest.objective.CObjectiveCollect;
import com.fantasticsource.rpgquesting.quest.objective.CObjectiveKill;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.gui.JournalGUI.RED;

public class ObjectiveEditorGUI extends GUIScreen
{
    public CObjective original, selection;
    public GUIObjective current;
    private GUIGradient root;
    private GUITextButton delete;
    private GUIText originalLabel, currentLabel, objectiveSelectorLabel, objectiveEditorLabel;
    private GUIGradientBorder[] separators = new GUIGradientBorder[4];
    private GUIScrollView objectiveSelector, objectiveEditor, originalView, currentView;
    private GUIVerticalScrollbar objectiveSelectorScrollbar, objectiveEditorScrollbar, originalScrollbar, currentScrollbar;

    public ObjectiveEditorGUI(GUIObjective clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.objective;
        selection = original;


        root = new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f));
        guiElements.add(root);


        //Management
        current = new GUIObjective(this, original);
        GUITextButton save = new GUITextButton(this, "Save and Close", JournalGUI.GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.objective;
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(() ->
        {
            selection = original;
            close();
        }));

        delete = new GUITextButton(this, "Delete Objective and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        double free = 1 - delete.height - 0.03;


        separators[0] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[0]);


        //Labels
        originalLabel = new GUIText(this, 0, 0, "ORIGINAL", 3, Color.YELLOW.copy().setVF(0.2f));
        root.add(originalLabel);
        currentLabel = new GUIText(this, 0, 0, "CURRENT", 3, Color.YELLOW.copy().setVF(0.2f));
        root.add(currentLabel);
        objectiveSelectorLabel = new GUIText(this, 0, 0, "OBJECTIVE SELECTION", 3, Color.YELLOW.copy().setVF(0.2f));
        root.add(objectiveSelectorLabel);
        objectiveEditorLabel = new GUIText(this, 0, 0, "OBJECTIVE EDITING", 3, Color.YELLOW.copy().setVF(0.2f));
        root.add(objectiveEditorLabel);


        //Original
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalView = new GUIScrollView(this, 0.44, free / 3);
        root.add(originalView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
        root.add(originalScrollbar);

        originalView.add(new GUIText(this, "\n"));
        GUIObjective originalElement = new GUIObjective(this, current.objective == null ? null : (CObjective) current.objective.copy());
        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.objective)));
        originalView.add(new GUIText(this, "\n"));


        //Current
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentView = new GUIScrollView(this, 0.44, free / 3);
        root.add(currentView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, currentView);
        root.add(currentScrollbar);

        currentView.add(new GUIText(this, "\n"));
        currentView.add(current);
        currentView.add(new GUIText(this, "\n"));


        separators[2] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[2]);


        //Objective selector
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        objectiveSelector = new GUIScrollView(this, 0.94, free / 3);
        root.add(objectiveSelector);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        objectiveSelectorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectiveSelector);
        root.add(objectiveSelectorScrollbar);

        //Objective types
        objectiveSelector.add(new GUIText(this, "\n"));

        objectiveSelector.add(new CObjectiveKill().getChoosableElement(this));
        objectiveSelector.add(new GUIText(this, "\n"));

        objectiveSelector.add(new CObjectiveCollect().getChoosableElement(this));
        objectiveSelector.add(new GUIText(this, "\n"));


        for (int i = objectiveSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = objectiveSelector.get(i);
            if (element instanceof GUIObjective)
            {
                element.addClickActions(() -> setCurrent(((GUIObjective) element).objective));
            }
        }


        separators[3] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[3]);


        //Objective editor
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        objectiveEditor = new GUIScrollView(this, 0.94, free / 3);
        root.add(objectiveEditor);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));

        objectiveEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectiveEditor);
        root.add(objectiveEditorScrollbar);

        setCurrent(current.objective);


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        objectiveSelectorLabel.x = objectiveSelector.x + objectiveSelector.width / 2 - objectiveSelectorLabel.width / 2;
        objectiveSelectorLabel.y = objectiveSelector.y + objectiveSelector.height / 2 - objectiveSelectorLabel.height / 2;

        objectiveEditorLabel.x = objectiveEditor.x + objectiveEditor.width / 2 - objectiveEditorLabel.width / 2;
        objectiveEditorLabel.y = objectiveEditor.y + objectiveEditor.height / 2 - objectiveEditorLabel.height / 2;
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        double free = 1 - delete.height - 0.03;


        //Resize views and scrollbars
        originalView.height = free / 3;
        originalScrollbar.height = free / 3;

        currentView.height = free / 3;
        currentScrollbar.height = free / 3;

        objectiveSelector.height = free / 3;
        objectiveSelectorScrollbar.height = free / 3;

        objectiveEditor.height = free / 3;
        objectiveEditorScrollbar.height = free / 3;


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        objectiveSelectorLabel.x = objectiveSelector.x + objectiveSelector.width / 2 - objectiveSelectorLabel.width / 2;
        objectiveSelectorLabel.y = objectiveSelector.y + objectiveSelector.height / 2 - objectiveSelectorLabel.height / 2;

        objectiveEditorLabel.x = objectiveEditor.x + objectiveEditor.width / 2 - objectiveEditorLabel.width / 2;
        objectiveEditorLabel.y = objectiveEditor.y + objectiveEditor.height / 2 - objectiveEditorLabel.height / 2;


        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CObjective objective)
    {
        current.setObjective(objective);


        objectiveEditor.clear();

        objectiveEditor.add(new GUIText(this, "\n"));

        if (objective != null)
        {
            Class cls = objective.getClass();
            if (cls == CObjectiveKill.class)
            {
//                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity name: ", ((CObjectiveNameIs) objective).name.value, FilterNotEmpty.INSTANCE);
//                name.input.addRecalcActions(() ->
//                {
//                    if (name.input.valid())
//                    {
//                        ((CObjectiveNameIs) objective).name.set(name.input.text);
//                        current.setObjective(objective);
//                    }
//                });
//                objectiveEditor.add(name);
//                objectiveEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CObjectiveCollect.class)
            {
//                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity registry name: ", ((CObjectiveEntityEntryIs) objective).entityEntryName.value, FilterNotEmpty.INSTANCE);
//                name.input.addRecalcActions(() ->
//                {
//                    if (name.input.valid())
//                    {
//                        ((CObjectiveEntityEntryIs) objective).entityEntryName.set(name.input.text);
//                        current.setObjective(objective);
//                    }
//                });
//                objectiveEditor.add(name);
//                objectiveEditor.add(new GUIText(this, "\n"));
            }
        }

        currentView.recalc();
    }
}
