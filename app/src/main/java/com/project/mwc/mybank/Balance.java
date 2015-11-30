package com.project.mwc.mybank;

import android.app.ListFragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Balance.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Balance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Balance extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG_RESULTID = "result";
    private static final String TAG_BALANCE = "balance";
    private static final String TAG_AMOUNT = "amount";


    ArrayList<HashMap<String, String>> balanceList;
//    private SwipeRefreshLayout swipeRefreshLayout;

    JSONArray balances = null;

    ListView listView;
    ArrayAdapter<String> adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Balance() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Balance.
     */
    // TODO: Rename and change types and number of parameters
    public static Balance newInstance(String param1, String param2) {
        Balance fragment = new Balance();
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
            balanceList = new ArrayList<>();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loadHistory();
        return inflater.inflate(R.layout.fragment_balance, container, false);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void loadHistory ( ) {
        DownloadWebPageTask task = new DownloadWebPageTask();
//        historyList.clear();
        task.execute("http://cs.ashesi.edu.gh/~csashesi/class2016/fredrick-abayie/mobileweb/onga_mwc/php/onga.php?cmd=onga_mwc_orders_history&user_id=19882016");
    }


    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        /**
         * Functiont to open an http connection
         *
         * @param urls The url to be sent
         * @return Returning the response
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url1 : urls) {
                try {
                    URL url = new URL(url1);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    System.out.println(url);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(in));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        System.out.println(s);
                        response += s;
                    }
                    System.out.println(response);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    assert urlConnection != null;
                    urlConnection.disconnect();
                }
            }
            if(response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String resultID = jsonObject.getString(TAG_RESULTID);
                    if(resultID.equals("1")) {
                        balances = jsonObject.getJSONArray(TAG_BALANCE);
//                        for (int i = 0; i < users.length(); i++) {
//                            JSONObject jObj = users.getJSONObject(i);

                            String balance = jsonObject.getString(TAG_BALANCE);
//                            String meal_price = "GH\u20B5 "+jObj.getString(TAG_MEALPRICE);
//                            String order_date = jObj.getString(TAG_ORDERDATE);
//                            String order_time = jObj.getString(TAG_ORDERTIME);
//                            String order_status = convert_status(jObj.getString(TAG_ORDERSTATUS));

                            HashMap<String, String> history = new HashMap<>();

                            history.put(TAG_BALANCE, balance);
//                            history.put(TAG_MEALPRICE, meal_price);
//                            history.put(TAG_ORDERDATE, order_date);
//                            history.put(TAG_ORDERTIME, order_time);
//                            history.put(TAG_ORDERSTATUS, order_status);


                            balanceList.add(history);
                            System.out.println(balanceList);
                        }
//                    }

                } catch (JSONException jsonex) {
                    jsonex.printStackTrace();
                }

            }

            return response;

        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            System.out.println(values[0]);
        }


        /**
         * Function to get result from the http post
         *
         * @param result Result from the post
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            setListAdapter(null);
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), balanceList,
                    R.layout.balance_list, new String[] { TAG_BALANCE },
                    new int[] { R.id.list_balance } );

            setListAdapter(adapter);
//            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private String convert_status(String status){
        String state = "ready";
        if(status.equals("not")){
            state="not ready";
        }
        return state;
    }
}
