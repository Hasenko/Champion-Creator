package com.example.projet_mobile;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class SendRequestToRiot {
    private final Context context; // CHATGPT
    private final String language = "en_US";
    private final String urlChampionPath = "https://ddragon.leagueoflegends.com/cdn/14.4.1/data/" + language + "/champion";
    private int requestNumber = 0;
    public interface championTabCallBack
    {
        void returnChampionTab(String[] championTab);
        void returnError(String error);
    }
    public interface spellTabCallBack
    {
        void returnSpellTab(String[][] spellTab);
        void returnError(String error);
    }
    public SendRequestToRiot(Context context) // METHOD BY CHATGPT
    {
        this.context = context;
    }

    // Method to obtain all champion name and store it in an array
    public void getChampionList(final championTabCallBack callBack)
    {
        String url = urlChampionPath + ".json";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try
                    {
                        JSONObject jObject = new JSONObject(response);
                        JSONObject championData = jObject.getJSONObject("data");
                        JSONArray championArray = championData.names();

                        String[] championTab = new String[20];
                        for (int i = 0; i < championTab.length; i++)
                        {
                            championTab[i] = championArray.getString(i);
                        }
                        callBack.returnChampionTab(championTab);
                    }
                    catch (Exception e)
                    {
                        callBack.returnError("Unable to get request result ! : " + e);
                    }
                }, error -> callBack.returnError("That didn't work : " + error.toString()));

        queue.add(stringRequest);
    }

    // Method to obtain all spell information and store it in a 2 dimensions array
    public void getSpellInfoList(String[] championTab, String spell, final spellTabCallBack callBack)
    {
        String[][] spellInfoTab = new String[championTab.length][3];
        final int spellIndex;

        switch (spell) {
            case "Q":
                spellIndex = 0;
                break;
            case "W":
                spellIndex = 1;
                break;
            case "E":
                spellIndex = 2;
                break;
            case "R":
                spellIndex = 3;
                break;
            default:
                spellIndex = 4;
                break;
        }

        for (int i = 0; i < championTab.length; i++) {
            String url = urlChampionPath + "/" + championTab[i] + ".json";
            RequestQueue queue = Volley.newRequestQueue(context); // CHATGPT

            final int indexTab = i;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try
                        {
                            JSONObject jObject = new JSONObject(response);
                            JSONObject championData = jObject.getJSONObject("data").getJSONObject(championTab[indexTab]);

                            String spellName;
                            String spellImageName;
                            String spellDescription;

                            if (spellIndex == 4)
                            {
                                spellName = championData.getJSONObject("passive").getString("name");
                                spellImageName = championData.getJSONObject("passive").getJSONObject("image").getString("full");
                                spellDescription = championData.getJSONObject("passive").getString("description");
                            }
                            else
                            {
                                spellName = championData.getJSONArray("spells").getJSONObject(spellIndex).getString("name");
                                spellImageName = championData.getJSONArray("spells").getJSONObject(spellIndex).getJSONObject("image").getString("full");
                                spellDescription = championData.getJSONArray("spells").getJSONObject(spellIndex).getString("description");
                            }

                            spellInfoTab[indexTab][0] = spellName;
                            spellInfoTab[indexTab][1] = spellImageName;
                            spellInfoTab[indexTab][2] = spellDescription;

                            requestNumber--;
                            if (requestNumber == 0)
                            {
                                callBack.returnSpellTab(spellInfoTab);
                            }
                        }
                        catch (Exception e)
                        {
                            callBack.returnError("Unable to get request result ! : " + e);
                        }
                    }, error -> callBack.returnError("That didn't work : " + error.toString()));

            queue.add(stringRequest);
            requestNumber++;
        }
    }
}
