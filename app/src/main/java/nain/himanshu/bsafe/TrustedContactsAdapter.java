package nain.himanshu.bsafe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nain.himanshu.bsafe.Database.Contacts;

public class TrustedContactsAdapter extends RecyclerView.Adapter<TrustedContactsAdapter.ViewHolder> {

    private Context mContext;
    private List<Contacts> mList;

    public TrustedContactsAdapter(Context mContext, List<Contacts> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.contact_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Contacts data = mList.get(position);
        holder.mName.setText(data.getName());
        holder.mNumber.setText(data.getNumber());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mName, mNumber;

        public ViewHolder(@NonNull View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mNumber = view.findViewById(R.id.number);
        }
    }
}
