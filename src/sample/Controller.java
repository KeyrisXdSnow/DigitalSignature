package sample;

import Hesh.SHA1;
import fileWorker.FileWorker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.math.BigInteger;
import java.util.regex.Pattern;
import signature.DigitalSignature;
import signature.PrimeNumber;
import static sample.Main.fileChooser;
import static sample.Main.stage;


public class Controller {

    private File file = null ;
    private int q_height = 21, p_height = 140, b_height = 250;
    private boolean q_simple = false, p_simple = false, b_simple = false ;
    private String regEx = "\\d*";
    private Pattern pattern ;
    private Alert alert ;

    @FXML
    private MenuItem chooseFile,lFilePath;
    @FXML
    private TextArea text_q,text_p,text_b;
    @FXML
    private Label l_bad,l_good,q_ok,p_ok,b_ok, q_bad, p_bad, b_bad, lHesh,lESP, lESP1;
    @FXML
    private Button bGo;
    @FXML
    private RadioButton rbCheckSign, rbCreateSign;


    @FXML
    void initialize() {

        chooseFile.setOnAction( mouseEvent -> {
           file = fileChooser.showOpenDialog(stage);
           lFilePath.setText(file.toString());

           text_q.setText("");
           text_p.setText("");
           text_b.setText("");
           text_b.setDisable(true);
           l_bad.setVisible(false);
           l_good.setVisible(false);
           q_ok.setVisible(false);
           q_bad.setVisible(false);
            b_ok.setVisible(false);
            b_bad.setVisible(false);
            p_ok.setVisible(false);
            p_bad.setVisible(false);
       });

        rbCreateSign.setOnAction( actionEvent -> rbCheckSign.setSelected(false));
        rbCheckSign.setOnAction( actionEvent -> rbCreateSign.setSelected(false));

        text_q.textProperty().addListener((observableValue, oldValue, newValue) -> {

            l_bad.setPrefHeight(q_height);
            l_good.setPrefHeight(q_height);
            q_ok.setVisible(false);
            q_bad.setVisible(false);
            pattern = Pattern.compile(regEx) ;

            if (!pattern.matcher(newValue).matches() ) text_q.setText(oldValue);
            else q_simple = showTextCondition ("q", text_q);

        } );
        text_q.setOnMouseEntered(mouseEvent -> {
            l_good.setVisible(false);
            l_bad.setVisible(false);
//            l_bad.setPrefHeight(q_height);
//            l_good.setPrefHeight(q_height);
            q_ok.setVisible(false);
            q_bad.setVisible(false);
        });
        text_q.setOnMouseExited(mouseEvent -> {
            if ( q_simple ) {
                l_good.setVisible(false);
                l_bad.setVisible(false);
                q_ok.setVisible(true);
            }
            else  if ( text_q.getLength() != 0) {
                q_bad.setVisible(true);
                l_good.setVisible(false);
                l_bad.setVisible(false);
            }

        });

        text_p.textProperty().addListener((observableValue, oldValue, newValue) -> {

            l_bad.setPrefHeight(p_height);
            l_good.setPrefHeight(p_height);
            p_ok.setVisible(false);
            p_bad.setVisible(false);

            pattern = Pattern.compile(regEx) ;

            if (!pattern.matcher(newValue).matches() ) text_p.setText(oldValue);
            else p_simple = showTextCondition("p", text_p);

        } );
        text_p.setOnMouseEntered(mouseEvent -> {
            l_good.setVisible(false);
            l_bad.setVisible(false);
//            l_bad.setPrefHeight(p_height);
//            l_good.setPrefHeight(p_height);
            p_bad.setVisible(false);
            p_ok.setVisible(false);
        });
        text_p.setOnMouseExited(mouseEvent -> {
            if ( p_simple ) {
                p_ok.setVisible(true);
                l_good.setVisible(false);
                l_bad.setVisible(false);

            }
            else  if ( text_p.getLength() != 0) {
                l_good.setVisible(false);
                l_bad.setVisible(false);
                p_bad.setVisible(true);
            }

        });

        text_b.textProperty().addListener((observableValue, oldValue, newValue) -> {

            l_bad.setPrefHeight(b_height);
            l_good.setPrefHeight(b_height);
            b_ok.setVisible(false);
            b_bad.setVisible(false);

            pattern = Pattern.compile(regEx) ;
             l_bad.setPrefHeight(b_height);
             l_good.setPrefHeight(b_height);

            if (!pattern.matcher(newValue).matches() ) text_b.setText(oldValue);
            else {
                BigInteger echi =  new BigInteger(
                        text_q.getText()).subtract(BigInteger.ONE).multiply(
                        new BigInteger(text_q.getText()).subtract(BigInteger.ONE));
                if  (text_b.getLength() != 0 ) {
                    if ( new BigInteger(text_b.getText()).compareTo(BigInteger.TWO) >= 0 &&
                         new BigInteger(text_b.getText()).compareTo(echi) <= 0) {

                        if ( echi.gcd(new BigInteger(text_b.getText())).compareTo(BigInteger.ONE) == 0) {
                            b_ok.setVisible(true);
                            b_bad.setVisible(false);
                            l_bad.setVisible(false);
                            b_simple = true;
                        } else {
                            l_bad.setVisible(true);
                            l_bad.setText (" b должно быть мульт. инв. с ф. Эйлера(p*q)") ;
                            b_simple = false ;
                        }
                    } else {
                        l_bad.setVisible(true);
                        l_bad.setText (" b должно быть >= 2 и <= ф. Эйлера(p*q)") ;
                        b_simple = false ;
                    }
                } else {
                    l_good.setVisible(false);
                    l_bad.setVisible(false);
                    b_ok.setVisible(false);
                    b_bad.setVisible(false);
                }
            }
        } );

        text_b.setOnMouseExited( mouseEvent -> {////
            if  (text_b.getLength() != 0 && !text_b.isDisable()  )
            if (b_simple) {
                b_ok.setVisible(true);
                b_bad.setVisible(false);
                l_good.setVisible(false);
            } else {
                b_ok.setVisible(false);
                b_bad.setVisible(true);
                l_bad.setVisible(false);
            }
        });
///////////////////////////////////////////////////////////////
        bGo.setOnAction( actionEvent -> {

          if ( file != null && q_ok.isVisible() && p_ok.isVisible() && b_ok.isVisible()) {
              DigitalSignature digitalSignature = new DigitalSignature(
                      new BigInteger(text_q.getText()), new BigInteger(text_p.getText()),
                      new BigInteger(text_b.getText()));
              SHA1 sha1 = new SHA1();
              FileWorker fileWorker = new FileWorker(file);
              byte[] text = fileWorker.readFile();
              BigInteger hesh = BigInteger.ZERO, actualHesh = BigInteger.ZERO;

              if (rbCreateSign.isSelected()) {

                  hesh = digitalSignature.createSignature(sha1.getIntHash(text));
                  fileWorker.writeFile((String.valueOf(hesh)));

              } else {
                  if (digitalSignature.checkSignature(fileWorker.readSignFile(),
                          fileWorker.getSign())) {
                      hesh = digitalSignature.getM();
                      actualHesh = digitalSignature.getM1();
                      alert = new Alert(Alert.AlertType.INFORMATION, " Цифровая подпись действительна");
                  } else alert = new Alert(Alert.AlertType.ERROR, " Цифровая подпись не совпадает");

                  alert.show();

              }

              lHesh.setText(" Хеш сообщения : " + sha1.getHash(text));
              if (hesh.compareTo(BigInteger.ZERO) == 0) lESP.setText(" ЭЦП : - ");
              else lESP.setText(" ЭЦП : " + hesh);
              if (actualHesh.compareTo(BigInteger.ZERO) != 0) lESP1.setText(" ЭЦП' : " + actualHesh);

          } else {
              alert = new Alert(Alert.AlertType.ERROR,"ВВедены некорректные данные");
              alert.show();
          }
        });

    }

    private boolean showTextCondition (String value, TextArea textArea) {

        boolean simple = false ;

        if (textArea.getLength() != 0) {
            if (PrimeNumber.cheсkSimplicity( new BigInteger (textArea.getText())) ) {
                    l_bad.setVisible(false);
                    l_good.setVisible(true);
                    l_good.setText(" "+value+" простое" );
                    simple = true ;
            } else {
                l_good.setVisible(false);
                l_bad.setVisible(true);
                l_bad.setText(" "+value+" не простое");
            }
        } else {
            l_bad.setVisible(false);
            l_good.setVisible(false);
        }

        if ( ( q_ok.isVisible() && l_good.getHeight() == p_height && !l_bad.isVisible() ) ^
                ( p_ok.isVisible() && l_good.getHeight() == q_height && !l_bad.isVisible() )) text_b.setDisable(false);
        else {
            text_b.setText("");
            b_ok.setVisible(false);
            b_bad.setVisible(false);
            text_b.setDisable(true);
        }

        return  simple;
    }

}
