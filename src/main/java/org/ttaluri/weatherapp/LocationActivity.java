package org.ttaluri.weatherapp;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLocationInputDialog();
    }

    private void showLocationInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a location")
                .setMessage("For US locations, enter as 'City', or 'City, state' /n For international enter as 'City, Country'");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String location = input.getText().toString().trim();
            if (!location.isEmpty()) {

                Toast.makeText(LocationActivity.this, "Location set to: " + location, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(LocationActivity.this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
