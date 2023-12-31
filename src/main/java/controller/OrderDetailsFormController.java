package controller;

import bo.BoFactory;
import bo.custom.OrderDetailsBo;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import dto.tm.OrderDetailsTm;
import dto.tm.OrderTm;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderDetailsFormController {

    public JFXTreeTableView<OrderTm> tblOrder;
    public TreeTableColumn colOrderId;
    public TreeTableColumn colDate;
    public TreeTableColumn colCustName;
    public TreeTableColumn colOption;
    public AnchorPane orderDetailsPane;

    public JFXTreeTableView<OrderDetailsTm> tblDetails;
    public TreeTableColumn colItemCode;
    public TreeTableColumn colDesc;
    public TreeTableColumn colQty;
    public TreeTableColumn colAmount;

    OrderDetailsBo orderDetailsBo = BoFactory.getInstance().getBoType(BoFactory.BoType.ORDER_DETAILS);

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) orderDetailsPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    public void initialize(){
        colOrderId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        colCustName.setCellValueFactory(new TreeItemPropertyValueFactory<>("custName"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        colItemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("itemCode"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));

        loadOrders();

        tblOrder.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->{
            if (newValue!=null){
                loadDetails(newValue);
            }
        });
    }

    private void loadDetails(TreeItem<OrderTm> newValue) {
        ObservableList<OrderDetailsTm> tmList = FXCollections.observableArrayList();
        try {
            List<OrderDetailsDto> list = orderDetailsBo.findOrderDetailByOrderId(newValue.getValue().getId());

            for (OrderDetailsDto detail:list) {

                ItemDto item = orderDetailsBo.findItem(detail.getItemCode());

                tmList.add(new OrderDetailsTm(
                        detail.getItemCode(),
                        item.getDescription(),
                        detail.getQty(),
                        detail.getUnitPrice()*detail.getQty()
                ));
            }

            TreeItem<OrderDetailsTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblDetails.setRoot(treeItem);
            tblDetails.setShowRoot(false);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadOrders() {
        ObservableList<OrderTm> tmList = FXCollections.observableArrayList();
        try {
            List<OrderDto> list = orderDetailsBo.findAllOrders();

            for (OrderDto order:list) {
                JFXButton btn = new JFXButton("Delete");
                btn.setBackground(Background.fill(Color.rgb(227,92,92)));
                btn.setTextFill(Color.rgb(255,255,255));
                btn.setStyle("-fx-font-weight: BOLD");


                btn.setOnAction(actionEvent -> {
                    try {
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete " + order.getId() + " order ? ", ButtonType.YES, ButtonType.NO).showAndWait();
                        if (buttonType.get() == ButtonType.YES){

                            boolean isDeleted = orderDetailsBo.deleteOrder(order.getId());;

                            if (isDeleted){
                                new Alert(Alert.AlertType.INFORMATION,"Order Deleted..!").show();
                                loadOrders();
                            }else{
                                new Alert(Alert.AlertType.ERROR,"Something went wrong..!").show();
                            }
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

                CustomerDto customer = orderDetailsBo.findCustomer(order.getCustomerId());

                tmList.add(new OrderTm(
                        order.getId(),
                        order.getDate(),
                        customer.getName(),
                        btn
                ));
            }

            TreeItem<OrderTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeItem);
            tblOrder.setShowRoot(false);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
