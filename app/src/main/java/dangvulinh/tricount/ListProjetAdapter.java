package dangvulinh.tricount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class ListProjetAdapter extends BaseAdapter {

    private MainActivity context;
    private int layout;
    private List<ListProjet> projet_list;

    public ListProjetAdapter(MainActivity context, int layout, List<ListProjet> projet_list) {
        this.context = context;
        this.layout = layout;
        this.projet_list = projet_list;
    }

    @Override
    public int getCount() {
        return projet_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtProjet;
        TextView txtDescription;
        TextView txtDate;
        ImageView imgInto;
        ImageView imgChange;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);

            holder.txtProjet = (TextView) convertView.findViewById(R.id.tvProjet);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            holder.txtDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.imgInto = (ImageView) convertView.findViewById(R.id.imgInto);
            holder.imgChange = (ImageView) convertView.findViewById(R.id.ImgChange);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ListProjet lp = projet_list.get(position);

        holder.txtProjet.setText(lp.getProjet());
        holder.txtDescription.setText(lp.getDescription());
        holder.txtDate.setText(lp.getDate());
        holder.imgInto.setImageResource(R.drawable.getin);
        holder.imgChange.setImageResource(R.drawable.modify);

        //get event go into projet
        holder.imgInto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.gotoResult(lp.getId());
            }
        });

        //get event change projet
        holder.imgChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.dialogmodify(lp.getId());
            }
        });

        return convertView;
    }
}
