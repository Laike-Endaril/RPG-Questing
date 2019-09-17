package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.quest.objective.CObjective;

import java.util.ArrayList;

public class QuestTracker
{
    public static String questname = "";
    public static ArrayList<CObjective> objectives = new ArrayList<>();

    public static void stopTracking(String name)
    {
        if (questname != null && questname.equals(name)) stopTracking();
    }

    public static void stopTracking()
    {
        questname = "";
        objectives.clear();
    }

    public static void startTracking(String questName, ArrayList<CObjective> objectives)
    {
        QuestTracker.questname = questName;
        QuestTracker.objectives = objectives;
    }
}
