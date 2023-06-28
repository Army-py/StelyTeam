package fr.army.stelyteam.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Page {

    private String authorName;
    private int currentPage = 0;
    private int maxElementsPerPage;
    private Collection<Team> teams;

    public Page(String authorName, int maxElementsPerPage, Collection<Team> teams) {
        this.authorName = authorName;
        this.maxElementsPerPage = maxElementsPerPage;
        this.teams = teams;
    }

    public Page setAuthorName(String authorName){
        this.authorName = authorName;
        return this;
    }

    public Page setCurrentPage(int currentPage){
        this.currentPage = currentPage;
        return this;
    }

    public Page setMaxElementsPerPage(int maxElementsPerPage) {
        this.maxElementsPerPage = maxElementsPerPage;
        return this;
    }

    public Page setTeams(ArrayList<Team> teams) {
        this.teams = teams;
        return this;
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

    public Collection<Team> getElements() {
        return this.teams;
    }

    public Page nextPage() {
        if (this.currentPage < this.getPages().size() - 1) {
            this.currentPage++;
        }
        return this;
    }

    public Page previousPage() {
        if (this.currentPage > 0) {
            this.currentPage--;
        }
        return this;
    }

    public List<List<Team>> getPages() {
        List<List<Team>> pages = new ArrayList<List<Team>>();
        List<Team> teams = new ArrayList<Team>(this.teams);
        int pagesCount = (int) Math.ceil((double) this.teams.size() / (double) this.maxElementsPerPage);
        for (int i = 0; i < pagesCount; i++) {
            pages.add(new ArrayList<Team>());
        }
        for (int i = 0; i < this.teams.size(); i++) {
            pages.get(i / maxElementsPerPage).add(teams.get(i));
        }
        return pages;
    }
}
