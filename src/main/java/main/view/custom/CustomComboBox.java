package main.view.custom;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.ArrayList;

public class CustomComboBox<T> extends ComboBox<T> {

    private ArrayList<T> disabledItems = new ArrayList<T>();

    public CustomComboBox() {
        super();
        setup();
    }

    public CustomComboBox(ObservableList<T> list) {
        super(list);
        setup();
    }

    private void setup() {

        SingleSelectionModel<T> model = new SingleSelectionModel<T>() {

            @Override
            public void select(T item) {

                if (disabledItems.contains(item)) {
                    return;
                }

                super.select(item);
            }

            @Override
            public void select(int index) {
                T item = getItems().get(index);

                if (disabledItems.contains(item)) {
                    return;
                }

                super.select(index);
            }

            @Override
            protected int getItemCount() {
                return getItems().size();
            }

            @Override
            protected T getModelItem(int index) {
                return getItems().get(index);
            }

        };

        Callback<ListView<T>, ListCell<T>> callback = new Callback<ListView<T>, ListCell<T>>() {
            public ListCell<T> call(ListView<T> param) {
                final ListCell<T> cell = new ListCell<T>() {
                    @Override
                    public void updateItem(T item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item != null) {

                            setText(item.toString());

                            if (disabledItems.contains(item)) {
                                setTextFill(Color.LIGHTGRAY);
                                setDisable(true);
                            }

                        } else {

                            setText(null);

                        }
                    }
                };

                return cell;
            }

        };

        setSelectionModel(model);
        setCellFactory(callback);

    }

    public void setDisabled(T item, boolean disable) {
        if(!disable && disabledItems.contains(item))
            disabledItems.remove(item);
        else if(disable)
            disabledItems.add(item);
    }



}
