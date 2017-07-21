package com.stkee.xpquery.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import com.stkee.xpquery.dashboard.DashboardPresenter;
import com.xpquery.core.XPQueryConfigurationService;
import com.xpquery.metadata.SingleFieldDefinition;
import com.xpquery.metadata.XPQueryConfiguration;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ConfigurationPresenter implements Initializable {

	@FXML
	TableColumn<SingleFieldDefinition, String> columnSectionName;

	@FXML
	TableColumn<SingleFieldDefinition, String> columnLabelQuery;

	@FXML
	TableColumn<SingleFieldDefinition, String> columnValueQuery;

	@FXML
	TableView<SingleFieldDefinition> sectionTable;

	final ObservableList<SingleFieldDefinition> fieldDefinitions = FXCollections.observableArrayList();

	DashboardPresenter mediator;

	@Inject
	private XPQueryConfigurationService configurationService;

	@FXML
	TextField textSectionName;

	@FXML
	TextField textLabelQuery;

	@FXML
	TextField textValueQuery;

	@FXML
	Button addSectionButton;

	XPQueryConfiguration loadConfiguration;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		columnSectionName.setMaxWidth(1d * Integer.MAX_VALUE * 20);
		columnLabelQuery.setMaxWidth(1d * Integer.MAX_VALUE * 40);
		columnValueQuery.setMaxWidth(1d * Integer.MAX_VALUE * 40);
		columnSectionName.setCellValueFactory(new PropertyValueFactory<SingleFieldDefinition, String>("sectionName"));
		columnLabelQuery.setCellValueFactory(new PropertyValueFactory<SingleFieldDefinition, String>("labelQuery"));
		columnValueQuery.setCellValueFactory(new PropertyValueFactory<SingleFieldDefinition, String>("valueQuery"));
		columnSectionName.setCellFactory(TextFieldTableCell.<SingleFieldDefinition>forTableColumn());
		columnLabelQuery.setCellFactory(TextFieldTableCell.<SingleFieldDefinition>forTableColumn());
		columnValueQuery.setCellFactory(TextFieldTableCell.<SingleFieldDefinition>forTableColumn());
		sectionTable.setItems(fieldDefinitions);
		sectionTable.setEditable(true);
		addSectionButton.disableProperty().bind(textValueQuery.textProperty().isEmpty()
				.or(textLabelQuery.textProperty().isEmpty()).or(textSectionName.textProperty().isEmpty()));

		sectionTable.setOnDragOver(e->{
			
			if(e.getGestureSource()!=sectionTable && e.getDragboard().hasFiles()){
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				
			}
			e.consume();
		});
		
		sectionTable.setOnDragDropped(e->{
			System.out.println("File Dropped");
			if(e.getGestureSource()!=sectionTable && e.getDragboard().hasFiles()){
				Dragboard dragboard = e.getDragboard();
				List<File> files = dragboard.getFiles();
				File file = files.get(0);
				
				loadConfig(file);
			}
			e.setDropCompleted(true);
			e.consume();
		});
		
		
		fieldDefinitions.addListener(new ListChangeListener<SingleFieldDefinition>() {

			@Override
			public void onChanged(Change<? extends SingleFieldDefinition> c) {
				mediator.reloadResult();
			}

		});

	}

	public void init(DashboardPresenter mediator) {
		this.mediator = mediator;
	}

	@FXML
	public void addSection() {
		String sectionName = textSectionName.textProperty().getValue();
		String valueQuery = textValueQuery.textProperty().getValueSafe();
		String labelQuery = textLabelQuery.textProperty().getValue();
		SingleFieldDefinition definition = new SingleFieldDefinition();
		definition.setSectionName(sectionName);
		definition.setValueQuery(valueQuery);
		definition.setLabelQuery(labelQuery);
		fieldDefinitions.add(definition);
		textSectionName.textProperty().set("");
		textValueQuery.textProperty().set("");
		textLabelQuery.textProperty().set("");

	}

	public void loadConfig(File file) {

		try (FileInputStream fis = new FileInputStream(file)) {
			loadConfiguration = configurationService.LoadConfiguration(fis);
			if (loadConfiguration != null) {
				List<SingleFieldDefinition> definitions = loadConfiguration.getFieldDefinition().getDefinitions();
				fieldDefinitions.clear();
				fieldDefinitions.addAll(definitions);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public XPQueryConfiguration getLoadConfiguration() {
		return loadConfiguration;
	}

}
