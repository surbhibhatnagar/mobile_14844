/*
 * Copyright 2012 Jan Kühle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.kuehle.carreport.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import me.kiip.sdk.Kiip;
import me.kiip.sdk.KiipFragmentCompat;
import me.kiip.sdk.Poptart;
import me.kuehle.carreport.R;

public class DataDetailActivity extends AppCompatActivity implements
        AbstractDataDetailFragment.OnItemActionListener {
    public static final String EXTRA_EDIT = "edit";
    public static final String EXTRA_NEW_ID = "new_id";

    public static final int EXTRA_EDIT_REFUELING = 0;
    public static final int EXTRA_EDIT_OTHER = 1;
    public static final int EXTRA_EDIT_CAR = 2;
    public static final int EXTRA_EDIT_REMINDER = 3;

    //kiip start
    private final static String KIIP_TAG = "kiip_fragment_tag";
    private KiipFragmentCompat mKiipFragment;
    Button mbutton;
    //end kiip variables
    //kiip
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail);

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            Fragment fragment;
            int edit = getIntent().getIntExtra(EXTRA_EDIT, EXTRA_EDIT_REFUELING);
            if (edit == EXTRA_EDIT_REFUELING) {
                fragment = new DataDetailRefuelingFragment();
            } else if (edit == EXTRA_EDIT_OTHER) {
                fragment = new DataDetailOtherFragment();
            } else if (edit == EXTRA_EDIT_CAR) {
                fragment = new DataDetailCarFragment();
            } else {
                fragment = new DataDetailReminderFragment();
            }

            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.detail, fragment).commit();
        }
        //kiip start
        // Create or re-use KiipFragment.
        if (savedInstanceState != null) {
            mKiipFragment = (KiipFragmentCompat) getSupportFragmentManager().findFragmentByTag(KIIP_TAG);
        } else {
            mKiipFragment = new KiipFragmentCompat();
            getSupportFragmentManager().beginTransaction().add(mKiipFragment, KIIP_TAG).commit();
        }


        //kiip end
    }

    @Override
    public void onItemSaved(long newId) {
        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_ID, newId);

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onItemCanceled() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onItemDeleted() {
        setResult(RESULT_OK);
        finish();
    }

    //kiip start
    @Override
    protected void onStart() {
        super.onStart();
        Kiip.getInstance().startSession(new Kiip.Callback() {
            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
            }

            @Override
            public void onFinished(Kiip kiip, Poptart poptart) {
                onPoptart(poptart);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Kiip.getInstance().endSession(new Kiip.Callback() {
            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
                Log.e(KIIP_TAG, "Failed to save moment " );

            }

            @Override
            public void onFinished(Kiip kiip, Poptart poptart) {
                //onPoptart(poptart);

            }
        });
    }

    public void onPoptart(Poptart poptart) {
        mKiipFragment.showPoptart(poptart);
    }

    public void iClicked(MenuItem item) {
        //  Context context = getApplicationContext();
        //CharSequence text = momentId;
        // int duration = Toast.LENGTH_SHORT;

        //Toast toast = Toast.makeText(context, momentId, duration);
        // toast.show();

        Kiip.getInstance().saveMoment("kiipmomentId", new Kiip.Callback() {

            @Override
            public void onFinished(Kiip kiip, Poptart reward) {


                onPoptart(reward);
                Context context = getApplicationContext();
                CharSequence text = "hello";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }

            @Override
            public void onFailed(Kiip kiip, Exception exception) {
                // handle failure
                Log.d(KIIP_TAG, "Failed to save moment");
            }
        });

    }
}
//kiip end

