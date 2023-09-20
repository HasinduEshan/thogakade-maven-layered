package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.DaoFactory;
import dao.custom.CustomerDao;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import entity.Customer;
import dto.tm.CustomerTm;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class CustomerFormController implements Initializable {
    public Label lblCustId;
    @FXML
    private TreeTableColumn colAddress;

    @FXML
    private TreeTableColumn colCustId;

    @FXML
    private TreeTableColumn colCustName;

    @FXML
    private TreeTableColumn colSalary;

    @FXML
    private TreeTableColumn colOption;

    @FXML
    private AnchorPane customerPane;

    @FXML
    private JFXTreeTableView<CustomerTm> tblCustomer;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtSalary;

    @FXML
    private JFXTextField txtSearch;

    CustomerDao customerDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.CUSTOMER);

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) customerPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBoard.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }


    @FXML
    void clearButtonOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        generateId();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
        txtSearch.clear();
        tblCustomer.refresh();
    }

    @FXML
    void saveButtonOnAction(ActionEvent event) {
        Customer customer = new Customer(
                lblCustId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );
        try {
            boolean isSaved = customerDao.save(customer);

            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION,"Customer Saved..!").show();
                loadTable();
                clearFields();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong..!").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateButtonOnAction(ActionEvent event) {
        Customer customer = new Customer(
                lblCustId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );
        try {

            boolean isUpdate = customerDao.update(customer);

            if (isUpdate){
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated..!").show();
                clearFields();
                loadTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong..!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCustId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colCustName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new TreeItemPropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new TreeItemPropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        generateId();
        loadTable();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->{
            if (newValue!=null){
                setData(newValue);
            }
        });

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                tblCustomer.setPredicate(new Predicate<TreeItem<CustomerTm>>() {
                    @Override
                    public boolean test(TreeItem<CustomerTm> customerTmTreeItem) {
                        boolean flag = customerTmTreeItem.getValue().getId().contains(newValue) ||
                                customerTmTreeItem.getValue().getName().contains(newValue);
                        return flag;
                    }
                });
            }
        });
    }

    private void setData(TreeItem<CustomerTm> value) {
        lblCustId.setText(value.getValue().getId());
        txtName.setText(value.getValue().getName());
        txtAddress.setText(value.getValue().getAddress());
        txtSalary.setText(String.valueOf(value.getValue().getSalary()));
    }

    private void loadTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        try {
            List<Customer> list = customerDao.findAll();

            for (Customer customer:list) {
                JFXButton btn = new JFXButton("Delete");
                btn.setBackground(Background.fill(Color.rgb(227,92,92)));
                btn.setTextFill(Color.rgb(255,255,255));
                btn.setStyle("-fx-font-weight: BOLD");

                btn.setOnAction(actionEvent -> {
                    try {
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete " + customer.getId() + " customer ? ", ButtonType.YES, ButtonType.NO).showAndWait();
                        if (buttonType.get() == ButtonType.YES){

                            boolean isDelete = customerDao.delete(customer.getId());

                            if (isDelete){
                                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted..!").show();
                                loadTable();
                                generateId();
                            }else{
                                new Alert(Alert.AlertType.ERROR,"Something went wrong..!").show();
                            }
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

                tmList.add(new CustomerTm(
                        customer.getId(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getSalary(),
                        btn
                ));
            }

            TreeItem<CustomerTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblCustomer.setRoot(treeItem);
            tblCustomer.setShowRoot(false);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generateId() {
        try {
            String id = customerDao.findLastId();

            if (id!=null){
                int num = Integer.parseInt(id.split("[C]")[1]);
                num++;
                lblCustId.setText(String.format("C%03d",num));
            }else {
                lblCustId.setText("C001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
