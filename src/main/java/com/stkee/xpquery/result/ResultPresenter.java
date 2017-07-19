package com.stkee.xpquery.result;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.stkee.xpquery.dashboard.DashboardPresenter;
import com.xpquery.core.FieldProcessor;
import com.xpquery.core.FieldTransformer;
import com.xpquery.core.KeyValueParser;
import com.xpquery.metadata.XPQueryConfiguration;
import com.xpquery.model.Field;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ResultPresenter implements Initializable {

	@FXML
	TableView<Field> resultTable;

	@FXML
	TableColumn<Field, String> labelColumn;

	@FXML
	TableColumn<Field, String> valueColumn;

	private DashboardPresenter mediator;
	
	final ObservableList<Field> items = FXCollections.observableArrayList();
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelColumn.setCellValueFactory(new PropertyValueFactory<Field, String>("fieldName"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<Field, String>("fieldValue"));
		resultTable.setItems(items);
	
	}

	public void init(DashboardPresenter mediator) {
		this.mediator = mediator;
	}
	
	public void parseResult(Document document, XPQueryConfiguration configuration) throws XPathExpressionException{

		FieldProcessor processor = new FieldProcessor(document, configuration);
		FieldTransformer transformer = new FieldTransformer();
		
		KeyValueParser parser = new KeyValueParser(processor, transformer);
		List<Field> resultList = parser.ProcessField();
		items.clear();
		items.addAll(resultList);
		
	}
	

}
