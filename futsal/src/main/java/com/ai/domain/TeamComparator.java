package com.ai.domain;

import java.util.Comparator;

public class TeamComparator implements Comparator<TeamDTO> {

    @Override
    public int compare(TeamDTO t1, TeamDTO t2) {
        if(t1.getTTotal() > t2.getTTotal()){
            return 1;
        }else if(t1.getTTotal() < t2.getTTotal()){
            return -1;
        }else{
            return 0;
        }
    }
}
