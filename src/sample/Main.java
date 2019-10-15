package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Cars");

        VBox vBox = new VBox();
        ComboBox make = new ComboBox();
        ComboBox model = new ComboBox();
        ComboBox year = new ComboBox();
        make.setEditable(true);
        model.setEditable(true);
        year.setEditable(true);

        Button submit = new Button("Submit");
        Button search = new Button("Search");

        Text yearLabel = new Text("Year:");
        Text makeLabel = new Text("Make:");
        Text modelLabel = new Text("Model:");

        vBox.getChildren().addAll(yearLabel, year, makeLabel, make, modelLabel, model, submit, search);
        vBox.setSpacing(10);


        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!checkForNulls(year.getValue(), make.getValue(), model.getValue())) {
                    xmlSave((String)year.getValue(), (String)make.getValue(), (String)model.getValue());
                }
            }
        });

        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                searchScene(primaryStage);
            }
        });

        List<String[]> cars = xmlRead();
        if (cars != null) {
            for (String[] car : cars) {
                if(!checkForNulls(car[0], car[1], car[2])) {
                    year.getItems().add(car[0]);
                    make.getItems().add(car[1]);
                    model.getItems().add(car[2]);
                }
            }
        }

        primaryStage.setScene(new Scene(vBox, 300, 275));
        primaryStage.show();
    }

    private void searchScene(Stage mainStage) {
        VBox searchBox = new VBox();

        Label searchLabel = new Label("Model: ");
        TextField searchField = new TextField();
        Button searchButton = new Button("search");
        Text resultText = new Text();
        searchBox.getChildren().addAll(searchLabel, searchField, searchButton, resultText);
        searchBox.setSpacing(10);

        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String[] result = findCar(searchField.getText());
                if (result == null) {
                    resultText.setText("car not found");
                } else {
                    resultText.setText("");
                    for (int temp = 0; temp < result.length; temp++) {
                        resultText.setText(resultText.getText() + result[temp] + " ");
                    }
                }
            }
        });

        Scene searchScene = new Scene(searchBox, 300,  275);
        Stage searchStage = new Stage();
        searchStage.setScene(searchScene);
        mainStage.hide();
        searchStage.show();
    }

    private boolean checkForNulls(Object...args){
        List<Object> test = new ArrayList<Object>(Arrays.asList(args));
        return test.contains(null);
    }

    private void xmlSave(String year, String make, String model) {
        try {
            File carsFile = new File("src/sample/cars.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(carsFile);

            Element rootElement = doc.getDocumentElement();
            Node superCars =  doc.getElementsByTagName("supercars").item(0);

            List<String> cars = new ArrayList<String>();
            cars.add("");

            for (String car : cars) {
                Element newCar = doc.createElement("car");

                Element carYear = doc.createElement("year");
                carYear.appendChild(doc.createTextNode(year));
                newCar.appendChild(carYear);

                Element carMake = doc.createElement("make");
                carMake.appendChild(doc.createTextNode(make));
                newCar.appendChild(carMake);

                Element carModel = doc.createElement("model");
                carModel.appendChild(doc.createTextNode(model));
                newCar.appendChild(carModel);
                superCars.appendChild(newCar);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult result = new StreamResult(carsFile);
            transformer.transform(domSource, result);

            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(domSource, consoleResult);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private static List<String[]> xmlRead() {
        try {
            List<String[]> cars;
            cars = new ArrayList<String[]>();
            String[] car = null;
            File carsFile = new File("src/sample/cars.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(carsFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("car");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    car = new String[3];
                    Element year = (Element) eElement.getElementsByTagName("year").item(0);
                    Element make = (Element) eElement.getElementsByTagName("make").item(0);
                    Element model = (Element) eElement.getElementsByTagName("model").item(0);

                    //add data to car array
                    car[0] = year.getTextContent();
                    car[1] = make.getTextContent();
                    car[2] = model.getTextContent();

                    //add car array to list
                    cars.add(car);
                }

            }

            return cars;
        } catch (Exception e) {
            System.out.println("error: " + e);
            return null;
        }
    }

    private String[] findCar(String search) {
        try {
            File carsFile = new File("src/sample/cars.xml");
            List<String[]> cars;
            cars = new ArrayList<String[]>();
            String[] car = null;


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(carsFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("car");
            String[] models = new String[nList.getLength()];


            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eElement = (Element) node;
                    car = new String[3];
                    Element year = (Element) eElement.getElementsByTagName("year").item(0);
                    Element make = (Element) eElement.getElementsByTagName("make").item(0);
                    Element model = (Element) eElement.getElementsByTagName("model").item(0);

                    //add data to car array
                    car[0] = year.getTextContent();
                    car[1] = make.getTextContent();
                    car[2] = model.getTextContent();

                    //add car array to list
                    cars.add(car);

                }
            }
            Collections.sort(cars, new Comparator<String[]>() {
                @Override
                public int compare(String[] strings, String[] t1) {
                    return strings[2].compareToIgnoreCase(t1[2]);
                }
            });
            for(int temp = 0; temp < cars.size(); temp++){
                models[temp] = cars.get(temp)[2];
            }

            String[] carFound = cars.get(binarySearch(models, 0, models.length - 1, search));
            return carFound;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private int binarySearch(String arr[], int low, int range, String x) {
        while(low <= range){
            int mid = low + (range - low)/2;
            int res = x.compareToIgnoreCase(arr[mid]);
            if (res == 0) {
                return mid;
            } else if (res > 0) {
                low = mid + 1;
            } else {
                range = mid - 1;
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
