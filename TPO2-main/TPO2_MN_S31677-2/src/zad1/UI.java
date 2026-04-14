package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;

public class UI {
    private JFrame frame;
    private JTextField city;
    private JTextField country;
    private JTextField currency;
    private JTextArea displayArea;
    private JFXPanel wikipedia;
    private JButton searchButton;
    private JPanel panel;
    private Service service;

    public UI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("city:"));
        city = new JTextField();
        panel.add(city);

        panel.add(new JLabel("country:"));
        country = new JTextField();
        panel.add(country);

        panel.add(new JLabel("currency:"));
        currency = new JTextField();
        panel.add(currency);


        searchButton = new JButton("Click me ");
        searchButton.addActionListener(e -> loadData());
        panel.add(searchButton);

        frame.add(panel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setRows(4);
        displayArea.setMinimumSize(new Dimension(600, 50));
        //    frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        frame.add(displayArea, BorderLayout.CENTER);
        wikipedia = new JFXPanel();
        wikipedia.setMaximumSize(new Dimension(600,300));
        frame.add(wikipedia, BorderLayout.SOUTH);

        frame.setVisible(true);
    }


    private void loadData() {
        String cityText = city.getText().trim();
        String countryText = country.getText().trim();
        String currencyText = currency.getText().trim();

        if (cityText.isEmpty() || countryText.isEmpty() || currencyText.isEmpty()) {
            displayArea.setText("All fields must be filled");
            return;
        }

        service = new Service(countryText);

        String weather = service.getWeather(cityText);
        double rate = service.getRateFor(currencyText);
        double NBPrate = service.getNBPRate();

        displayArea.setText(displayWeather(weather) + "\n" + displayCurrency(currencyText, rate) + "\n" + displayNBP(NBPrate));
        Platform.runLater(() -> {
            try {
                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();
                webEngine.load("https://en.wikipedia.org/wiki/" + cityText);
                wikipedia.setScene(new Scene(webView));
            } catch (Exception ex) {
                ex.printStackTrace();
                displayArea.append("\nBłąd ładowania Wikipedii.");
            }
        });

    }

    private String displayNBP(double nbPrate) {
        return "Exchange rate: " + nbPrate + " for PLN";
    }

    private String displayCurrency(String currencyText, double rate) {
        return "Exchange rate: " + rate + " for " + currencyText;
    }

    private String displayWeather(String json) {
        return json;
    }


}
