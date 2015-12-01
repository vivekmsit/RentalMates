package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog fragment that allows user to select an item from a list
 */
public class ItemPickerDialogFragment extends DialogFragment {
    public static final String LOGTAG = "ItemPickerDFragment";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_ITEMS = "ARG_ITEMS";
    private static final String ARG_SELECTED_INDEX = "ARG_SELECTED_INDEX";
    private static final String ARG_NEUTRAL_BUTTON_TITLE = "ARG_NEUTRAL_BUTTON_TITLE";

    private String title;
    private String neutralButtonName;
    private ArrayList<Item> items;
    private int selectedIndex;
    private OnDialogResultListener listener;

    public interface OnDialogResultListener {
        void onPositiveResult(Long flatId);

        void onNeutralButtonResult();

        void onNegativeResult();
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.listener = listener;
    }

    /**
     * Create a new instance of ItemPickerDialogFragment with specified arguments
     *
     * @param title         Dialog title text
     * @param items         Selectable items
     * @param selectedIndex initial selection index, or -1 if no item should be pre-selected
     * @return ItemPickerDialogFragment
     */
    public static ItemPickerDialogFragment newInstance(String title, String neutralButtonName, ArrayList<Item> items, int selectedIndex) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putBundle(ARG_ITEMS, Item.bundleOfItems(items));
        args.putInt(ARG_SELECTED_INDEX, selectedIndex);
        args.putString(ARG_NEUTRAL_BUTTON_TITLE, neutralButtonName);

        ItemPickerDialogFragment fragment = new ItemPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_SELECTED_INDEX, selectedIndex);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARG_TITLE, "Dialog");
            neutralButtonName = args.getString(ARG_NEUTRAL_BUTTON_TITLE, "TITLE");
            items = Item.itemsFromBundle(args.getBundle(ARG_ITEMS));
            selectedIndex = args.getInt(ARG_SELECTED_INDEX, -1);
        }

        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(ARG_SELECTED_INDEX, selectedIndex);
        }

        String[] itemTitles = getItemTitlesArray();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            if (0 <= selectedIndex && selectedIndex < items.size()) {
                                Item item = items.get(selectedIndex);
                                listener.onPositiveResult(item.getId());
                            }
                        }
                    }
                })
                .setNeutralButton(neutralButtonName, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (listener != null) {
                                    listener.onNeutralButtonResult();
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(LOGTAG, "Cancel button clicked");
                            }
                        }
                )
                .setSingleChoiceItems(itemTitles, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(LOGTAG, "User clicked item with index " + which);
                        selectedIndex = which;
                    }
                });

        return builder.create();
    }

    private String[] getItemTitlesArray() {
        final int itemCount = items.size();
        String[] itemTitles = new String[itemCount];
        for (int i = 0; i < itemCount; ++i) {
            itemTitles[i] = items.get(i).getTitle();
        }
        return itemTitles;
    }


    /**
     * An item that can be displayed and selected by the ItemPickerDialogFragment
     */
    public static class Item {
        private String title;
        private int intValue;
        private Long id;

        private static final String KEY_TITLE = "title";
        private static final String KEY_INT_VALUE = "intValue";
        private static final String KEY_ID = "id";

        /**
         * Construct with title and integer value
         *
         * @param title Name displayed in list
         * @param value Integer value associated with item
         */
        public Item(String title, int value) {
            assert (!TextUtils.isEmpty(title));

            this.title = title;
            this.intValue = value;
        }

        /**
         * Construct with title and string value
         *
         * @param title Name displayed in list
         * @param id    Long value associated with item
         */
        public Item(String title, Long id) {
            assert (!TextUtils.isEmpty(title));

            this.title = title;
            this.id = id;
        }

        /**
         * Construct from a bundle of values
         *
         * @param bundle
         */
        public Item(Bundle bundle) {
            title = bundle.getString(KEY_TITLE, null);
            intValue = bundle.getInt(KEY_INT_VALUE, 0);
            id = bundle.getLong(KEY_ID, 0);
        }

        /**
         * Get a Bundle of values that can be passed to the Item(Bundle) constructor
         * to re-create the object
         *
         * @return Bundle
         */
        public Bundle getValuesBundle() {
            Bundle bundle = new Bundle();

            bundle.putString(KEY_TITLE, title);
            bundle.putInt(KEY_INT_VALUE, intValue);
            if (id != 0) {
                bundle.putLong(KEY_ID, id);
            }

            return bundle;
        }

        public String getTitle() {
            return title;
        }

        public int getIntValue() {
            return intValue;
        }

        public Long getId() {
            return id;
        }

        /**
         * Given a list of items, create a Bundle that can be passed to
         * Item.itemsFromBundle() to recreate them.
         *
         * @param items list of items
         * @return Bundle
         */
        public static Bundle bundleOfItems(List<Item> items) {
            int itemCount = items.size();
            ArrayList<Bundle> itemBundles = new ArrayList<>();
            for (int i = 0; i < itemCount; ++i) {
                itemBundles.add(items.get(i).getValuesBundle());
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ARG_ITEMS, itemBundles);
            return bundle;
        }

        /**
         * Given a Bundle created by Item.bundleOfItems(), recreate the
         * original list of items.
         *
         * @param bundle Bundle created by Item.bundleOfItems()
         * @return ArrayList&lt;Item&gt;
         */
        public static ArrayList<Item> itemsFromBundle(Bundle bundle) {
            ArrayList<Bundle> itemBundles = bundle.getParcelableArrayList(ARG_ITEMS);
            ArrayList<Item> items = new ArrayList<>();
            if (itemBundles != null) {
                for (Bundle itemBundle : itemBundles) {
                    items.add(new Item(itemBundle));
                }
            }
            return items;
        }
    }
}