package com.example.android.newsfeed;

/**
 * class Publication
 *
 * Stores all necessary data about a single publication
 */
public class Publication {
    private String author;      // Name of the author of the publication
    private String articleName; // Name of the publication
    private String datePosted;  // Date when the publication was posted
    private String timePosted;  // Time when the publication was posted
    private String section;     // News section of the publication
    private String storyLink;   // Link of the publication

    /**
     * Constructor Publication
     *
     * @param author      - Name of the author of the publication
     * @param articleName - Name of the publication
     * @param datePosted  - Date when the publication was posted
     * @param timePosted  - Time when the publication was posted
     * @param section     - News section of the publication
     * @param storyLink   - Link of the publication
     */
    Publication(String author, String articleName, String datePosted,
                String timePosted, String section, String storyLink){
        this.author = author;
        this.articleName = articleName;
        this.datePosted = datePosted;
        this.timePosted = timePosted;
        this.section = section;
        this.storyLink = storyLink;
    }

    /**
     * getAuthor
     *
     * @return - Name of the author of the publication
     */
    public String getAuthor() {
        return author;
    }

    /**
     * getArticleName
     *
     * @return - Name of the publication
     */
    public String getArticleName() {
        return articleName;
    }

    /**
     * getDatePosted
     *
     * @return - Date when the publication was posted
     */
    public String getDatePosted() {
        return datePosted;
    }

    /**
     * getTimePosted
     *
     * @return - Time when the publication was posted
     */
    public String getTimePosted() {
        return timePosted;
    }

    /**
     * getSection
     *
     * @return - News section of the publication
     */
    public String getSection() {
        return section;
    }

    /**
     * getStoryLink
     *
     * @return - Link of the publication
     */
    public String getStoryLink() {
        return storyLink;
    }
}
