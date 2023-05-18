package fr.army.stelyteam.team;

import java.util.ArrayList;
import java.util.List;

public class Page {

    private String authorName;
    private int currentPage = 0;
    private int maxElementsPerPage;
    private ArrayList<Team> teams;

    public Page(String authorName, int maxElementsPerPage, ArrayList<Team> teams) {
        this.authorName = authorName;
        this.maxElementsPerPage = maxElementsPerPage;
        this.teams = teams;
    }

    public void setAuthorName(String authorName){
        this.authorName = authorName;
    }

    public void setCurrentPage(int currentPage){
        this.currentPage = currentPage;
    }

    public void setMaxElementsPerPage(int maxElementsPerPage) {
        this.maxElementsPerPage = maxElementsPerPage;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public String getAuthorName(){
        return this.authorName;
    }

    public int getCurrentPage(){
        return this.currentPage;
    }

    public int getMaxElementsPerPage() {
        return this.maxElementsPerPage;
    }

    public List<Team> getElements() {
        return this.teams;
    }

    public ArrayList<List<Team>> getPages() {
        ArrayList<List<Team>> pages = new ArrayList<List<Team>>();
        int pagesCount = (int) Math.ceil((double) this.teams.size() / (double) this.maxElementsPerPage);
        for (int i = 0; i < pagesCount; i++) {
            pages.add(new ArrayList<Team>());
        }
        for (int i = 0; i < this.teams.size(); i++) {
            pages.get(i / maxElementsPerPage).add(this.teams.get(i));
        }
        return pages;
    }
}
