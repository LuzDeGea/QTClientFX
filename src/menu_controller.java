import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class menu_controller {
	@FXML
	private Text actiontarget;

	@FXML
	protected void handleSubmitButtonAction(final ActionEvent event) {
		actiontarget.setText("Sign in button pressed");
	}

}