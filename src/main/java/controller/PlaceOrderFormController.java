package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.DaoFactory;
import dao.custom.CustomerDao;
import dao.custom.ItemDao;
import dao.custom.OrderDao;
import dao.custom.OrderDetailsDao;
import db.DBConnection;
import entity.Customer;
import entity.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import entity.Order;
import entity.OrderDetails;
import dto.tm.CartTm;
import util.CrudUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlaceOrderFormController implements Initializable {
    @FXML
    private JFXComboBox cmbCustomerId;

    @FXML
    private JFXComboBox cmbItemCode;

    @FXML
    private TreeTableColumn colAmount;

    @FXML
    private TreeTableColumn colDescription;

    @FXML
    private TreeTableColumn colItemCode;

    @FXML
    private TreeTableColumn colOption;

    @FXML
    private TreeTableColumn colQty;

    @FXML
    private TreeTableColumn colUnitPrice;

    @FXML
    private Label lblOrderId;

    @FXML
    private Label lblQtyOnHand;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblUnitPrice;

    @FXML
    private AnchorPane placeOrderPane;

    @FXML
    private JFXTreeTableView<CartTm> tblOrder;

    @FXML
    private JFXTextField txtCustomerName;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTextField txtSearch;

    OrderDao orderDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ORDERS);
    ItemDao itemDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ITEM);
    CustomerDao customerDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.CUSTOMER);
    OrderDetailsDao orderDetailsDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ORDER_DETAILS);

    ObservableList<CartTm> tmList = FXCollections.observableArrayList();

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) placeOrderPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    private double findTotal(){
        double total = 0;
        for (CartTm tm:tmList) {
            total += tm.getAmount();
        }
        return total;
    }

    @FXML
    void addToCartButtonOnAction(ActionEvent event) {
        boolean isExist = false;
        for (CartTm tm:tmList) {
            if (tm.getCode().equals(cmbItemCode.getValue().toString())){
                tm.setQty(tm.getQty()+Integer.parseInt(txtQty.getText()));
                tm.setAmount(tm.getQty()*tm.getAmount());
                isExist = true;
            }
        }

        if (!isExist){
            JFXButton btn = new JFXButton("Delete");
            btn.setBackground(Background.fill(Color.rgb(227,92,92)));
            btn.setTextFill(Color.rgb(255,255,255));
            btn.setStyle("-fx-font-weight: BOLD");



            CartTm cartTm = new CartTm(
                    cmbItemCode.getValue().toString(),
                    txtDescription.getText(),
                    Double.parseDouble(lblUnitPrice.getText()),
                    Integer.parseInt(txtQty.getText()),
                    Double.parseDouble(lblUnitPrice.getText())*Integer.parseInt(txtQty.getText()),
                    btn
            );

            btn.setOnAction(actionEvent -> {
                tmList.remove(cartTm);
                lblTotal.setText(String.format("%.2f",findTotal()));
                tblOrder.refresh();
            });

            tmList.add(cartTm);

            TreeItem<CartTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeItem);
            tblOrder.setShowRoot(false);
        }
        lblTotal.setText(String.format("%.2f",findTotal()));
        tblOrder.refresh();
    }

    @FXML
    void clearButtonOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        generateId();
        cmbCustomerId.setValue("");
        cmbItemCode.setValue("");
        txtCustomerName.clear();
        txtSearch.clear();
        txtDescription.clear();
        lblUnitPrice.setText("0.00");
        lblQtyOnHand.setText("");
        txtQty.clear();

    }

    @FXML
    void placeOrderButtonOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        List<OrderDetails> detailsList = new ArrayList<>();

        for (CartTm tm:tmList) {
            detailsList.add(new OrderDetails(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getUnitPrice()
            ));
        }

        Order order = new Order(
                lblOrderId.getText(),
                LocalDate.now().toString(),
                cmbCustomerId.getValue().toString()
        );
        try {
            DBConnection.getInstance().getConnection().setAutoCommit(false);

            boolean orderPlaced = orderDao.save(order);

            boolean isOrderPlaced = true;
            if (orderPlaced) {
                for (OrderDetails detail:detailsList) {

                    boolean detailSaved = orderDetailsDao.save(detail);

                    if (!detailSaved){
                        isOrderPlaced = false;
                    }

                }
            }else{
                isOrderPlaced = false;
                new Alert(Alert.AlertType.ERROR,"Something went wrong..!").show();
                DBConnection.getInstance().getConnection().rollback();
            }

            if (isOrderPlaced){
                new Alert(Alert.AlertType.INFORMATION,"Order Placed..!").show();
                DBConnection.getInstance().getConnection().commit();
                tmList.clear();
                tblOrder.refresh();
                clearFields();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong..!").show();
                DBConnection.getInstance().getConnection().rollback();
            }
        } catch (SQLException | ClassNotFoundException e) {
            DBConnection.getInstance().getConnection().rollback();
            e.printStackTrace();
        } finally {
            DBConnection.getInstance().getConnection().setAutoCommit(true);
        }
    }


    @FXML
    void updateButtonOnAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colItemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        generateId();
        loadCustomerId();
        loadItemCodes();

        cmbCustomerId.setOnAction(actionEvent -> {
            setCustomerName();
        });

        cmbItemCode.setOnAction(actionEvent -> {
            setItemDetails();
        });
    }

    private void setItemDetails() {
        try {

            Item item = itemDao.find(cmbItemCode.getValue().toString());

            if (item!=null){
                txtDescription.setText(item.getDescription());
                lblUnitPrice.setText(String.format("%.2f",item.getUnitPrice()));
                lblQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setCustomerName() {
        try {

            Customer customer = customerDao.find(cmbCustomerId.getValue().toString());

            if (customer!=null){
                txtCustomerName.setText(customer.getName());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadItemCodes() {
        try {

            List<Item> items = itemDao.findAll();

            ObservableList<String> itemCodes = FXCollections.observableArrayList();

            for (Item item:items) {
                itemCodes.add(item.getCode());
            }

            cmbItemCode.setItems(itemCodes);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerId() {

        try {

            ObservableList<String> customerIds = FXCollections.observableArrayList();
            List<Customer> customers = customerDao.findAll();

            for (Customer customer:customers) {
                customerIds.add(customer.getId());
            }

            cmbCustomerId.setItems(customerIds);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void generateId() {
        try {

            String id = orderDao.findLastId();

            if (id!=null){
                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                lblOrderId.setText(String.format("D%03d",num));
            }else {
                lblOrderId.setText("D001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
