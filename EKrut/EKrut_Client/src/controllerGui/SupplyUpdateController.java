package controllerGui;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import Store.NavigationStoreController;
import client.ClientController;
import common.CommonFunctions;
import common.Message;
import entity.ItemInMachineEntity;
import entity.MachineEntity;
import enums.PopupTypeEnum;
import enums.ScreensNamesEnum;
import enums.TaskType;
import interfaces.ICmbANDTableSetUp;
import interfaces.IScreen;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import utils.PopupSetter;

/**
 * Order report GUI controller, implements Screen interface
 * Getting the report and showing it to user 
 *
 */
/**
 * Supply Update implements Screen interface for a GUI used to update the supply of machines. 
 * display information about the machines and the items in them. 
 * @author Lidor
 */
public class SupplyUpdateController implements ICmbANDTableSetUp {

	@FXML
	private TableColumn<ItemInMachineEntity, Integer> currentAmountCol;

	@FXML
	private GridPane itemDisplayGridPane;

	@FXML
	private TableColumn<ItemInMachineEntity, Integer> itemIdCol;

	@FXML
	private TableColumn<ItemInMachineEntity, String> itemNameCol;

	@FXML
	private ComboBox<MachineEntity> machineCmb;

	@FXML
	private TableColumn<ItemInMachineEntity, Integer> machineIdCol;

	@FXML
	private Label machineNameLbl;

	@FXML
	private Label minamountLbl;

	@FXML
	private Button refreshBtn;

	@FXML
	private Label regionNameLbl;

	@FXML
	private Label machineIdLbl;

	@FXML
	private TableView<ItemInMachineEntity> supplyMangmentTbl;

	@FXML
	private Label titleLbl;

	@FXML
	private Button updatedBtn;

	private static MachineEntity machine;
	private static ClientController chat = HostClientController.getChat(); // define the chat for t
	public static ObservableList<MachineEntity> machineLst = FXCollections.observableArrayList();
	public static ObservableList<ItemInMachineEntity> itemsInMachineLst = FXCollections.observableArrayList();
	private static LinkedHashMap<Integer, ItemInMachineEntity> itemsOg = new LinkedHashMap<Integer, ItemInMachineEntity>();
	private static ArrayList<ItemInMachineEntity> toUpdate;
	private int[] arr = new int[2];
	private String[] arrStr = new String[2];

	public static boolean recievedData = false;

	/***
	 * initialize the screen
	 * 
	 */
	@Override
	public void initialize() {
		arrStr[0] = "1";
		arrStr[1] = NavigationStoreController.connectedUser.getId() + "";
		recievedData = false;
		chat.acceptObj(new Message(TaskType.InitMachinesSupplyUpdate, arrStr));
		while (!recievedData) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setUpComboBox();

	}

