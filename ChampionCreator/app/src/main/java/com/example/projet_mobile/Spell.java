package com.example.projet_mobile;

/* CLASS FOR SPELL INFORMATION */
public class Spell {
    private String category;
    private String spellImageName;
    private String championName;
    private String spellName;
    private String description;
    public Spell(){}
    public Spell(String championName, String spellName, String spellImageName, String category, String description)
    {
        this.championName = championName;
        this.spellName = spellName;
        this.spellImageName = spellImageName;
        this.category = category;
        this.description = description;
    }
    public String getSpellImageName()
    {
        return spellImageName;
    }

    public String getChampionName()
    {
        if (championName == null)
            return "00000000";
        return championName;
    }

    public String getSpellName()
    {
        if (spellName == null)
            return "00000000";
        return spellName;
    }

    public String getCategory() { return category; }

    public String getDescription() {
        return description;
    }

}
