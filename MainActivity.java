package com.mizanur.totthosongroho;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, list;
    EditText search;
    ArrayList<JSONObject> people = new ArrayList<>();
    android.content.SharedPreferences prefs;

    int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    TextView tv(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(dp(12), dp(10), dp(12), dp(10));
        return t;
    }

    Button btn(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        return b;
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("data", MODE_PRIVATE);
        load();
        home();
    }

    void load() {
        people.clear();
        try {
            JSONArray a = new JSONArray(prefs.getString("people","[]"));
            for(int i=0;i<a.length();i++) people.add(a.getJSONObject(i));
        } catch(Exception ignored) {}
    }

    void save() {
        JSONArray a = new JSONArray();
        for(JSONObject o: people) a.put(o);
        prefs.edit().putString("people", a.toString()).apply();
    }

    void base(String title) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        TextView bar = tv(title, 21, Color.WHITE);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setTypeface(null, 1);
        bar.setBackgroundColor(Color.rgb(25,118,210));
        root.addView(bar, new LinearLayout.LayoutParams(-1, dp(58)));

        setContentView(root);
    }

    void home() {
        base("তথ্য সংগ্রহ");
        LinearLayout top = new LinearLayout(this);
        top.setPadding(dp(10),dp(10),dp(10),dp(4));
        top.setOrientation(LinearLayout.VERTICAL);

        TextView intro = tv("তথ্য সংরক্ষণ, নাম দিয়ে অনুসন্ধান ও প্রোফাইল দেখুন",16,Color.DKGRAY);
        top.addView(intro);

        search = new EditText(this);
        search.setHint("নাম দিয়ে Search করুন");
        search.setSingleLine(true);
        top.addView(search, new LinearLayout.LayoutParams(-1,dp(55)));
        root.addView(top);

        Button add = btn("＋ নতুন তথ্য যোগ করুন");
        root.addView(add, new LinearLayout.LayoutParams(-1,dp(55)));
        add.setOnClickListener(v -> addForm());

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv = new ScrollView(this);
        sv.addView(list);
        root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        search.addTextChangedListener(new android.text.TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int b,int c){ render(s.toString());}
            public void afterTextChanged(android.text.Editable e){}
        });
        render("");
    }

    void render(String q) {
        if(list==null)return;
        list.removeAllViews();
        int count=0;
        for(JSONObject p: people) {
            String name=p.optString("name");
            if(q.trim().length()>0 && !name.toLowerCase().contains(q.toLowerCase())) continue;
            count++;
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(8),dp(4),dp(8),dp(4));
            TextView n=tv(name,18,Color.rgb(25,70,120));
            n.setTypeface(null,1);
            card.addView(n);
            card.addView(tv(p.optString("occupation","পেশা উল্লেখ নেই"),14,Color.DKGRAY));
            Button open=btn("প্রোফাইল দেখুন");
            card.addView(open);
            open.setOnClickListener(v->profile(p));
            list.addView(card);
            View line=new View(this);
            line.setBackgroundColor(Color.LTGRAY);
            list.addView(line,new LinearLayout.LayoutParams(-1,dp(1)));
        }
        if(count==0) list.addView(tv("কোনো তথ্য পাওয়া যায়নি। নতুন তথ্য যোগ করুন।",16,Color.GRAY));
    }

    EditText field(LinearLayout box, String hint) {
        EditText e=new EditText(this);
        e.setHint(hint);
        e.setSingleLine(false);
        box.addView(e,new LinearLayout.LayoutParams(-1,dp(54)));
        return e;
    }

    void addForm() {
        base("নতুন তথ্য যোগ করুন");
        ScrollView sv=new ScrollView(this);
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14),dp(8),dp(14),dp(20));
        sv.addView(box);
        root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        EditText name=field(box,"নাম *");
        EditText father=field(box,"পিতার নাম");
        EditText mother=field(box,"মাতার নাম");
        EditText dob=field(box,"জন্মতারিখ");
        EditText phone=field(box,"মোবাইল নম্বর");
        EditText address=field(box,"ঠিকানা");
        EditText occupation=field(box,"পেশা");
        EditText education=field(box,"শিক্ষাগত যোগ্যতা");
        EditText blood=field(box,"রক্তের গ্রুপ");
        EditText notes=field(box,"অতিরিক্ত তথ্য / নোট");

        Button save=btn("তথ্য সংরক্ষণ করুন");
        box.addView(save);
        save.setOnClickListener(v->{
            if(name.getText().toString().trim().isEmpty()){
                name.setError("নাম লিখুন"); return;
            }
            try {
                JSONObject p=new JSONObject();
                p.put("name",name.getText().toString().trim());
                p.put("father",father.getText().toString());
                p.put("mother",mother.getText().toString());
                p.put("dob",dob.getText().toString());
                p.put("phone",phone.getText().toString());
                p.put("address",address.getText().toString());
                p.put("occupation",occupation.getText().toString());
                p.put("education",education.getText().toString());
                p.put("blood",blood.getText().toString());
                p.put("notes",notes.getText().toString());
                people.add(0,p); save();
                Toast.makeText(this,"তথ্য সংরক্ষণ হয়েছে",Toast.LENGTH_SHORT).show();
                home();
            } catch(Exception e){ Toast.makeText(this,"সংরক্ষণ করা যায়নি",Toast.LENGTH_SHORT).show(); }
        });
    }

    void profile(JSONObject p) {
        base("প্রোফাইল");
        ScrollView sv=new ScrollView(this);
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16),dp(16),dp(16),dp(20));
        sv.addView(box); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        TextView name=tv(p.optString("name"),26,Color.rgb(25,70,120));
        name.setTypeface(null,1); box.addView(name);
        addRow(box,"পিতার নাম",p.optString("father"));
        addRow(box,"মাতার নাম",p.optString("mother"));
        addRow(box,"জন্মতারিখ",p.optString("dob"));
        addRow(box,"মোবাইল",p.optString("phone"));
        addRow(box,"ঠিকানা",p.optString("address"));
        addRow(box,"পেশা",p.optString("occupation"));
        addRow(box,"শিক্ষাগত যোগ্যতা",p.optString("education"));
        addRow(box,"রক্তের গ্রুপ",p.optString("blood"));
        addRow(box,"অতিরিক্ত তথ্য",p.optString("notes"));

        Button del=btn("এই তথ্য মুছে দিন");
        box.addView(del);
        del.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("তথ্য মুছবেন?")
            .setMessage("এই প্রোফাইলটি ডিভাইস থেকে মুছে যাবে।")
            .setNegativeButton("না",null)
            .setPositiveButton("মুছে দিন",(d,w)->{
                people.remove(p); save(); home();
            }).show());
    }

    void addRow(LinearLayout box,String label,String value){
        if(value==null||value.trim().isEmpty()) return;
        TextView t=tv(label+" : "+value,16,Color.DKGRAY);
        box.addView(t);
        View line=new View(this); line.setBackgroundColor(Color.LTGRAY);
        box.addView(line,new LinearLayout.LayoutParams(-1,dp(1)));
    }
}
