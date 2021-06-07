package com.sba.slyoutubers;

public class Channels
{
    private String Name;
    private String Image;
    private String Description;
    private String Link;

    public Channels()
    {

    }
    public Channels(String Name,String Image,String Description,String Link)
    {
        this.Name = Name;
        this.Image = Image;
        this.Description = Description;
        this.Link = Link;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