	/***
	 * set the combobox and handle the choice of a machine
	 */
	public void setUpComboBox() {
		machineCmb.setItems(machineLst);
		if (machineCmb.getItems().isEmpty()) {
			setDisableItems();
			PopupSetter.createPopup(PopupTypeEnum.Warning, "No new calls for items");
		}
		machineCmb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (newValue != null) {
				machine = machineCmb.getValue();
				if (machine.equals(null))
					PopupSetter.createPopup(PopupTypeEnum.Warning, "You have to choose a machine");
				else if (oldValue != newValue) {
					regionNameLbl.setText(machine.getRegionName());
					machineNameLbl.setText(machine.machineName);
					machineIdLbl.setText(machine.machineId + "");
					machineNameLbl.setVisible(true);
					minamountLbl.setText(machine.getMinamount() + "");
					toUpdate = new ArrayList<ItemInMachineEntity>();
					setupTable(machine.machineId);

				}
			}

		});

		// recievedData = true;
	}
	/***
	 * sets the combo box and the update btn Disable when there is no call for this worker
	 */
	void setDisableItems() {
		machineCmb.setDisable(true);
		updatedBtn.setDisable(true);
	}

	/***
	 * table setup
	 * 
	 * @param machineId
	 * @throws InterruptedException
	 */
	public void setupTable(int machineId) {
		arr[0] = machineId;
		arr[1] = NavigationStoreController.connectedUser.getId();
		recievedData = false;
		chat.acceptObj(new Message(TaskType.RequestProssecedItemsInMachine, arr));
		while (!recievedData)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		supplyMangmentTbl.setEditable(true);
		supplyMangmentTbl.setItems(itemsInMachineLst);
		// factory
		setFactoryCols();
	}

	/***
	 * set column for table 
	 * currentAmountCol is a text filed table cell
	 */
	@SuppressWarnings("unchecked")
	private void setFactoryCols() {
		machineIdCol
				.setCellValueFactory((Callback) new PropertyValueFactory<ItemInMachineEntity, Integer>("machineId"));
		itemIdCol.setCellValueFactory((Callback) new PropertyValueFactory<ItemInMachineEntity, Integer>("itemId"));
		itemNameCol.setCellValueFactory((Callback) new PropertyValueFactory<ItemInMachineEntity, String>("name"));

		currentAmountCol.setCellValueFactory(
				(Callback) new PropertyValueFactory<ItemInMachineEntity, Integer>("currentAmount"));

		currentAmountCol.setEditable(true);
		currentAmountCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		currentAmountCol.setOnEditCommit(new EventHandler<CellEditEvent<ItemInMachineEntity, Integer>>() {
			@Override
			public void handle(CellEditEvent<ItemInMachineEntity, Integer> event) {

				ItemInMachineEntity item = (ItemInMachineEntity) event.getRowValue();

				ItemInMachineEntity itemUpdate = new ItemInMachineEntity(item.getMachineId(), item.getItemId(),
						item.getCurrentAmount(), ItemInMachineEntity.Call_Status.Processed, 0, item.getWorkerId(), item.getName(), 0.0,
						"");
				itemUpdate.setWorkerId(NavigationStoreController.connectedUser.getId());
				int oldValue = event.getOldValue();
				int newValue = event.getNewValue();
				if (event.getNewValue() != null) {

					if (newValue < oldValue) {
						PopupSetter.createPopup(PopupTypeEnum.Error,
								"You can't decrease the amount of current items");
					}

					else {
						item.setCurrentAmount(newValue);
						itemUpdate.setCurrentAmount(newValue - oldValue);
						if (newValue >= machine.getMinamount()) {

							itemUpdate.setCallStatus(ItemInMachineEntity.Call_Status.Complete);

						} else {
							PopupSetter.createPopup(PopupTypeEnum.Warning,
									"The item is still under the minimum so the call still open\nPlease fill more if you have");
						}
						int index = toUpdate.indexOf(itemUpdate);
						if (index == -1)
							toUpdate.add(itemUpdate);
						else {
							ItemInMachineEntity temp = toUpdate.get(index);
							temp.setCurrentAmount(temp.getCurrentAmount() + itemUpdate.getCurrentAmount());
						}
					}
				}
			}
		});
	}

	// ---------------------------------- set buttons event
	/***
	 * press refresh button to refresh table and item displayed ask from data base
	 * to load updated table
	 * 
	 * @param event
	 */
	@SuppressWarnings("static-access")
	@FXML
	void refresh(ActionEvent event) {
		MachineEntity tempMachine = machineCmb.getValue();
		Platform.runLater(() -> {
			try {
				NavigationStoreController.getInstance().refreshStage(ScreensNamesEnum.SupplyUpdate);
				CommonFunctions.SleepFor(500, () -> {
					SupplyUpdateController sc = (SupplyUpdateController) NavigationStoreController.getInstance()
							.getController();
					sc.setUpComboBox();
					if (!sc.machineLst.isEmpty()) {
						for (MachineEntity m : sc.machineLst) {
							if (m.machineId == tempMachine.machineId)
								sc.machineCmb.getSelectionModel().select(m);
						}
					} else {
						setDisableItems();
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	
	/**
	* Update the item stock for the selected machine
	* @param event The event that triggers the update
	*/
	@FXML
	void update(ActionEvent event) {
		int itemId;
		int ogCurAmount;
		for (ItemInMachineEntity i : toUpdate) {
			 itemId = i.getId();
			 ogCurAmount = itemsOg.get(itemId).getCurrentAmount();
			 if ( ogCurAmount >= machine.getMinamount())
				i.setCallStatus(ItemInMachineEntity.Call_Status.Complete);
		}
		chat.acceptObj(new Message(TaskType.RequestItemsInMachineRestockFromServer, toUpdate));
		PopupSetter.createPopup(PopupTypeEnum.Success, "Update success!");
		toUpdate.clear();
		refresh(null);
	}

	// ----------------------------------------------------------------- get data
	/**
	 * get machines and put them in a combo box
	 * 
	 * @param arrayList
	 */
	public static void getAllMachines(ArrayList<MachineEntity> arrayList) {
		if (machineLst.isEmpty())
			machineLst.addAll(arrayList);
		else {Platform.runLater(() -> {
			if (!machineLst.isEmpty())
				machineLst.clear();
			machineLst.addAll(arrayList);
		});}

		recievedData = true;

	}

	/***
	 * get items in machine from server in processed status
	 * 
	 * @param obj
	 */
	public static void recevieItemsInMachine(ArrayList<ItemInMachineEntity> obj) {
		if (!itemsInMachineLst.isEmpty()) {
			itemsInMachineLst.clear();
			itemsOg.clear();
		}
		itemsInMachineLst.addAll(obj);
		for (ItemInMachineEntity item : itemsInMachineLst) {
			itemsOg.put(item.getItemId(), item);
		}
		recievedData = true;
	}
}
