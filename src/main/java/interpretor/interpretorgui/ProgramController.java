package interpretor.interpretorgui;

import Controller.Controller;
import Exceptions.ADTsExceptions;
import Exceptions.ExpressionException;
import Exceptions.StatementException;
import Model.ADTs.*;
import Model.ProgramState;
import Model.Statements.StatementInterface;
import Model.Value.StringValue;
import Model.Value.ValueInterface;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class ProgramController implements Initializable {

    @FXML
    private TextField programIDs;

    @FXML
    private Button runButton;

    @FXML
    private Label programLabel;

    @FXML
    private ListView<String> executionStack;

    @FXML
    private ListView<String> output;

    @FXML
    private ListView<String> programStateID;

    @FXML
    private ListView<String> fileTable;

    @FXML
    private TableView<Pair<String, ValueInterface>> symbolTable;

    @FXML
    private TableColumn<Pair<String, ValueInterface>, String> varNameColumn;

    @FXML
    private TableColumn<Pair<String, ValueInterface>, ValueInterface> valueColumn;

    @FXML
    private TableView<Pair<Integer, ValueInterface>> heapTable;

    @FXML
    private TableColumn<Pair<Integer, ValueInterface>, Integer> addressColumn;

    @FXML
    private TableColumn<Pair<Integer, ValueInterface>, ValueInterface> heapValueColumn;

    private Controller statementController;
    private int statementID;
    private StatementInterface statement;
    private int programIDNumber;
    private int populateFlag;

    public void setStatementController(Controller c, StatementInterface stmt) throws StatementException, ADTsExceptions, ExpressionException {
        this.statementController = c;
        this.statement = stmt;

        StackInterface<StatementInterface> executionStack1 = new MyStack<StatementInterface>();
        DictInterface<String, ValueInterface> symbolTable1 = new Dictionary<String, ValueInterface>();
        ListInterface<ValueInterface> out1 = new MyList<ValueInterface>();
        DictInterface<StringValue, BufferedReader> file1 = new Dictionary<StringValue, BufferedReader>();
        HeapInterface<ValueInterface> heap1 = new MyHeap<ValueInterface>();
        ProgramState myProgramState1 = new ProgramState(executionStack1, symbolTable1, out1, statement, file1, heap1);
        statementController.addProgram(myProgramState1);

        try{
            statementController.typecheck();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.toString());
            alert.show();
        }

        statementController.setExecutor();
        statementID = statementController.getCurrentID();
        programIDNumber = 1;
        populateFlag = 1;
        populate();

    }

    public void setNumberTextField(){
        this.programIDs.setText(String.valueOf(programIDNumber));
    }

    public void setProgramLabel(String text){
        programLabel.setText(text);
    }

    public void populate(){
        populateOutput();
        populateExecutionStack();
        populateProgramIDs();
        populateFileTable();
        populateSymbolTable();
        populateHeap();
        setNumberTextField();
    }

    private void populateExecutionStack(){
        executionStack.getItems().clear();
        StackInterface<StatementInterface> exeStack = statementController.getExecutionStackByID(statementID);
        List<String> executionString = new ArrayList<>();

        for(StatementInterface exeStatement: exeStack.getAll()){
            executionString.add(exeStatement.toString());
        }

        executionStack.getItems().addAll(executionString);
    }

    private void populateOutput(){
        output.getItems().clear();
        ListInterface<ValueInterface> out = statementController.getOutByID(statementID);
        List<String> outString = new ArrayList<>();

        for(ValueInterface v: out.getAll()){
            outString.add(v.toString());
        }

        //System.out.println("Out String: " + outString);

        output.getItems().addAll(outString);
    }


    private void populateProgramIDs(){
        programStateID.getItems().clear();
        List<Integer> programStates = statementController.getIDs();
        programIDNumber = statementController.getIDs().size();
        programStateID.getItems().addAll(programStates.stream().map(Object::toString).collect(Collectors.toList()));
    }

    private void populateFileTable(){
        fileTable.getItems().clear();
        DictInterface<StringValue, BufferedReader> file = statementController.getFileTableByID(statementID);
        List<String> fileString = new ArrayList<>();

        for(Map.Entry<StringValue, BufferedReader> e : file.getContent().entrySet()){
            fileString.add(e.getValue().toString());
        }
        fileTable.getItems().addAll(fileString);
    }

    private void populateSymbolTable(){
        symbolTable.getItems().clear();
        DictInterface<String, ValueInterface> symTable = statementController.getSymbolTableByID(statementID);
        List<Pair<String, ValueInterface>> symString = new ArrayList<>();

        for(Map.Entry<String, ValueInterface> e: symTable.getContent().entrySet()){
            symString.add(new Pair<>(e.getKey(), e.getValue()));
        }

        symbolTable.getItems().addAll(symString);

    }

    private void populateHeap(){
        heapTable.getItems().clear();
        HeapInterface<ValueInterface> heaptbl = statementController.getHeapTableByID(statementID);
        List<Pair<Integer, ValueInterface>> heapString = new ArrayList<>();

        for(Map.Entry<Integer, ValueInterface> e: heaptbl.getContent().entrySet()){
            heapString.add(new Pair<>(e.getKey(), e.getValue()));
        }

        heapTable.getItems().addAll(heapString);
    }

    private void oneStepOverall(){
        //System.out.println("hey?");
            oneStep();
            if(populateFlag == 1)
             populate();
    }

    private void oneStep(){
        try{
            List<ProgramState> prglist = statementController.removeCompletedPrograms(statementController.getProgramList());
            System.out.println("ID list " + statementController.getIDs());
            System.out.println("Current program ID: " + statementController.getCurrentID());

            int selected_id;
            if(programStateID.getSelectionModel().getSelectedItem() == null)
                selected_id = -1;
            else
                selected_id = parseInt(programStateID.getSelectionModel().getSelectedItem().toString());

            if(selected_id == -1)
                statementID = statementController.getCurrentID();
            else
                statementID = selected_id;

            System.out.println("Selected id: " + selected_id);

            if(prglist.size() > 0){
                statementController.conservativeGarbageCollector(prglist);
                statementController.oneStepOverall(prglist);
                prglist = statementController.removeCompletedPrograms(statementController.getProgramList());

            }
            else{
                System.out.println("Over!!");
                statementController.closeExecutor();
                statementController.setProgramList(prglist);
                populateFlag = 0;
                notify("Execution finished!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void notify(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        //executeOneStepButton.setOnAction(actionEvent -> { executeOneStep(); });
        varNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<String, ValueInterface>, String>, ObservableValue<String>>() {
                                              @Override
                                              public ObservableValue<String> call(TableColumn.CellDataFeatures<Pair<String, ValueInterface>, String> pairStringCellDataFeatures) {
                                                  return new ReadOnlyObjectWrapper<>(pairStringCellDataFeatures.getValue().getKey());
                                              }
                                          });
        valueColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<String, ValueInterface>, ValueInterface>, ObservableValue<ValueInterface>>() {
            @Override
            public ObservableValue<ValueInterface> call(TableColumn.CellDataFeatures<Pair<String, ValueInterface>, ValueInterface> pairValueInterfaceCellDataFeatures) {
                ValueInterface value = pairValueInterfaceCellDataFeatures.getValue().getValue();
                if (value instanceof ObservableValue) {
                    return (ObservableValue) value;
                } else {
                    return new ReadOnlyObjectWrapper<>(value);
                }
            }
        });

        addressColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<Integer, ValueInterface>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Pair<Integer, ValueInterface>, Integer> pairIntegerCellDataFeatures) {
                Integer value = pairIntegerCellDataFeatures.getValue().getKey();
                return new ReadOnlyObjectWrapper<>(value);
            }
        });

        heapValueColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<Integer, ValueInterface>, ValueInterface>, ObservableValue<ValueInterface>>() {
            @Override
            public ObservableValue<ValueInterface> call(TableColumn.CellDataFeatures<Pair<Integer, ValueInterface>, ValueInterface> pairValueInterfaceCellDataFeatures) {
                ValueInterface value = pairValueInterfaceCellDataFeatures.getValue().getValue();
                if (value instanceof ObservableValue) {
                    return (ObservableValue) value;
                } else {
                    return new ReadOnlyObjectWrapper<>(value);
                }
            }
        });

        runButton.setOnAction(actionEvent -> {
                    oneStepOverall();
                });

    }




}
