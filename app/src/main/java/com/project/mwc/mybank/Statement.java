package com.project.mwc.mybank;

import android.app.ListFragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * {@link Statement.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Statement extends ListFragment {

    SessionManager sessionManager;
    String user_id = null;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG_RESULTID = "result";
    private static final String TAG_BALANCE = "balance";
    private static final String TAG_TRANSACTIONDATE = "transaction_date";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DEBIT = "debt";
    private static final String TAG_STATEMENT = "statement";


    ArrayList<HashMap<String, String>> statementList;
//    private SwipeRefreshLayout swipeRefreshLayout;

    JSONArray statement = null;

    ListView listView;
    ArrayAdapter<String> adapter;

    private OnFragmentInteractionListener mListener;

    public Statement() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        user_id = user.get(SessionManager.KEY_CUSTOMERID);
        statementList = new ArrayList<>();
//        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statement, container, false);

        loadHistory();
        return rootView;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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
        task.execute("http://cs.ashesi.edu.gh/~csashesi/class2016/fredrick-abayie/mobileweb/mybank/php/mybank.php?cmd=check_statement&user_id="+user_id);
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
                        statement = jsonObject.getJSONArray(TAG_STATEMENT);
                        for (int i = 0; i < statement.length(); i++) {
                            JSONObject jObj = statement.getJSONObject(i);

                            String transaction_date = jObj.getString(TAG_TRANSACTIONDATE);
                            String debt = "GH\u20B5 "+jObj.getString(TAG_DEBIT);
                            String description = jObj.getString(TAG_DESCRIPTION);
                            String balance = "GH\u20B5 "+jObj.getString(TAG_BALANCE);
//                            String period = jObj.getString(TAG_PERIOD);

                            HashMap<String, String> statement = new HashMap<>();

                            statement.put(TAG_TRANSACTIONDATE, transaction_date);
                            statement.put(TAG_DEBIT, debt);
                            statement.put(TAG_DESCRIPTION, description);
                            statement.put(TAG_BALANCE, balance);
//                            balance.put(TAG_PERIOD, period);


                            statementList.add(statement);
                        }
                    }

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
                    getActivity(), statementList,
                    R.layout.statement_list, new String[] { TAG_TRANSACTIONDATE, TAG_DESCRIPTION,
                    TAG_DEBIT, TAG_BALANCE },
                    new int[] { R.id.list_transaction_date, R.id.list_description, R.id.list_debit,
                            R.id.list_balance } );

            setListAdapter(adapter);
//            swipeRefreshLayout.setRefreshing(false);

        }
    }
}
