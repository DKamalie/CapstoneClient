package com.example.application.views.admin.employee;

import com.example.application.api.DriverApi;
import com.example.application.api.EmployeeApi;
import com.example.application.domain.Driver;
import com.example.application.domain.Pizzeria;
import com.example.application.domain.Vehicle;
import com.example.application.factory.DriverFactory;
import com.example.application.factory.PizzeriaFactory;
import com.example.application.factory.VehicleFactory;
import com.example.application.views.MainLayout;
import com.example.application.views.admindashboard.AdminDashboard;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Set;

@PageTitle("Driver")
@Route(value = "driver", layout = MainLayout.class)
public class DriverView extends VerticalLayout {

    private TextField employeeId;
    private TextField name;
    private TextField surname;
    private TextField phoneNumber;
    private TextField email;
    private TextField pizzeriaId;
    private TextField vehicleId;
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button viewAllButton;
    private Button resetButton;
    private Div viewContainer;
    private boolean isEditing = false;

    EmployeeApi getALL = new EmployeeApi();

    public DriverView(){
        employeeId = new TextField("Employee Id: ");
        employeeId.setWidth("300px");
        employeeId.setPlaceholder("Enter in the employee Id");

        name = new TextField("Name: ");
        name.setWidth("300px");
        name.setPlaceholder("Enter in the name");

        surname = new TextField("Surname: ");
        surname.setWidth("300px");
        surname.setPlaceholder("Enter in the surname");

        phoneNumber = new TextField("Phone number: ");
        phoneNumber.setWidth("300px");
        phoneNumber.setPlaceholder("Enter in the phone number");

        email = new TextField("Email: ");
        email.setWidth("300px");
        email.setPlaceholder("Enter in the email");

        pizzeriaId = new TextField("Pizzeria Id: ");
        pizzeriaId.setWidth("300px");
        pizzeriaId.setPlaceholder("");
        pizzeriaId.setEnabled(false);

        vehicleId = new TextField("Vehicle Id: ");
        vehicleId.setWidth("300px");
        vehicleId.setPlaceholder("");
        vehicleId.setEnabled(false);

        saveButton = new Button("Save");
        saveButton.setWidth("300px");

        updateButton = new Button("Update");
        updateButton.setWidth("300px");
        updateButton.setEnabled(true);

        deleteButton = new Button("Delete");
        deleteButton.setWidth("300px");

        viewAllButton = new Button("View all drivers");
        viewAllButton.setWidth("300px");

        resetButton = new Button("Reset");
        resetButton.setWidth("300px");

        VerticalLayout inputContainer = new VerticalLayout(employeeId, name, surname, phoneNumber, email, pizzeriaId, vehicleId, saveButton, updateButton, deleteButton, viewAllButton, resetButton);
        inputContainer.setAlignItems(Alignment.BASELINE);

        viewContainer = new Div();

        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        centeringLayout.setSizeFull();

        centeringLayout.add(viewContainer);

        Div dataContainer = new Div(viewContainer);


        Div dataMarginContainer = new Div(dataContainer);
        dataMarginContainer.getStyle()
                .set("margin-left", "200px");

        HorizontalLayout mainContainer = new HorizontalLayout(inputContainer, dataMarginContainer);
        mainContainer.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        saveButton.addClickListener(e -> {//this is for creating a vehicle and saving it to the database
            try {
                boolean hasErrors = checkErrors();

                if (!hasErrors) {
                    Driver driverSave = setDriverValues();
                    createDriver(driverSave);
                    Notification.show("Driver saved");
                }
            } catch (Exception exception) {
                Notification.show(exception.getMessage());
            }
        });

        employeeId.addKeyDownListener(Key.ENTER, event -> {//read method
            try {
                TextField source = (TextField) event.getSource();
                String enteredValue = source.getValue();

                if (!enteredValue.isEmpty()) {
                    Integer enteredEmployeeId = Integer.valueOf(enteredValue);

                    // Call the readVehicleId method to fetch vehicle data
                    Driver employeeData = readEmployeeId(enteredEmployeeId);

                    // Update the other text fields with the retrieved data
                    if (employeeData != null) {
                        updateDriverFields(employeeData);
                        employeeId.setEnabled(false);
                        pizzeriaId.setEnabled(false);
                        vehicleId.setEnabled(false);
                        Notification.show("Employee Id read successfully");
                    } else {
                        // Handle the case where the entered vehicleId does not exist
                        Notification.show("Employee with ID " + enteredEmployeeId + " not found");
                    }
                }
            } catch (NumberFormatException e) {
                // Handle the case where the entered value is not a valid integer
                Notification.show("Please enter a valid employee ID as a number.");
            } catch (Exception e) {
                // Handle any other exceptions that may occur
                Notification.show("An error occurred: " + e.getMessage());
            }
        });

        updateButton.addClickListener(e -> {//use for update method
            try {
                System.out.println(isEditing);

                if (isEditing == false) {
                    Driver updatedDriver = updateSetDriverValues();
                    updateDriver(updatedDriver);

                    Notification.show("Driver updated successfully");
                }
            } catch (Exception ex) {
                Notification.show("An error occurred during the update: " + ex.getMessage());
            }
        });

        deleteButton.addClickListener(e ->{//use for delete method
            try {
                String employeeIdValue = employeeId.getValue();
                if (!employeeIdValue.isEmpty()) {
                    Integer id = Integer.valueOf(employeeIdValue);
                    deleteEmployeeId(id);
                    Notification.show("Driver deleted successfully");
                    clearFormFields();
                } else {
                    Notification.show("Please enter a employee ID.");
                }
            } catch (NumberFormatException ex) {
                Notification.show("Please enter a valid employee ID as a number.");
            } catch (Exception ex) {
                Notification.show("An error occurred during the delete: " + ex.getMessage());
            }
        });

        viewAllButton.addClickListener(event -> {//use for getAll method
            try {
                Set<Driver> drivers = getALL.getAllDriver();
                viewContainer.removeAll();
                drivers.forEach(driver -> {
                    viewContainer.add(createDriverSpan(driver));
                });
            } catch (Exception e) {
                Notification.show("Failed to retrieve drivers. Please try again later." + e.getMessage());
            }
        });

        resetButton.addClickListener(event -> {//use for getAll method
            try {
                clearFormFields();
                viewContainer.removeAll();
            } catch (Exception e) {
                Notification.show("Failed to clear the fields." + e.getMessage());
            }
        });

        Style buttonStyle = saveButton.getStyle();
        buttonStyle.set("color", "white");
        buttonStyle.set("background-color", "#000000");
        buttonStyle.set("font-family", "Arial");
        buttonStyle.set("font-size", "16px");
        buttonStyle.set("font-weight", "bold");
        buttonStyle.set("border-radius", "17px");
        buttonStyle.set("box-shadow", "0 5px 4px rgba(0, 0, 0, 0.2)");

        Style buttonStyle2 = updateButton.getStyle();
        buttonStyle2.set("color", "white");
        buttonStyle2.set("background-color", "#000000");
        buttonStyle2.set("font-family", "Arial");
        buttonStyle2.set("font-size", "16px");
        buttonStyle2.set("font-weight", "bold");
        buttonStyle2.set("border-radius", "17px");
        buttonStyle2.set("box-shadow", "0 5px 4px rgba(0, 0, 0, 0.2)");

        Style buttonStyle3 = deleteButton.getStyle();
        buttonStyle3.set("color", "white");
        buttonStyle3.set("background-color", "#000000");
        buttonStyle3.set("font-family", "Arial");
        buttonStyle3.set("font-size", "16px");
        buttonStyle3.set("font-weight", "bold");
        buttonStyle3.set("border-radius", "17px");
        buttonStyle3.set("box-shadow", "0 5px 4px rgba(0, 0, 0, 0.2)");

        Style buttonStyle4 = viewAllButton.getStyle();
        buttonStyle4.set("color", "white");
        buttonStyle4.set("background-color", "#000000");
        buttonStyle4.set("font-family", "Arial");
        buttonStyle4.set("font-size", "16px");
        buttonStyle4.set("font-weight", "bold");
        buttonStyle4.set("border-radius", "17px");
        buttonStyle4.set("box-shadow", "0 5px 4px rgba(0, 0, 0, 0.2)");

        Style buttonStyle5 = resetButton.getStyle();
        buttonStyle5.set("color", "white");
        buttonStyle5.set("background-color", "#000000");
        buttonStyle5.set("font-family", "Arial");
        buttonStyle5.set("font-size", "16px");
        buttonStyle5.set("font-weight", "bold");
        buttonStyle5.set("border-radius", "17px");
        buttonStyle5.set("box-shadow", "0 5px 4px rgba(0, 0, 0, 0.2)");

        Button backButton = new Button("< Back");
        backButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(AdminDashboard.class)));


        backButton.getStyle().set("color", "white");
        backButton.getStyle().set("background-color", "#000000");
        backButton.getStyle().set("font-family", "Arial");
        backButton.getStyle().set("font-size", "16px");
        backButton.getStyle().set("font-weight", "bold");
        backButton.getStyle().set("border-radius", "17px");
        backButton.getStyle().set("box-shadow", "0 5px 4px rgba(0, 0, 0, 0.2)");


        HorizontalLayout buttonLayout = new HorizontalLayout(backButton);
        buttonLayout.setVerticalComponentAlignment(Alignment.START, backButton);
        add(buttonLayout);

        setMargin(true);

        add(mainContainer);
    }

    public void createDriver(Driver driver) {//use for create method
        DriverApi driverApi = new DriverApi();
        driverApi.createDriver(driver);
    }

    public Driver readEmployeeId(Integer employeeId) {//use for read method
        DriverApi readEmployeeId = new DriverApi();
        return readEmployeeId.readDriver(employeeId);
    }

    public void updateDriver(Driver driver) {//use for the update method
        DriverApi updateDriver = new DriverApi();
        updateDriver.updateDriver(driver);
    }

    public void deleteEmployeeId(Integer employeeId){//use for delete method
        DriverApi deleteEmployeeId = new DriverApi();
        deleteEmployeeId.deleteDriver(employeeId);
    }

    Pizzeria pizzeria = PizzeriaFactory.buildPizzaria("Hill Crest", "300 Long St, Cape Town City Centre, 8000");
    Vehicle vehicle = VehicleFactory.createVehicle("Mazda", "Mazda RX", "GTR", "2021", "Black");

    public void updateDriverFields(Driver driver) {//this is not an update method, this is for the read method
        name.setValue(driver.getName());
        surname.setValue(driver.getSurname());
        phoneNumber.setValue(driver.getPhoneNumber());
        email.setValue(driver.getEmail());
        pizzeriaId.setValue(String.valueOf(driver.getPizzeria().getPizzeriaID()));
        vehicleId.setValue(String.valueOf(driver.getVehicle().getVehicleId()));
    }


    public Driver updateSetDriverValues() {//use for update method
        Integer employeeIdValue = Integer.valueOf(employeeId.getValue());
        String nameValue = name.getValue();
        String surnameValue = surname.getValue();
        String phoneNumberValue = phoneNumber.getValue();
        String emailValue = email.getValue();

        Driver updateDriverData = DriverFactory.createDriver(employeeIdValue, nameValue, surnameValue, phoneNumberValue, emailValue, vehicle, pizzeria);

        return updateDriverData;
    }

    public Driver setDriverValues() {//use for create method

        String nameValue = name.getValue();
        String surnameValue = surname.getValue();
        String phoneNumberValue = phoneNumber.getValue();
        String emailValue = email.getValue();

        Driver getDriverData = DriverFactory.buildDriver(nameValue, surnameValue, phoneNumberValue, emailValue, vehicle, pizzeria);

        return getDriverData;
    }

    public boolean isValidEmail(String email) {//email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return email.matches(emailRegex);
    }

    public boolean checkErrors() {//checks errors
        String nameValue = name.getValue();
        String surnameValue = surname.getValue();
        String phoneNumberValue = phoneNumber.getValue();
        String emailValue = email.getValue();

        if (nameValue.isEmpty() || surnameValue.isEmpty() || phoneNumberValue.isEmpty() || emailValue.isEmpty()) {
            Notification.show("Please enter in all the fields.");
            return true;
        }

        if (!nameValue.matches("[a-zA-Z ]+") || !surnameValue.matches("[a-zA-Z ]+")) {
            Notification.show("Invalid input. please only enter letters.");
            return true;
        }

        if (!phoneNumberValue.matches("^[0-9]+$")) {
            Notification.show("Invalid input, please only enter in numbers.");
            return true;
        }

        if(!(phoneNumberValue.length() == 10)){
            Notification.show("Invalid input, phone number must contain 10 digits");
            return true;
        }

        if (!isValidEmail(emailValue)){
            Notification.show("Invalid email, please try again");
            return true;
        }
        return false;
    }

    private Div createDriverSpan(Driver driver) {
        Div outerDiv = new Div();
        outerDiv.getStyle().set("display", "flex");
        outerDiv.getStyle().set("justify-content", "center");
        outerDiv.getStyle().set("margin-top", "15px");
        outerDiv.getStyle().set("margin-right", "300px");

        Div innerDiv = new Div();
        innerDiv.getStyle().set("display", "flex");
        innerDiv.getStyle().set("flex-direction", "row");
        innerDiv.getStyle().set("justify-content", "space-between");

        createDataField(innerDiv, "Employee ID ", String.valueOf(driver.getEmpId()));
        createDataField(innerDiv, "Name ", driver.getName());
        createDataField(innerDiv, "Surname ", driver.getSurname());
        createDataField(innerDiv, "Phone number ", driver.getPhoneNumber());
        createDataField(innerDiv, "Email ", driver.getEmail());

        outerDiv.add(innerDiv);

        return outerDiv;
    }

    private void createDataField(Div container, String label, String value) {
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-weight", "bold");
        Span valueSpan = new Span(value);

        Div dataField = new Div(labelSpan, valueSpan);
        dataField.getStyle().set("margin-right", "40px");

        container.add(dataField);
    }

    private void clearFormFields() {//clear text fields
        employeeId.clear();
        name.clear();
        surname.clear();
        phoneNumber.clear();
        email.clear();
        pizzeriaId.clear();
        vehicleId.clear();
        employeeId.setEnabled(true);
    }
}
