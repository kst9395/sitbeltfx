package com.stkee.xpquery.dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.stkee.xpquery.configuration.ConfigurationPresenter;
import com.stkee.xpquery.result.ResultPresenter;
import com.xpquery.metadata.XPQueryConfiguration;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DashboardPresenter implements Initializable {

	@FXML
	Accordion resultPane;

	@FXML
	ConfigurationPresenter configurationController;

	@FXML
	ResultPresenter resultController;

	private Stage stage;

	private Document document;

	private Document asDocument(File file) {
		Document doc = null;
		try (FileInputStream fis = new FileInputStream(file)) {

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = documentBuilder.parse(fis);
			doc.normalize();
		} catch (Exception e) {

		}
		return doc;

	}

	@FXML
	public void openFile() throws XPathExpressionException {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select File");
		File selectedFile = chooser.showOpenDialog(stage);
		if (selectedFile != null) {
			this.document = asDocument(selectedFile);
			if (configurationController.getLoadConfiguration() != null) {
				XPQueryConfiguration configuration = configurationController.getLoadConfiguration();
				resultController.parseResult(document, configuration);

			}
		}

	}

	@FXML
	public void loadConfiguration() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select File");
		File selectedFile = chooser.showOpenDialog(stage);
		if (selectedFile != null) {
			configurationController.loadConfig(selectedFile);
		}
	}

	@FXML
	public void closeProgram() {
		Platform.exit();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configurationController.init(this);

		resultPane.setOnDragOver(e -> {
			if (e.getGestureSource() != resultPane && e.getDragboard().hasFiles()) {
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			e.consume();

		});

		resultPane.setOnDragDropped(e -> {
			System.out.println("Drag detected");
			Dragboard dragboard = e.getDragboard();
			if (dragboard.hasFiles()) {
				List<File> files = dragboard.getFiles();
				if (files.size() > 1) {

				} else if (files.size() == 1) {
					File file = files.get(0);
					document = asDocument(file);
					reloadResult();
				}
				e.setDropCompleted(true);
				e.consume();
			}else{
				System.out.println("no file in dragboard");
			
			}

		});

		Platform.runLater(() -> {
			stage = (Stage) resultPane.getScene().getWindow();
		});
	}

	public void reloadResult() {
		System.out.print("Result reload");
		if (this.document != null) {
			try {
				XPQueryConfiguration configuration = configurationController.getLoadConfiguration();
				resultController.parseResult(document, configuration);
			} catch (XPathExpressionException xep) {

			}
		}
	}

}
