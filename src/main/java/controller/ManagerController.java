package controller;

import entity.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerController implements Initializable {

    //client
    @FXML
    private TableColumn<Client,String> clientIdColumn;
    @FXML
    private TextField clientName;
    @FXML
    private TableColumn<Client,String> clientNameColumn;
    @FXML
    private TextField clientSurname;
    @FXML
    private TableColumn<Client,String> clientSurnameColumn;
    @FXML
    private TextField clientPhone;
    @FXML
    private TableColumn<Client,String> clientPhoneColumn;
    @FXML
    private TextField clientPhone2;
    @FXML
    private TableColumn<Client,String> clientPhone2Column;
    @FXML
    private TextField clientEmail;
    @FXML
    private TableColumn<Client,String> clientEmailColumn;
    @FXML
    private Button clientAddButton;
    @FXML
    private Button clientRemoveButton;
    @FXML
    private Button clientChangeButton;
    @FXML
    private TextField clientAdress;
    @FXML
    private TableColumn<Client,String> clientAdressColumn;
    @FXML
    private TableView clientTable;

    //request
    @FXML
    private TableColumn<Request,String> requestIdColumn;
    @FXML
    private TableColumn<Request,String> requestClientIdColumn;
    @FXML
    private TableColumn<Request,String> requestColumn;
    @FXML
    private TableColumn requestCheckedColumn;
    @FXML
    private TableColumn requestApprovedColumn;
    @FXML
    private TableColumn<RequestProduct,String> requestProductColumn;
    @FXML
    private TableColumn<RequestProduct,String> requestCountColumn;
    @FXML
    private TableView requestTable;
    @FXML
    private TableView requestProductTable;

    //order
    @FXML
    private TableColumn<Order,String> orderIdColumn;
    @FXML
    private TableColumn<Order,String> orderClientColumn;
    @FXML
    private TableColumn<Order,String> orderRequestColumn;
    @FXML
    private TableColumn<Order,String> orderContractColumn;
    @FXML
    private TableColumn orderPaymentColumn;
    @FXML
    private TableColumn<OrderProduct,String> orderProductColumn;
    @FXML
    private TableColumn<OrderProduct,String> orderCountColumn;
    @FXML
    private TableColumn<OrderProduct,String> orderRestColumn;
    @FXML
    private TableView orderTable;
    @FXML
    private TableView orderProductTable;

    //contract
    @FXML
    private TableColumn<Order,String> contractNameColumn;
    @FXML
    private TableColumn<Order,String> contractClientNameColumn;
    @FXML
    private TableColumn<Order,String> contractBeginDateColumn;
    @FXML
    private TableColumn<Order,String> contractEndDateColumn;
    @FXML
    private TableColumn contractPaymentColumn;
    @FXML
    private TableView contractTable;

    //invoice
    @FXML
    private TableColumn<Invoice,String> invoiceIdColumn;
    @FXML
    private TableColumn<Invoice,String> invoiceContractColumn;
    @FXML
    private TableColumn<Invoice,String> invoiceDateCreateColumn;
    @FXML
    private TableColumn invoiceAgreedColumn;
    @FXML
    private TableColumn<InvoiceProduct,String> invoiceProductNameColumn;
    @FXML
    private TableColumn<InvoiceProduct,String> invoiceProductCountColumn;
    @FXML
    private TableColumn invoiceProductLoadedColumn;
    @FXML
    private TableView invoiceTable;
    @FXML
    private TableView invoiceProductTable;

    private String login;
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public ManagerController()  {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setFactories();
        this.setOnEditCommit();

        this.setAllClients();
        this.setAllRequests();
        this.setAllOrders();
        this.setAllInvoices();
    }

    private void setAllClients(){
        ObservableList<Client> usersList = FXCollections.observableArrayList();
        this.setFactories();
        Session session = sessionFactory.openSession();
        Query query1 = session.createQuery(" FROM ClientEntity ");
        Iterator iter = query1.list().iterator();
        while(iter.hasNext()){
            ClientEntity clientEntity = (ClientEntity) iter.next();
            usersList.add(new Client(clientEntity));
        }
        clientTable.setItems(usersList);
        session.close();
    }
    private void setAllRequests(){
        ObservableList<Request> requestsList = FXCollections.observableArrayList();

        Session session = sessionFactory.openSession();
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
        session.close();
    }
    private void setAllOrders(){
        ObservableList<Order> ordersList = FXCollections.observableArrayList();

        Session session = sessionFactory.openSession();
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
        session.close();
    }
    private void setAllInvoices(){
        ObservableList<Invoice> invoicesList = FXCollections.observableArrayList();

        Session session = sessionFactory.openSession();
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
        session.close();
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
    private void setOnEditCommit(){
        clientIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        clientIdColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Client, String> t) {
                ((Client) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setId(t.getNewValue());
            }
        });
        clientNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        clientNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Client, String> t) {
                ((Client) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setName(t.getNewValue());
            }
        });
        clientSurnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        clientSurnameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Client, String> t) {
                ((Client) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setSurname(t.getNewValue());
            }
        });
        clientPhoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        clientPhoneColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Client, String> t) {
                ((Client) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setPhone(t.getNewValue());
            }
        });
        clientPhone2Column.setCellFactory(TextFieldTableCell.forTableColumn());
        clientPhone2Column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Client, String> t) {
                ((Client) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setPhone2(t.getNewValue());
            }
        });
        clientAdressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        clientAdressColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Client, String> t) {
                ((Client) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setAdress(t.getNewValue());
            }
        });
        clientEmailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        clientEmailColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Client, String> t) {
                ((Client) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setEmail(t.getNewValue());
            }
        });

        requestIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestIdColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Request, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Request, String> t) {
                ((Request) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setId(t.getNewValue());
            }
        });
        requestClientIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestClientIdColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Request, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Request, String> t) {
                ((Request) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setClientName(t.getNewValue());
            }
        });
        requestColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Request, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Request, String> t) {
                ((Request) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setRequest(t.getNewValue());
            }
        });
        requestCheckedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestCheckedColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Request, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Request, String> t) {
                ((Request) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setChecked(Boolean.getBoolean(t.getNewValue()));
            }
        });
        requestApprovedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestApprovedColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Request, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Request, String> t) {
                ((Request) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setApproved(Boolean.getBoolean(t.getNewValue()));
            }
        });
        requestProductColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestProductColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RequestProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<RequestProduct, String> t) {
                ((RequestProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setProductName(t.getNewValue());
            }
        });
        requestCountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        requestCountColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RequestProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<RequestProduct, String> t) {
                ((RequestProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setCount(t.getNewValue());
            }
        });

        orderIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderIdColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setId(t.getNewValue());
            }
        });
        orderClientColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderClientColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setClientName(t.getNewValue());
            }
        });
        orderRequestColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderRequestColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setRequestName(t.getNewValue());
            }
        });
        orderContractColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderContractColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setContractName(t.getNewValue());
            }
        });
        orderPaymentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderPaymentColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setPayment(Boolean.getBoolean(t.getNewValue()));
            }
        });
        orderProductColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderProductColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OrderProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<OrderProduct, String> t) {
                ((OrderProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setProductName(t.getNewValue());
            }
        });
        orderCountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderCountColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OrderProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<OrderProduct, String> t) {
                ((OrderProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setCount(t.getNewValue());
            }
        });
        orderRestColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orderRestColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OrderProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<OrderProduct, String> t) {
                ((OrderProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setRest(t.getNewValue());
            }
        });

        contractNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contractNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setContractName(t.getNewValue());
            }
        });
        contractClientNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contractClientNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setClientName(t.getNewValue());
            }
        });
        contractBeginDateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contractBeginDateColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setBeginDate(t.getNewValue());
            }
        });
        contractEndDateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contractEndDateColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setEndDate(t.getNewValue());
            }
        });
        contractPaymentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contractPaymentColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Order, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Order, String> t) {
                ((Order) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setPayment(Boolean.getBoolean(t.getNewValue()));
            }
        });

        invoiceIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        invoiceIdColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Invoice, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Invoice, String> t) {
                ((Invoice) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setId(t.getNewValue());
            }
        });
        invoiceContractColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        invoiceContractColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Invoice, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Invoice, String> t) {
                ((Invoice) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setId(t.getNewValue());
            }
        });
        invoiceDateCreateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        invoiceDateCreateColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Invoice, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Invoice, String> t) {
                ((Invoice) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setDateCreate(t.getNewValue());
            }
        });
        invoiceAgreedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        invoiceAgreedColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Invoice, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Invoice, String> t) {
                ((Invoice) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setAgreed(Boolean.getBoolean(t.getNewValue()));
            }
        });
        invoiceProductNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        invoiceProductNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InvoiceProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<InvoiceProduct, String> t) {
                ((InvoiceProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setProductName(t.getNewValue());
            }
        });
        invoiceProductCountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        invoiceProductCountColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InvoiceProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<InvoiceProduct, String> t) {
                ((InvoiceProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setCount(t.getNewValue());
            }
        });
        invoiceProductLoadedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        invoiceProductLoadedColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InvoiceProduct, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<InvoiceProduct, String> t) {
                ((InvoiceProduct) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setLoaded(Boolean.getBoolean(t.getNewValue()));
            }
        });
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
