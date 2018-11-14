package controller;

import entity.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import models.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerController implements Initializable {

    //client
    @FXML
    private TableColumn clientIdColumn;
    @FXML
    private TextField clientName;
    @FXML
    private TableColumn clientNameColumn;
    @FXML
    private TextField clientSurname;
    @FXML
    private TableColumn clientSurnameColumn;
    @FXML
    private TextField clientPhone;
    @FXML
    private TableColumn clientPhoneColumn;
    @FXML
    private TextField clientPhone2;
    @FXML
    private TableColumn clientPhone2Column;
    @FXML
    private TextField clientEmail;
    @FXML
    private TableColumn clientEmailColumn;
    @FXML
    private Button clientAddButton;
    @FXML
    private Button clientRemoveButton;
    @FXML
    private Button clientChangeButton;
    @FXML
    private TextField clientAdress;
    @FXML
    private TableColumn clientAdressColumn;
    @FXML
    private TableView clientTable;

    //request
    @FXML
    private TableColumn requestIdColumn;
    @FXML
    private TableColumn requestClientIdColumn;
    @FXML
    private TableColumn requestColumn;
    @FXML
    private TableColumn requestCheckedColumn;
    @FXML
    private TableColumn requestApprovedColumn;
    @FXML
    private TableColumn requestProductColumn;
    @FXML
    private TableColumn requestCountColumn;
    @FXML
    private TableView requestTable;
    @FXML
    private TableView requestProductTable;

    //order
    @FXML
    private TableColumn orderIdColumn;
    @FXML
    private TableColumn orderClientColumn;
    @FXML
    private TableColumn orderRequestColumn;
    @FXML
    private TableColumn orderContractColumn;
    @FXML
    private TableColumn orderPaymentColumn;
    @FXML
    private TableColumn orderProductColumn;
    @FXML
    private TableColumn orderCountColumn;
    @FXML
    private TableColumn orderRestColumn;
    @FXML
    private TableView orderTable;
    @FXML
    private TableView orderProductTable;

    //contract
    @FXML
    private TableColumn contractNameColumn;
    @FXML
    private TableColumn contractClientNameColumn;
    @FXML
    private TableColumn contractBeginDateColumn;
    @FXML
    private TableColumn contractEndDateColumn;
    @FXML
    private TableColumn contractPaymentColumn;
    @FXML
    private TableView contractTable;

    //invoice
    @FXML
    private TableColumn invoiceIdColumn;
    @FXML
    private TableColumn invoiceContractColumn;
    @FXML
    private TableColumn invoiceDateCreateColumn;
    @FXML
    private TableColumn invoiceAgreedColumn;
    @FXML
    private TableColumn invoiceProductNameColumn;
    @FXML
    private TableColumn invoiceProductCountColumn;
    @FXML
    private TableColumn invoiceProductLoadedColumn;
    @FXML
    private TableView invoiceTable;
    @FXML
    private TableView invoiceProductTable;

    private String login;
    private final Session session = HibernateUtil.getSessionFactory();

    public ManagerController()  {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setFactories();
        this.setAllClients();
        this.setAllRequests();
        this.setAllOrders();
        this.setAllInvoices();
    }

    private void setAllClients(){
        ObservableList<Client> usersList = FXCollections.observableArrayList();
        this.setFactories();
        Query query1 = session.createQuery(" FROM ClientEntity ");
        Iterator iter = query1.list().iterator();
        while(iter.hasNext()){
            ClientEntity clientEntity = (ClientEntity) iter.next();
            usersList.add(new Client(clientEntity));
        }
        clientTable.setItems(usersList);
    }
    private void setAllRequests(){
        ObservableList<Request> requestsList = FXCollections.observableArrayList();

        Query query = session.createQuery(" FROM ClientRequestEntity ");
        Iterator iter = query.list().iterator();
        while(iter.hasNext()){
            ClientRequestEntity requestEntity = (ClientRequestEntity) iter.next();

            Query query2 = session.createQuery(" FROM RequestProductEntity WHERE clientRequestByRequestId.id = "+requestEntity.getId());
            Iterator iter2 = query2.list().iterator();
            ObservableList<RequestProduct> products = FXCollections.observableArrayList();
            while(iter2.hasNext()){
                RequestProductEntity requestProduct =(RequestProductEntity) iter2.next();
                Product product = new Product(requestProduct.getProductByProductId());
                products.add(new RequestProduct(requestProduct,product.getName()));
            }
            Request request = new Request(requestEntity);
            request.setRequestsProducts(products);
            requestsList.add(request);
        }
        requestTable.setItems(requestsList);
    }
    private void setAllOrders(){
        ObservableList<Order> ordersList = FXCollections.observableArrayList();

        Query query = session.createQuery(" FROM ClientOrderEntity ");
        Iterator iter = query.list().iterator();
        while(iter.hasNext()){
            ClientOrderEntity orderEntity = (ClientOrderEntity) iter.next();

            Query query2 = session.createQuery(" FROM OrderProductEntity WHERE clientOrderByOrderId.id = "+orderEntity.getId());
            Iterator iter2 = query2.list().iterator();
            ObservableList<OrderProduct> products = FXCollections.observableArrayList();
            while(iter2.hasNext()){
                OrderProductEntity orderProduct =(OrderProductEntity) iter2.next();
                ProductEntity product = orderProduct.getProductByProductId();
                products.add(new OrderProduct(orderProduct,product.getName()));
            }
            Order order = new Order(orderEntity);
            order.setOrdersProducts(products);
            ordersList.add(order);
        }
        orderTable.setItems(ordersList);
        contractTable.setItems(ordersList);
    }
    private void setAllInvoices(){
        ObservableList<Invoice> invoicesList = FXCollections.observableArrayList();

        Query query = session.createQuery(" FROM InvoiceEntity ");
        Iterator iter = query.list().iterator();
        while(iter.hasNext()){
            InvoiceEntity invoiceEntity = (InvoiceEntity) iter.next();

            Query query2 = session.createQuery(" FROM InvoiceProductEntity WHERE invoiceByInvoiceId.id = "+invoiceEntity.getId());
            Iterator iter2 = query2.list().iterator();
            ObservableList<InvoiceProduct> products = FXCollections.observableArrayList();
            while(iter2.hasNext()){
                InvoiceProductEntity invoiceProduct =(InvoiceProductEntity) iter2.next();
                ProductEntity product = invoiceProduct.getProductByProductId();
                products.add(new InvoiceProduct(invoiceProduct,product.getName()));
            }
            Invoice invoice = new Invoice(invoiceEntity);
            invoice.setInvoiceProducts(products);
            invoicesList.add(invoice);
        }
        invoiceTable.setItems(invoicesList);
    }

    public void onRequestTableClick(){
        Request selectedRequest = (Request) requestTable.getSelectionModel().getSelectedItem();
        ObservableList<RequestProduct> products = selectedRequest.getRequestsProducts();
        if(!products.isEmpty()){requestProductTable.setItems(products);}
        else{requestProductTable.setItems(null);}
    }
    public void onOrderTableClick(){
        Order selectedRequest = (Order) orderTable.getSelectionModel().getSelectedItem();
        ObservableList<OrderProduct> products = selectedRequest.getOrdersProducts();
        if(!products.isEmpty()){orderProductTable.setItems(products);}
        else{orderProductTable.setItems(null);}
    }
    public void onInvoiceTableClick(){
        Invoice selectedRequest = (Invoice) invoiceTable.getSelectionModel().getSelectedItem();
        ObservableList<InvoiceProduct> products = selectedRequest.getInvoiceProducts();
        if(!products.isEmpty()){invoiceProductTable.setItems(products);}
        else{invoiceProductTable.setItems(null);}
    }

    private void setFactories() {
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<Client,String>("id"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<Client,String>("name"));
        clientSurnameColumn.setCellValueFactory(new PropertyValueFactory<Client,String>("surname"));
        clientPhoneColumn.setCellValueFactory(new PropertyValueFactory<Client,String>("phone"));
        clientPhone2Column.setCellValueFactory(new PropertyValueFactory<Client,String>("phone2"));
        clientAdressColumn.setCellValueFactory(new PropertyValueFactory<Client,String>("adress"));
        clientEmailColumn.setCellValueFactory(new PropertyValueFactory<Client,String>("email"));

        requestIdColumn.setCellValueFactory(new PropertyValueFactory<Request,String>("id"));
        requestClientIdColumn.setCellValueFactory(new PropertyValueFactory<Request,String>("clientName"));
        requestColumn.setCellValueFactory(new PropertyValueFactory<Request,String>("request"));
        requestCheckedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                Request request = (Request) param.getValue();
                SimpleBooleanProperty p = new SimpleBooleanProperty(request.getChecked());
                return p;
            }
        });
        requestCheckedColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                CheckBoxTableCell cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        requestApprovedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                Request request = (Request) param.getValue();
                SimpleBooleanProperty p = new SimpleBooleanProperty(request.getApproved());
                return p;
            }
        });
        requestApprovedColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                CheckBoxTableCell cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        requestProductColumn.setCellValueFactory(new PropertyValueFactory<RequestProduct,String>("productName"));
        requestCountColumn.setCellValueFactory(new PropertyValueFactory<RequestProduct,String>("count"));

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("id"));
        orderClientColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("clientName"));
        orderRequestColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("requestName"));
        orderContractColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("contractName"));
        orderPaymentColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                Order order = (Order) param.getValue();
                SimpleBooleanProperty p = new SimpleBooleanProperty(order.getPayment());
                return p;
            }
        });
        orderPaymentColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                CheckBoxTableCell cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        orderProductColumn.setCellValueFactory(new PropertyValueFactory<OrderProduct,String>("productName"));
        orderCountColumn.setCellValueFactory(new PropertyValueFactory<OrderProduct,String>("count"));
        orderRestColumn.setCellValueFactory(new PropertyValueFactory<OrderProduct,String>("rest"));

        contractNameColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("contractName"));
        contractClientNameColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("clientName"));
        contractBeginDateColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("beginDate"));
        contractEndDateColumn.setCellValueFactory(new PropertyValueFactory<Order,String>("endDate"));
        contractPaymentColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                Order order = (Order) param.getValue();
                SimpleBooleanProperty p = new SimpleBooleanProperty(order.getPayment());
                return p;
            }
        });
        contractPaymentColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                CheckBoxTableCell cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<Invoice,String>("id"));
        invoiceContractColumn.setCellValueFactory(new PropertyValueFactory<Invoice,String>("contractName"));
        invoiceDateCreateColumn.setCellValueFactory(new PropertyValueFactory<Invoice,String>("dateCreate"));
        invoiceAgreedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                Invoice invoice = (Invoice) param.getValue();
                SimpleBooleanProperty p = new SimpleBooleanProperty(invoice.getAgreed());
                return p;
            }
        });
        invoiceAgreedColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                CheckBoxTableCell cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        invoiceProductNameColumn.setCellValueFactory(new PropertyValueFactory<InvoiceProduct,String>("productName"));
        invoiceProductCountColumn.setCellValueFactory(new PropertyValueFactory<InvoiceProduct,String>("count"));
        invoiceProductLoadedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                InvoiceProduct invoice = (InvoiceProduct) param.getValue();
                SimpleBooleanProperty p = new SimpleBooleanProperty(invoice.getLoaded());
                return p;
            }
        });
        invoiceProductLoadedColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                CheckBoxTableCell cell = new CheckBoxTableCell();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
