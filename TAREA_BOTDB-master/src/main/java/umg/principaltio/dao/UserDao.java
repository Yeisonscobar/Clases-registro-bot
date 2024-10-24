package umg.principaltio.dao;

import umg.principaltio.db.DatabaseConnection;
import umg.principaltio.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public void deleteUserByEmail(String email) throws SQLException {
        String query = "DELETE FROM  tb_respuestas WHERE correo = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.executeUpdate();
        }
    }



    public void updateUser(User user) throws SQLException {
        String query = "UPDATE tb_respuestas SET seccion = ?, telegram_id = ?, pregunta_id = ?, respuesta_texto = ?, fecha_respuesta = ?, correo = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getSeccion());
            statement.setLong(2, user.getTelegramId());
            statement.setInt(3, user.getPreguntaId());
            statement.setString(4, user.getRespuestaTexto());
            statement.setDate(5, user.getFechaRespuesta());
            statement.setString(6, user.getCorreo());
            statement.setInt(7, user.getId());
            statement.executeUpdate();
        }
    }



    public void insertUser(User user) throws SQLException {
        String query = "INSERT INTO tb_respuestas (seccion, telegram_id, pregunta_id, respuesta_texto, correo) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getSeccion());
            statement.setLong(2, user.getTelegramId());
            statement.setInt(3, user.getPreguntaId());
            statement.setString(4, user.getRespuestaTexto());
            statement.setString(5, user.getCorreo());
            statement.executeUpdate();
        }
    }

    public User getUserByTelegramId(long telegramid) throws SQLException {
        String query = "SELECT * FROM tb_respuestas WHERE telegram_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, telegramid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setSeccion(resultSet.getString("seccion"));
                user.setTelegramId(resultSet.getInt("telegram_id"));
                user.setPreguntaId(resultSet.getInt("pregunta_id"));
                user.setRespuestaTexto(resultSet.getString("respuesta_texto"));
                user.setCorreo(resultSet.getString("correo"));

                return user;
            }
        }
        return null;
    }


    public User getUserByEmail(String Email) throws SQLException {
        String query = "SELECT * FROM tb_respuestas WHERE correo = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setSeccion(resultSet.getString("seccion"));
                user.setTelegramId(resultSet.getInt("telegram_id"));
                user.setPreguntaId(resultSet.getInt("pregunta_id"));
                user.setRespuestaTexto(resultSet.getString("respuesta_texto"));
                user.setCorreo(resultSet.getString("correo"));

                return user;
            }
        }
        return null;
    }

    public User getUserByid (int id ) throws SQLException {
        String query = "SELECT * FROM tb_respuestas WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setSeccion(resultSet.getString("seccion"));
                user.setTelegramId(resultSet.getInt("telegram_id"));
                user.setPreguntaId(resultSet.getInt("pregunta_id"));
                user.setRespuestaTexto(resultSet.getString("respuesta_texto"));
                user.setCorreo(resultSet.getString("correo"));
                return user;
            }
        }
        return null;
    }

//    public User getUserBycarne (String carne ) throws SQLException {
//        String query = "SELECT * FROM tb_usuarios WHERE carne = ?";
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, carne);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                User user = new User();
//                user.setId(resultSet.getInt("idusuario"));
//                user.setCarne(resultSet.getString("carne"));
//                user.setNombre(resultSet.getString("nombre"));
//                user.setCorreo(resultSet.getString("correo"));
//                user.setSeccion(resultSet.getString("seccion"));
//                user.setTelegramid(resultSet.getLong("telegramid"));
//                user.setActivo(resultSet.getString("activo"));
//                return user;
//            }
//        }
//        return null;
//    }
}
