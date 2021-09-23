package at.ac.univie.lumi.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import at.ac.univie.lumi.R;
import at.ac.univie.lumi.controller.ActivityConstants;

/**
 *
 * This activity displays the scenario chooser information and let the user choose which scenario the user want to start.
 */

public class ScenarioChooserActivity extends AppCompatActivity {

    private Button scenario1;
    private Button scenario2;
    private Button scenario3;

    public final static String EXTRA_SCENARIO = "at.ac.univie.lumi.SCENARIO";

    /**
     * This method is called when the activity is ready. It initialize the different buttons for the scenario choosing. Moreover, it sends via intent the choosen scenario date to the splash activity.
     * @param savedInstanceState Can be used to save the current state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_chooser);

        scenario1 = (Button) findViewById(R.id.scenario1Button);
        scenario2 = (Button) findViewById(R.id.scenario2Button);
        scenario3 = (Button) findViewById(R.id.scenario3Button);

        scenario1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start scenario 1
                Intent intent = new Intent(getApplicationContext(), SplashLoadingActivity.class);
                intent.putExtra(EXTRA_SCENARIO, ActivityConstants.Scenario1);
                startActivity(intent);
                finish(); //no return with the back arrow possible
            }
        });

        scenario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start scenario 2
                Intent intent = new Intent(getApplicationContext(), SplashLoadingActivity.class);
                intent.putExtra(EXTRA_SCENARIO, ActivityConstants.Scenario2);
                startActivity(intent);
                finish(); //no return with the back arrow possible

            }
        });

        scenario3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start scenario 3
                Intent intent = new Intent(getApplicationContext(), SplashLoadingActivity.class);
                intent.putExtra(EXTRA_SCENARIO, ActivityConstants.Scenario3);
                startActivity(intent);
                finish(); //no return with the back arrow possible
            }
        });
    }
}
