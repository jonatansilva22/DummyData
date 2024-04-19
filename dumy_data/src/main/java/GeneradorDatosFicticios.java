import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.swing.*;
import com.github.javafaker.Faker;

public class GeneradorDatosFicticios extends JFrame {
    private JTextField campoCantidad;
    private JTextArea areaRegistro;
    private JButton botonGenerar, botonExportar;
    private Faker generadorFaker;
    private Random aleatorio;
    private SimpleDateFormat formatoFecha;
    private List<String[]> datos = new ArrayList<>();
    private String[] apellidosEspanoles = {"García", "Fernández", "Rodríguez", "González", "López"};
    private String[] apellidosIngleses = {"O'Neill", "Smith", "Johnson", "D’Angelo", "McDonald"};
    private String[] apellidosFranceses = {"Lefèvre", "Dupont", "Durand", "Fouquet", "Bélanger"};
    private String[] apellidosAlemanes = {"Müller", "Schneider", "Fischer", "Weiß", "Köhler"};
    private List<String[]> listasApellidos = new ArrayList<>();

    public GeneradorDatosFicticios() {
        super("Generador de Datos Ficticios");
        this.generadorFaker = new Faker(new Locale("es", "ES"));
        this.aleatorio = new Random();
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        listasApellidos.add(apellidosEspanoles);
        listasApellidos.add(apellidosIngleses);
        listasApellidos.add(apellidosFranceses);
        listasApellidos.add(apellidosAlemanes);
        crearUI();
    }

    private void crearUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.add(new JLabel("Número de Registros:"));
        campoCantidad = new JTextField(10);
        panel.add(campoCantidad);

        botonGenerar = new JButton("Generar");
        botonGenerar.addActionListener(this::generarDatos);
        panel.add(botonGenerar);

        botonExportar = new JButton("Exportar a SQL");
        botonExportar.addActionListener(this::exportarDatosSQL);
        panel.add(botonExportar);

        add(panel, BorderLayout.NORTH);

        areaRegistro = new JTextArea();
        areaRegistro.setEditable(false);  // Asegura que el usuario no pueda editar el contenido
        JScrollPane panelDesplazamiento = new JScrollPane(areaRegistro);
        add(panelDesplazamiento, BorderLayout.CENTER);

        setVisible(true);
    }

    private void generarDatos(ActionEvent event) {
        int cantidad = Integer.parseInt(campoCantidad.getText().trim());
        cantidad = Math.max(1, Math.min(50000, cantidad));
        datos.clear();
        areaRegistro.setText(""); // Limpiar la caja de texto antes de agregar nuevos datos

        StringBuilder registros = new StringBuilder();

        for (int i = 0; i < cantidad; i++) {
            String matricula = generarMatricula();
            String apellido1 = seleccionarApellidoAleatorio();
            String apellido2 = seleccionarApellidoAleatorio();
            String nombres = generadorFaker.name().firstName();
            Date fecha = generadorFaker.date().birthday(18, 25);
            String fechaNacimiento = formatoFecha.format(fecha);
            String correo = generarCorreo();

            String registro = String.format("Matrícula: %s, Apellido1: %s, Apellido2: %s, Nombres: %s, Correo: %s, Fecha Nacimiento: %s\n",
                    matricula, apellido1, apellido2, nombres, correo, fechaNacimiento);
            registros.append(registro);
        }

        areaRegistro.setText(registros.toString());
    }

    private String seleccionarApellidoAleatorio() {
        int listaIndex = aleatorio.nextInt(listasApellidos.size());
        String[] listaSeleccionada = listasApellidos.get(listaIndex);
        return listaSeleccionada[aleatorio.nextInt(listaSeleccionada.length)];
    }

    private String generarCorreo() {
        int numeroAleatorio = aleatorio.nextInt(10000000); // Genera un número aleatorio de 7 dígitos
        return String.format("A22%07d@unison.mx", numeroAleatorio);
    }

    private String generarMatricula() {
        int numeroAleatorio = aleatorio.nextInt(10000000); // Genera un número aleatorio de 7 dígitos
        return String.format("22%07d", numeroAleatorio);
    }

    private void exportarDatosSQL(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write("INSERT INTO usuarios (matricula, apellido1, apellido2, nombres, correo, fecha_nacimiento) VALUES");
                writer.newLine();
                for (int i = 0; i < datos.size(); i++) {
                    String[] registro = datos.get(i);
                    String sqlValue = String.format("('%s', '%s', '%s', '%s', '%s', '%s')",
                                                    registro[0], registro[1], registro[2], registro[3], registro[4], registro[5]);
                    if (i < datos.size() - 1) sqlValue += ",";
                    else sqlValue += ";";
                    writer.write(sqlValue);
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Datos exportados en SQL exitosamente a " + fileToSave.getAbsolutePath(), "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new GeneradorDatosFicticios();
    }
}
