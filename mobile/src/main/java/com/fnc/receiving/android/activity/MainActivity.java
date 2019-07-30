package com.fnc.receiving.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.fnc.receiving.android.BaseActivity;
import com.fnc.receiving.android.R;
import com.fnc.receiving.android.fragment.ChecklistFragment;
import com.fnc.receiving.android.fragment.ReceivingFragment;
import com.fnc.receiving.android.utilities.Helper;
import com.fnc.receiving.android.utilities.SharedData;
import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import java.util.ArrayList;
import hari.bounceview.BounceView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context ctx;
    AdvanceDrawerLayout drawer;

    private ArrayList<Pair> piecesAndButtons = new ArrayList<>();
    private HamButton.Builder builder;
    private SharedData sp;
    public BoomMenuButton bmb;
    public LinearLayout main_header;
    public TextView main_header_title;
    public LinearLayout ll_backbtnbox;
    public Button btn_back;
    private AlertDialog alertDialog;
    private ArrayList<String> mTitles;
//    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_main);

        ctx = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        drawer = (AdvanceDrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setViewScale(Gravity.START, 0.8f);
        drawer.setRadius(Gravity.START, 30);
        drawer.setViewElevation(Gravity.START, 80);
        drawer.useCustomBehavior(Gravity.START);

//        main_header = (LinearLayout) findViewById(R.id.main_header);
//        main_header_title = (TextView) findViewById(R.id.main_header_title);
//        ll_backbtnbox = (LinearLayout) findViewById(R.id.ll_backbtnbox);
//        btn_back = (Button) findViewById(R.id.btn_back);

//        ArrayList<String> sl = new ArrayList<String>();
//        sl.add("SELECT AREA....");
//        sl.add("Bodega 1");
//        sl.add("Bodega 2");
//        alertDialog = Helper.okCancelSpinnerDialogBuilder(ctx,
//            "Please select receiving area",
//            sl,
//            "Proceed",
//            new View.OnClickListener() {
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            }
//        );
//        alertDialog.getWindow().setLayout(800, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
         getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ChecklistFragment(), "checklist_fragment")
                .addToBackStack(null)
                .commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_about:
                break;
            case R.id.nav_logout:
                showLogout();
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initMenu() {
        /*Toolbar a_toolbar = (Toolbar) findViewById(R.id.a_toolbar);

        DuoDrawerLayout drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, drawerLayout, a_toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        DuoMenuView duoMenuView = (DuoMenuView) findViewById(R.id.menu);
        mTitles = new ArrayList<>();
        mTitles.add("Receiving");
        mTitles.add("Checklist");
        mTitles.add("Update Users");
        menuAdapter = new MenuAdapter(mTitles, ctx);
        duoMenuView.setAdapter(menuAdapter);
        duoMenuView.setOnMenuClickListener(new DuoMenuView.OnMenuClickListener() {
            @Override
            public void onFooterClicked() {
                // If the footer view contains a button
                // it will launch this method on the button click.
                // If the view does not contain a button it will listen
                // to the root view click.
            }

            @Override
            public void onHeaderClicked() { }

            @Override
            public void onOptionClicked(int position, Object objectClicked) {
                // Set the toolbar title
                setTitle(mTitles.get(position));
                // Set the right options selected
                menuAdapter.setViewSelected(position, true);
                // Navigate to the right fragment
                switch (position) {
                    default:
                        break;
                }
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        showCloseApp();
    }

    public void showLogout() {
        alertDialog =  Helper.okCancelDialog(ctx, "Log Out", "Are you sure you want to log-out?",
            "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }, 500);
                    alertDialog.hide();
                }
            },
            "No", null, false);
        BounceView.addAnimTo(alertDialog);
    }

    public void showCloseApp() {
        alertDialog =  Helper.okCancelDialog(ctx, "Close App", "Are you sure you want close this app?",
                "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finishAndRemoveTask();
                            }
                        }, 500);
                        alertDialog.hide();
                    }
                },
                "No", null, false);
        BounceView.addAnimTo(alertDialog);
    }
}

