package nain.himanshu.bsafe;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nain.himanshu.bsafe.Database.AppDatabase;
import nain.himanshu.bsafe.Database.Contacts;

public class TrustedContactsActivity extends AppCompatActivity {

    private static final int CONTACT_PICK_REQUEST = 76;
    private FloatingActionButton mAddContact;

    private RecyclerView mRecycler;
    private TrustedContactsAdapter mAdapter;
    private List<Contacts> mContactsList;

    private LinearLayout mNoContent;

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();
            try{

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        AppDatabase.getInstance(getApplicationContext()).contactsDao().delete(mContactsList.get(position));

                    }
                });
                thread.setName("DELETE_TRUSTED_CONTACT_THREAD");
                thread.start();
                thread.join();
                mContactsList.remove(position);
                toggleNoContent();
                mAdapter.notifyDataSetChanged();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        mAddContact = findViewById(R.id.addContact);
        mAddContact.setOnClickListener(onAddContactClickListener);
        mNoContent = findViewById(R.id.noContentLayout);

        mContactsList = new ArrayList<>();
        mAdapter = new TrustedContactsAdapter(this, mContactsList);
        mRecycler = findViewById(R.id.trustedContactsList);
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecycler.setAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(mRecycler);

        LOAD_DATA();
    }

    private void LOAD_DATA() {
        try{
            mContactsList.clear();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    mContactsList.addAll(AppDatabase.getInstance(getApplicationContext()).contactsDao().getAll());

                }
            });
            thread.setName("GET_TRUSTED_CONTACTS_THREAD");
            thread.start();
            thread.join();
            toggleNoContent();
            mAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void toggleNoContent(){

        if(mContactsList.size() == 0){
            mRecycler.setVisibility(View.GONE);
            mNoContent.setVisibility(View.VISIBLE);
        }else{
            mRecycler.setVisibility(View.VISIBLE);
            mNoContent.setVisibility(View.GONE);
        }

    }

    private View.OnClickListener onAddContactClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, CONTACT_PICK_REQUEST);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trusted_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                    super.onBackPressed();
                    //NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.ac_clear:

                try{
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase.getInstance(getApplicationContext()).contactsDao().deleteAll();
                        }
                    });
                    thread.setName("CLEAR_TRUSTED_CONTACTS_THREAD");
                    thread.start();
                    thread.join();
                    mContactsList.clear();
                    toggleNoContent();
                    Toast.makeText(this, "List cleared successfully", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == CONTACT_PICK_REQUEST && resultCode == RESULT_OK){

            try{

                assert data != null;
                Uri contactUri = data.getData();
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

                assert contactUri != null;
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIndex);
                    String name = cursor.getString(nameIndex);

                    addToDatabase(name, number);
                }

                assert cursor != null;
                cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void addToDatabase(String cName, String cNumber) {


        try{

            final Contacts contact = new Contacts();
            contact.setCid(System.currentTimeMillis());
            contact.setName(cName);
            contact.setNumber(cNumber.trim());
            contact.setPriority(1.0f);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase.getInstance(getApplicationContext()).contactsDao().insert(contact);
                }
            });
            thread.setName("ADD_TRUSTED_CONTACT_THREAD");
            thread.start();
            thread.join();
            mContactsList.add(contact);
            toggleNoContent();
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Contact Added Successfully", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
