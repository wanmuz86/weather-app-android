package my.com.anak2u.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    Button button;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.cityEditText);
        button = findViewById(R.id.searchButton);
        button.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
       adapter = new CustomAdapter();
       recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String cityName = editText.getText().toString();
        String url ="https://api.openweathermap.org/data/2.5/forecast/daily?q="+cityName+"&appid=9fd7a449d055dba26a982a3220f32aa2";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("debug",response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            List<JSONObject> weathers = new ArrayList<>();
                            for (int i = 0; i< jsonResponse.getJSONArray("list").length(); i++){
                                weathers.add(jsonResponse.getJSONArray("list").getJSONObject(i));
                            }
                            adapter.weathers = weathers;
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              Log.d("debug","An error occured");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
       public List<JSONObject> weathers = new ArrayList<>();

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
        }


        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            JSONObject currentWeather = weathers.get(position);
            try {
                holder.tempTextView.setText(
                        (currentWeather.getJSONObject("temp").getDouble("day")-273.15)+ "C");
                holder.dateTextView.setText(currentWeather.getInt("dt")+"");
                holder.weatherTextView.setText(currentWeather.getJSONArray("weather")
                        .getJSONObject(0).getString("main"));
                String iconId = currentWeather.getJSONArray("weather")
                        .getJSONObject(0).getString("icon");
                String imageUrl = "https://openweathermap.org/img/wn/"+iconId+"@2x.png";

                Glide.with(MainActivity.this).load(imageUrl).into(holder.imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return weathers.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView weatherTextView, tempTextView, dateTextView;
            ImageView imageView;
            public CustomViewHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.custom_row, parent, false));
                weatherTextView = itemView.findViewById(R.id.weatherTextView);
                tempTextView = itemView.findViewById(R.id.tempTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                imageView = itemView.findViewById(R.id.imageView);
            }

        }
    }
}
