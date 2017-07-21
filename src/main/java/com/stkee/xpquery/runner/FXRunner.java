package com.stkee.xpquery.runner;

import com.stkee.xpquery.dashboard.DashboardView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXRunner extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		DashboardView view=new DashboardView();
		Scene scene =new Scene(view.getView());
		primaryStage.setTitle("XPQuery");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
