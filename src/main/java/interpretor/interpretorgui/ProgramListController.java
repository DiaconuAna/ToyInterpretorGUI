package interpretor.interpretorgui;


import Controller.Controller;
import Exceptions.ADTsExceptions;
import Exceptions.ExpressionException;
import Exceptions.StatementException;
import Model.Expression.*;
import Model.Statements.*;
import Model.Types.BoolType;
import Model.Types.IntType;
import Model.Types.ReferenceType;
import Model.Types.StringType;
import Model.Value.BoolValue;
import Model.Value.IntValue;
import Model.Value.StringValue;
import Repository.RepositoryInterface;
import Repository.Repository;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ProgramListController implements Initializable {

    private List<StatementInterface> statements;

    private String currentProgramID;

    @FXML
    private ListView<String> programListView;

    @FXML
    private Label SelectedProgramID;

    @FXML
    private Button loadButton;

    @FXML
    private AnchorPane sasha;

    @FXML
    private Button exitButton;

    private Stage stage;

    public void logout(ActionEvent event){
        stage = (Stage) sasha.getScene().getWindow();
        System.out.println("Goodbye!");
        stage.close();
    }

    int getStatementID(String statement){

        for(int i=0;i<statements.size();i++){
            String st = statements.get(i).toString();
            if(statement.compareTo(st) == 0)
                return i;
        }
        return -1;
    }

    public void createStatements(){
        StatementInterface statement1 = new CompoundStatement(new VarDeclStatement("v", new IntType()),
                new CompoundStatement(new AssignmentStatement("v", new ValueExpression(new IntValue(2))),
                        new PrintStatement(new VariableExpression("v"))));

        StatementInterface statement2 = new CompoundStatement(new VarDeclStatement("a", new IntType()),
                new CompoundStatement(new VarDeclStatement("b", new IntType()),
                        new CompoundStatement(new AssignmentStatement("a", new ArithmeticExpression('+', new ValueExpression(new IntValue(2)),
                                new ArithmeticExpression('*', new ValueExpression(new IntValue(3)), new ValueExpression(new IntValue(5))))),
                                new CompoundStatement(new AssignmentStatement("b", new ArithmeticExpression('+', new VariableExpression("a"),
                                        new ValueExpression(new IntValue(1)))), new PrintStatement(new VariableExpression("b"))))));

        StatementInterface statement3 = new CompoundStatement(new VarDeclStatement("a", new BoolType()),
                new CompoundStatement(new VarDeclStatement("v", new IntType()),
                        new CompoundStatement(new AssignmentStatement("a", new ValueExpression(new BoolValue(true))),
                                new CompoundStatement(new IfStatement(new VariableExpression("a"), new AssignmentStatement("v", new ValueExpression(new IntValue(2))),
                                        new AssignmentStatement("v", new ValueExpression(new IntValue(3)))), new PrintStatement(new VariableExpression("v"))))));

        StatementInterface statement4 = new CompoundStatement(new VarDeclStatement("varf", new StringType()),
                new CompoundStatement(new AssignmentStatement("varf", new ValueExpression(new StringValue("test.in"))),
                        new CompoundStatement(new OpenReadFileStatement(new VariableExpression("varf")),
                                new CompoundStatement(new VarDeclStatement("varc", new IntType()),
                                        new CompoundStatement(new ReadFile(new VariableExpression("varf"), "varc"),
                                                new CompoundStatement(new PrintStatement(new VariableExpression("varc")),
                                                        new CompoundStatement(new ReadFile(new VariableExpression("varf"), "varc"),
                                                                new CompoundStatement(new PrintStatement(new VariableExpression("varc")), new CloseReadFile(new VariableExpression("varf")))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        //Ref int v;new(v,20);Ref Ref int a; new(a,v);print(v);print(a)

        StatementInterface statement5 = new CompoundStatement(new VarDeclStatement("v", new ReferenceType((new IntType()))),
                new CompoundStatement(new HeapAllocationStatement("v", new ValueExpression(new IntValue(20))),
                        new CompoundStatement(new VarDeclStatement("a", new ReferenceType(new ReferenceType(new IntType()))),
                                new CompoundStatement(new HeapAllocationStatement("a", new VariableExpression("v")),
                                        new CompoundStatement(new PrintStatement(new VariableExpression("v")), new PrintStatement(new VariableExpression("a")))))));

        //Ref int v;new(v,20);Ref Ref int a; new(a,v);print(rH(v));print(rH(rH(a))+5)

        StatementInterface statement6 = new CompoundStatement(new VarDeclStatement("v", new ReferenceType(new IntType())),
                new CompoundStatement(new HeapAllocationStatement("v", new ValueExpression(new IntValue(20))),
                        new CompoundStatement(new VarDeclStatement("a", new ReferenceType(new ReferenceType(new IntType()))),
                                new CompoundStatement(new HeapAllocationStatement("a", new VariableExpression("v")),
                                        new CompoundStatement(new PrintStatement(new HeapReadingExpression(new VariableExpression("v"))),
                                                new PrintStatement((new ArithmeticExpression('+', new HeapReadingExpression(
                                                        new HeapReadingExpression(new VariableExpression("a"))),
                                                        new ValueExpression(new IntValue(5))))))))));

        //Ref int v;new(v,20);print(rH(v)); wH(v,30);print(rH(v)+5);
        StatementInterface statement7 = new CompoundStatement(new VarDeclStatement("v",new ReferenceType(new IntType())),
                new CompoundStatement(new HeapAllocationStatement("v", new ValueExpression(new IntValue(20))),
                        new CompoundStatement(new PrintStatement(new HeapReadingExpression(new VariableExpression("v"))),
                                new CompoundStatement(new HeapWritingStatement("v", new ValueExpression(new IntValue(30))),
                                        new PrintStatement(new ArithmeticExpression
                                                ('+',new HeapReadingExpression(new VariableExpression("v")), new ValueExpression(new IntValue(5))))))));

        //Ref int v;new(v,20);Ref Ref int a; new(a,v); new(v,30);print(rH(rH(a)))
        StatementInterface statement8 = new CompoundStatement(new VarDeclStatement("v",new ReferenceType(new IntType())),
                new CompoundStatement(new HeapAllocationStatement("v", new ValueExpression(new IntValue(20))),
                        new CompoundStatement(new VarDeclStatement("a", new ReferenceType(new ReferenceType(new IntType()))),
                                new CompoundStatement(new HeapAllocationStatement("a", new VariableExpression("v")),
                                        new CompoundStatement(new HeapAllocationStatement("v", new ValueExpression(new IntValue(30))),
                                                new PrintStatement(new HeapReadingExpression(new HeapReadingExpression(new VariableExpression("a")))))))));

        //int v; v=4; (while (v>0) print(v);v=v-1);print(v)
        StatementInterface statement9 = new CompoundStatement(new VarDeclStatement("v", new IntType()),
                new CompoundStatement(new AssignmentStatement("v", new ValueExpression(new IntValue(4))),
                        new CompoundStatement(new WhileStatement(new RelationalExpression(">", new VariableExpression("v"), new ValueExpression(new IntValue(0))),
                                new CompoundStatement(new PrintStatement(new VariableExpression("v")), new AssignmentStatement("v",
                                        new ArithmeticExpression('-',new VariableExpression("v"), new ValueExpression(new IntValue(1)))))),
                                new PrintStatement(new VariableExpression("v")))));

        //int v; Ref int a; v=10;new(a,22);
        //fork(wH(a,30);v=32;print(v);print(rH(a)));
        //print(v);print(rH(a))
        StatementInterface statement10 = new CompoundStatement(new VarDeclStatement("v", new IntType()),
                new CompoundStatement(new VarDeclStatement("a",new ReferenceType(new IntType())),
                        new CompoundStatement(new AssignmentStatement("v", new ValueExpression(new IntValue(10))),
                                new CompoundStatement(new HeapAllocationStatement("a", new ValueExpression(new IntValue(22))),
                                        new CompoundStatement(new ForkStatement(new CompoundStatement(new HeapWritingStatement("a", new ValueExpression(new IntValue(30))),
                                                new CompoundStatement(new AssignmentStatement("v", new ValueExpression(new IntValue(32))),
                                                        new CompoundStatement(new PrintStatement(new VariableExpression("v")), new PrintStatement(new HeapReadingExpression(new VariableExpression("a"))))))),
                                                new CompoundStatement(new PrintStatement(new VariableExpression("v")),
                                                        new PrintStatement(new HeapReadingExpression(new VariableExpression("a")))))))));

        statements = new ArrayList<StatementInterface>();
        statements.add(statement1);
        statements.add(statement2);
        statements.add(statement3);
        statements.add(statement4);
        statements.add(statement5);
        statements.add(statement6);
        statements.add(statement7);
        statements.add(statement8);
        statements.add(statement9);
        statements.add(statement10);

    }

    public List<String> getStringStatements(){
        return statements.stream().map(str -> str.toString()).collect(Collectors.toList());
    }

    public StatementInterface getStatement(String statement){
        for(StatementInterface stmt: statements)
            if(statement.compareTo(stmt.toString()) == 0)
                return stmt;
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createStatements();
        List<String> stats = getStringStatements();
        final String[] currentProgram = new String[1];

        programListView.getItems().addAll(stats);
        programListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {

                String program_id = String.valueOf(getStatementID(programListView.getSelectionModel().getSelectedItem())+1);
                currentProgramID = program_id;
                currentProgram[0] = "Statement " + program_id + " : " + programListView.getSelectionModel().getSelectedItem();
                SelectedProgramID.setText(currentProgram[0]);

            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage = (Stage) sasha.getScene().getWindow();
                System.out.println("Goodbye!");
                stage.close();
            }
        });

        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String currentProgram;
                //String program_id = String.valueOf(getStatementID(programListView.getSelectionModel().getSelectedItem())+1);
                currentProgram = "Statement " + currentProgramID + " : " + programListView.getSelectionModel().getSelectedItem();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgramController.fxml"));
                try {
                    Parent root = loader.load();
                    ProgramController programController = loader.getController();
                    //System.out.println(currentProgram);
                    String file_name = "log" + currentProgramID + ".txt";
                    RepositoryInterface repository1 = new Repository(file_name);
                    Controller c = new Controller(repository1, true);
                    StatementInterface stmt = getStatement(programListView.getSelectionModel().getSelectedItem());
                    programController.setStatementController(c, stmt);
                    programController.setProgramLabel(currentProgram);

                    String css = this.getClass().getResource("Program.css").toExternalForm();
                    Image icon = new Image(getClass().getResource("web-programming.png").toExternalForm());


                    String title = "Statement " + currentProgramID;
                    Stage programWindow = new Stage();
                    programWindow.setTitle(title);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(css);
                    programWindow.getIcons().add(icon);
                    //scene.setFill(Color.ALICEBLUE);
                    programWindow.setScene(scene);
                    programWindow.show();

                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(e.toString());
                    alert.show();
                }

            }
        });


    }

    public void openProgramWindow(ActionEvent event){

    }
}
