package ink.girigiri.pnavigationdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ink.girigiri.navigation.PNavigatioinView;

public class MainActivity extends AppCompatActivity {
    List<Map<String,String>> list=new ArrayList<>();

    String value[]=new String[20];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 20; i++) {
            Map<String,String> map=new HashMap<>();
            value[i]="value"+i;
            map.put("value",value[i]);
            list.add(map);
        }
        ListView listView=findViewById(R.id.listView);
        listView.setAdapter(new SimpleAdapter(this,
                list,android.R.layout.simple_list_item_1,
                new String[]{"value"},
                new int[]{android.R.id.text1}
                ));
        PNavigatioinView navigatioinView=findViewById(R.id.navigation);
        navigatioinView.setScorllingAction(listView);
        navigatioinView.setOnItemClickListener(new PNavigatioinView.OnPNavigationItemCheckedListener() {
            @Override
            public void onChecked(int position, PNavigatioinView.PNavigationItemView itemView) {
                Toast.makeText(MainActivity.this, itemView.getLabel(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
