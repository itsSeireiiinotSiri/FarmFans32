package com.example;

import java.util.ArrayList;
import java.util.LinkedList;

public class EventCalendar
{
    //Instance variables
    LinkedList<Event> linEvents = new LinkedList<>();

    public void SortByDate (){//sorts the LL by date (NOT THOROUGHLY TESTED BEWARE!!!)
        boolean booIsSorted = false;

        if (linEvents.size() < 2){return;}

        while (booIsSorted == false){
            booIsSorted = true;
            for (int i = 1; i < linEvents.size();) {
                if (linEvents.get(i).calDate.before(linEvents.get(i-1).calDate)){
                    booIsSorted = false;
                    Event swapTemp = linEvents.get(i);
                    linEvents.remove(i);
                    linEvents.add(i-1,swapTemp);
                } else {
                    i++;
                }
            }
        }
        return;
    }
    public void FetchEvents(ArrayList<Event> eventList){//takes the inputted array list of events then clears and replaces its elements with a ordred array of all events
        eventList.clear();
        this.SortByDate();
        for (int i = 0; i < linEvents.size(); i++) {
            eventList.add(linEvents.get(i));
        }
        //System.out.println(linEvents);
    }

    public void LoadEvents(ArrayList<Event> eventList){//loads a event list into the big LL
        linEvents.clear();
        for (int i = 0; i < eventList.size(); i++) {
            linEvents.addLast(eventList.get(i));
        }
        //System.out.println(linEvents);
    }
}
