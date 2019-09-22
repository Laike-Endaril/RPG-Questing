package com.fantasticsource.rpgquesting;

import com.fantasticsource.rpgquesting.actions.*;
import com.fantasticsource.rpgquesting.conditions.*;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.objective.CObjectiveCollect;
import com.fantasticsource.rpgquesting.quest.objective.CObjectiveKill;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Temp
{
    public static void addKillQuest(boolean repeatable)
    {
        CQuest quest = new CQuest("The Wolf named Chicken", "The Wolves", 1, repeatable);
        CQuests.add(quest);

        quest.addObjectives(new CObjectiveKill("chickens killed", 5, new CConditionEntityEntryIs("chicken")));
        quest.addRewards(new ItemStack(Items.CHICKEN));
        quest.setExp(5);


        CDialogue dialogue = new CDialogue().setName("The Wolf named Chicken");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestAvailable(quest));
        dialogue.addEntityConditions(new CConditionEntityEntryIs("wolf"), new CConditionNameIs("Chicken"));

        CDialogueBranch branch = new CDialogueBranch("Yeah, that's right...despite being a wolf, my name is \"Chicken\".  Freaking...you know what?  Go kill 5 chickens for me and maybe I'll tell you how I got the name.");
        dialogue.add(branch);
        branch.add(new CDialogueChoice("Sure.").setAction(new CActionArray(new CActionStartQuest(quest), new CActionEndDialogue())));
        branch.add(new CDialogueChoice("Nah.").setAction(new CActionEndDialogue()));


        dialogue = new CDialogue().setName("The Wolf named Chicken (in progress)");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestInProgress(quest));
        dialogue.addEntityConditions(new CConditionEntityEntryIs("wolf"), new CConditionNameIs("Chicken"));

        branch = new CDialogueBranch("You kill those chickens yet?  Doesn't look like it...");
        dialogue.add(branch);
        branch.add(new CDialogueChoice("End Dialogue").setAction(new CActionEndDialogue()));


        dialogue = new CDialogue().setName("The Wolf named Chicken (complete)");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestReadyToComplete(quest));
        dialogue.addEntityConditions(new CConditionEntityEntryIs("wolf"), new CConditionNameIs("Chicken"));

        branch = new CDialogueBranch("Hey, grats.  You killed some helpless chickens.  Slow clap.");
        CDialogueBranch branch2 = new CDialogueBranch("...None of your business.  Now take this corpse and scram.");
        dialogue.add(branch, branch2);

        CDialogueChoice choice = new CDialogueChoice("Right...so how did you get your name?");
        branch.add(choice);
        choice.setAction(new CActionArray(new CActionBranch(branch2), new CActionCompleteQuest(quest)).addConditions(new CConditionInventorySpace(1)));
        branch.add(new CDialogueChoice("End Dialogue").setAction(new CActionEndDialogue()));

        branch2.add(new CDialogueChoice("Ugh...").setAction(new CActionEndDialogue()));
    }

    public static void addCollectionQuest(boolean repeatable)
    {
        String name = "The Wolves are Hungry";
        CCondition[] dialogueConditions = new CCondition[]{new CConditionEntityEntryIs("wolf"), new CConditionNot(new CConditionNameIs("Chicken"))};
        ItemStack toCollect = new ItemStack(Items.CHICKEN, 5);


        CQuest quest = new CQuest(name, "The Wolves", 1, repeatable);
        CQuests.add(quest);

        quest.addObjectives(new CObjectiveCollect("chicken meat collected", toCollect));
        quest.setExp(15);


        CDialogue dialogue = new CDialogue().setName(name + " (quest)");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestAvailable(quest));
        dialogue.addEntityConditions(dialogueConditions);

        CDialogueBranch branch = new CDialogueBranch("The pack hungers, two-legs...we hunger for the meat of the winged ones...");
        dialogue.add(branch);
        branch.add(new CDialogueChoice("Then I hunt.").setAction(new CActionArray(new CActionStartQuest(quest), new CActionEndDialogue())));
        branch.add(new CDialogueChoice("You'll have to fend for yourselves.").setAction(new CActionEndDialogue()));


        dialogue = new CDialogue().setName(name + " (in progress)");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestInProgress(quest));
        dialogue.addEntityConditions(dialogueConditions);

        branch = new CDialogueBranch("I appreciate the effort, two-legs, but I fear this amount is not enough; the pack would surely fight over it unless we have more.");
        dialogue.add(branch);
        branch.add(new CDialogueChoice("End Dialogue").setAction(new CActionEndDialogue()));


        dialogue = new CDialogue().setName(name + " (complete)");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestReadyToComplete(quest));
        dialogue.addEntityConditions(dialogueConditions);

        branch = new CDialogueBranch("Thank you, two-legs.  I share with you some of my knowledge of the forest.");
        dialogue.add(branch);

        CDialogueChoice choice = new CDialogueChoice("I see...");
        branch.add(choice);
        choice.setAction(new CActionArray(new CActionTakeItems(toCollect).addConditions(new CConditionHaveItems(toCollect)), new CActionEndDialogue(), new CActionCompleteQuest(quest)));
        branch.add(new CDialogueChoice("End Dialogue").setAction(new CActionEndDialogue()));
    }

    public static void addCollectionQuest2(boolean repeatable)
    {
        String name = "Bird Bath";
        CCondition[] dialogueConditions = new CCondition[]{new CConditionEntityEntryIs("chicken")};
        ItemStack toCollect = new ItemStack(Items.WATER_BUCKET, 1);


        CQuest quest = new CQuest(name, "For the Birds", 1, repeatable);
        CQuests.add(quest);

        quest.addObjectives(new CObjectiveCollect("bucket of water collected", toCollect));
        quest.addRewards(new ItemStack(Items.FEATHER, 16));


        CDialogue dialogue = new CDialogue().setName(name + " (quest)");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestAvailable(quest));
        dialogue.addEntityConditions(dialogueConditions);

        CDialogueBranch branch = new CDialogueBranch("I say, now I say I am getting Kentucky FRIED out here, son!  You bring me a bathtub and I'll give you some priiiiime feathers.");
        dialogue.add(branch);
        branch.add(new CDialogueChoice("Alright.").setAction(new CActionArray(new CActionStartQuest(quest), new CActionEndDialogue())));
        branch.add(new CDialogueChoice("Not right now; I'm going swimming.").setAction(new CActionEndDialogue()));


        dialogue = new CDialogue().setName(name + " (complete)");
        CDialogues.add(dialogue);

        dialogue.addPlayerConditions(new CConditionQuestReadyToComplete(quest));
        dialogue.addEntityConditions(dialogueConditions);

        branch = new CDialogueBranch("Now that is a FIIIIINE bath ya got there.");
        dialogue.add(branch);

        CDialogueChoice choice = new CDialogueChoice("All yours!");
        branch.add(choice);
        choice.setAction(new CActionArray(new CActionTakeItems(toCollect).addConditions(new CConditionHaveItems(toCollect)), new CActionEndDialogue(), new CActionCompleteQuest(quest)));
        branch.add(new CDialogueChoice("End Dialogue").setAction(new CActionEndDialogue()));
    }
}
