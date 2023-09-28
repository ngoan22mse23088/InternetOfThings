package code.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.material.components.R;

import java.util.List;

import code.model.MnType;
import code.room.AppDatabase;
import code.room.DAO;
import code.room.table.FavoriteEntity;

/**
 * Class handle menu at MainMenu.java or First Page
 */
public class MnAdapter extends ExpandableRecyclerAdapter<MnAdapter.Item> {

    private Context context;
    private DAO dao;
    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MnAdapter(Context context, List<Item> items, OnItemClickListener onItemClickListener) {
        super(context);
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        dao = AppDatabase.getDb(context).getDAO();
        setItems(items);

    }

    public static class Item extends ExpandableRecyclerAdapter.ListItem {
        public int Id = -1;
        public int Icon = -1;
        public String Text;
        public boolean New = false;
        public Class<?> Act = null;
        public String Parent;
        public String Id_str = Parent + " | " + Text;

        // divider
        public Item(int id, String title, MnType type) {
            super(type.getValue());
            Id = id;
            Text = title;
            Text = null;
            setIdStr();
        }

        // head old
        public Item(int id, String title, int icon, MnType type) {
            super(type.getValue());
            Id = id;
            Text = title;
            Icon = icon;
            setIdStr();
        }

        // head new
        public Item(int id, String title, int icon, boolean isNew, MnType type) {
            super(type.getValue());
            Id = id;
            Text = title;
            Icon = icon;
            New = isNew;
            setIdStr();
        }

        // item old
        public Item(int id, String p, String title, MnType type, Class<?> act) {
            super(type.getValue());
            Id = id;
            Text = title;
            Act = act;
            Parent = p;
            setIdStr();
        }

        // item new
        public Item(int id, String p, String title, boolean isNew, MnType type, Class<?> act) {
            super(type.getValue());
            Id = id;
            Text = title;
            New = isNew;
            Act = act;
            Parent = p;
            setIdStr();
        }

        private void setIdStr() {
            this.Id_str = Parent + " | " + Text;
        }
    }

    /**
     * ClassView for menu with icon and text
     */
    public class PlainViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        View view;
        TextView name;
        ImageView icon;

        public PlainViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.item_menu_group_name);
            icon = view.findViewById(R.id.item_menu_group_image);
        }

        public void bind(final int position) {
            name.setText(visibleItems.get(position).Text);
            icon.setImageResource(visibleItems.get(position).Icon);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position >= visibleItems.size()) return;
                    onItemClickListener.onItemClick(view, visibleItems.get(position));
                }
            });
        }
    }

    /**
     * ClassView for menu with icon, text and expandable feature
     */
    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name;
        ImageView icon;
        View badge;

        public HeaderViewHolder(View view) {
            super(view, view.findViewById(R.id.item_arrow));
            name = view.findViewById(R.id.item_menu_group_name);
            icon = view.findViewById(R.id.item_menu_group_image);
            badge = view.findViewById(R.id.item_menu_group_badge);
        }

        public void bind(final int position) {
            super.bind(position);
            name.setText(visibleItems.get(position).Text);
            icon.setImageResource(visibleItems.get(position).Icon);
            badge.setVisibility(visibleItems.get(position).New ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * ClassView for menu with text and sub header from HeaderViewHolder
     */
    public class SubHeaderViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        View view;
        TextView name;
        View badge;
        ImageView favorite;
        View lyt_parent;

        public SubHeaderViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.item_menu_sub_group_name);
            badge = view.findViewById(R.id.item_menu_sub_group_badge);
            favorite = view.findViewById(R.id.favorite);
            lyt_parent = view.findViewById(R.id.lyt_parent);
        }

        public void bind(final int position) {
            Item item = visibleItems.get(position);
            name.setText(visibleItems.get(position).Text);
            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position >= visibleItems.size()) return;
                    onItemClickListener.onItemClick(view, visibleItems.get(position));
                }
            });
            favorite.setImageResource(dao.isFavorite(item.Id_str) > 0 ? R.drawable.ic_favorites : R.drawable.ic_favorite_border);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dao.isFavorite(item.Id_str) > 0) {
                        dao.deleteFavorite(item.Id_str);
                    } else {
                        FavoriteEntity fav = new FavoriteEntity(item.Id, item.Parent, item.Text, System.currentTimeMillis());
                        dao.insertFavorite(fav);
                    }
                    notifyItemChanged(position);
                }
            });
            badge.setVisibility(item.New ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * ClassView for menu divider line
     */
    public class DividerViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        TextView name;

        public DividerViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_menu_divider_name);
        }

        public void bind(int position) {
            if (visibleItems.get(position).Text == null) {
                name.setVisibility(View.GONE);
            } else {
                name.setVisibility(View.VISIBLE);
                name.setText(visibleItems.get(position).Text);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View divider = new View(context);
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(context.getResources().getColor(R.color.grey_60));
        if (viewType == NORMAL) {
            return new PlainViewHolder(inflate(R.layout.item_menu_plain, parent));
        } else if (viewType == HEADER) {
            return new HeaderViewHolder(inflate(R.layout.item_menu_group, parent));
        } else if (viewType == SUB_HEADER) {
            return new SubHeaderViewHolder(inflate(R.layout.item_menu_sub_group, parent));
        } else if (viewType == DIVIDER) {
            return new DividerViewHolder(inflate(R.layout.item_menu_divider, parent));
        } else {
            return new PlainViewHolder(inflate(R.layout.item_menu_group, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == NORMAL) {
            ((PlainViewHolder) holder).bind(position);
        } else if (viewType == HEADER) {
            ((HeaderViewHolder) holder).bind(position);
        } else if (viewType == SUB_HEADER) {
            ((SubHeaderViewHolder) holder).bind(position);
        } else if (viewType == DIVIDER) {
            ((DividerViewHolder) holder).bind(position);
        } else {
            ((SubHeaderViewHolder) holder).bind(position);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, Item item);
    }
}
