package fr.acanoen.touchalert.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.IOException;

import fr.acanoen.touchalert.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertFragment extends Fragment implements LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private String type;
    //composants de l'interface
    private ImageButton event, danger, solde, medical, cata, more;
    private String name;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    private OnFragmentInteractionListener mListener;

    public AlertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertFragment newInstance(String param1, String param2) {
        AlertFragment fragment = new AlertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        event = view.findViewById(R.id.event);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //affiche le prompt
                showInputDialog("Evénement");
            }
        });

        //le bouton danger
        danger = view.findViewById(R.id.danger);
        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //affiche le prompt
                showInputDialog("Danger");
            }
        });

        //le bouton solde
        solde = view.findViewById(R.id.solde);
        solde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //affiche le prompt
                showInputDialog("Promotions");
            }
        });

        //le bouton medical
        medical = view.findViewById(R.id.medical);
        medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //affiche le prompt
                showInputDialog("Santé");
            }
        });

        //le bouton cata
        cata = view.findViewById(R.id.cata);
        cata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //affiche le prompt
                showInputDialog("Catastrophe");
            }
        });

        //le bouton more
        more = view.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //affiche le prompt
                showInputDialog("Autre");
            }
        });

        return view;
    }


    // envoie la localisation
    private void sendGeo(String typeEvent) {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
           // String provider = locationManager.getBestProvider(criteria, true);

            //Request location updates:
            this.type = typeEvent;
            locationManager.requestLocationUpdates("network", 20, 1, this);
            Location location = locationManager.getLastKnownLocation("network");
            try {
                sendToServer(type, location);
            } catch (IOException e) {
                e.printStackTrace();
            }



        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                sendGeo("event");
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    //appel l'API
    private void sendToServer(String type, Location location) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String url = "https://dev.acanoen.fr/touchalert/public/api/alert";

        RequestBody body =  new FormBody.Builder()
                .add("name", name)
                .add("type", type)
                .add("longitude", location.getLongitude()+"")
                .add("latitude", location.getLatitude() + "")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Log.d("Rep", myResponse);

                }
            }
        });
    }


    protected void showInputDialog(final String typeEvent){

        LayoutInflater layoutInflater = LayoutInflater.from(AlertFragment.this.getActivity());
        View promptView =  layoutInflater.inflate(R.layout.input_dialog, null );
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlertFragment.this.getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       name = String.valueOf(editText.getText());

                        //localisation
                        sendGeo(typeEvent);
                    }
                })
                .setNegativeButton("Annuler",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
