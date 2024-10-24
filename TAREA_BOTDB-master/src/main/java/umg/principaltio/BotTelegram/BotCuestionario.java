package umg.principaltio.BotTelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import umg.principaltio.model.User;
import umg.principaltio.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BotCuestionario extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Fabianlobos_bot";
    }

    @Override
    public String getBotToken() {
        return "7441021485:AAFThD97fDHz5WSaKUq_bz6yOtoz9p7bX04";
    }


        private Map<Long, String> estadoConversacion = new HashMap<>();
        private Map<Long, String> seccionActiva = new HashMap<>();
        private Map<Long, Integer> indicePregunta = new HashMap<>();
        private UserService userService = new UserService();
        private User respuestaService;

        private Map<String, String[]> preguntas = new HashMap<String, String[]>() {{
            put("SECTION_1", new String[]{"Pregunta 1 de Sección 1", "Pregunta 2 de Sección 1", "Pregunta 3 de Sección 1"});
            put("SECTION_2", new String[]{"Pregunta 1 de Sección 2", "Pregunta 2 de Sección 2", "Pregunta 3 de Sección 2"});
            put("SECTION_3", new String[]{"Pregunta 1 de Sección 3", "Pregunta 2 de Sección 3", "Pregunta 3 de Sección 3"});
            put("SECTION_4", new String[]{"Pregunta 1 de Sección 4", "¿Cuál es tu edad?", "Pregunta 3 de Sección 4"});
        }};

        public BotCuestionario(UserService userService, User respuestaService) {
            this.userService = userService;
            this.respuestaService = respuestaService;
        }

        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                String userFirstName = update.getMessage().getFrom().getFirstName();
                String userLastName = update.getMessage().getFrom().getLastName();
                String nickName = update.getMessage().getFrom().getUserName();

                String state = estadoConversacion.getOrDefault(chatId, "");
                User usuarioConectado = userService.getUserByTelegramId(chatId);

                if (usuarioConectado == null && state.isEmpty()) {
                    sendText(chatId, "Hola " + formatUserInfo(userFirstName, userLastName, nickName) + ", no tienes un usuario registrado en el sistema. Por favor ingresa tu correo electrónico:");
                    estadoConversacion.put(chatId, "ESPERANDO_CORREO");
                } else if (state.equals("ESPERANDO_CORREO")) {
                    processEmailInput(chatId, messageText);
                } else if (messageText.equals("/menu")) {
                    sendMenu(chatId);
                } else if (seccionActiva.containsKey(chatId)) {
                    manejaCuestionario(chatId, messageText);
                } else {
                    sendText(chatId, "Hola " + formatUserInfo(userFirstName, userLastName, nickName) + ", envía /menu para iniciar el cuestionario.");
                }
            } else if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                inicioCuestionario(chatId, callbackData);
            }
        }

        private void processEmailInput(long chatId, String email) {
            sendText(chatId, "Recibo su Correo: " + email);
            estadoConversacion.remove(chatId);
            User usuarioConectado = userService.getUserByEmail(email);

            if (usuarioConectado == null) {
                sendText(chatId, "El correo no se encuentra registrado en el sistema, por favor contacte al administrador.");
            } else {
                usuarioConectado.setTelegramid(chatId);
                userService.updateUser(usuarioConectado);
                sendText(chatId, "Usuario actualizado con éxito! Envía /menu para iniciar el cuestionario.");
            }
        }

        private void sendMenu(long chatId) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Selecciona una sección:");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            rows.add(crearFilaBoton("Sección 1", "SECTION_1"));
            rows.add(crearFilaBoton("Sección 2", "SECTION_2"));
            rows.add(crearFilaBoton("Sección 3", "SECTION_3"));
            rows.add(crearFilaBoton("Sección 4", "SECTION_4"));

            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private List<InlineKeyboardButton> crearFilaBoton(String text, String callbackData) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(text);
            button.setCallbackData(callbackData);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            return row;
        }

        private void inicioCuestionario(long chatId, String section) {
            seccionActiva.put(chatId, section);
            indicePregunta.put(chatId, 0);
            enviarPregunta(chatId);
        }

        private void enviarPregunta(long chatId) {
            String seccion = seccionActiva.get(chatId);
            int index = indicePregunta.get(chatId);
            String[] questions = preguntas.get(seccion);

            if (index < questions.length) {
                sendText(chatId, questions[index]);
            } else {
                sendText(chatId, "¡Has completado el cuestionario!");
                seccionActiva.remove(chatId);
                indicePregunta.remove(chatId);
            }
        }

        private void manejaCuestionario(long chatId, String response) {
            String section = seccionActiva.get(chatId);
            int index = indicePregunta.get(chatId);

            if (section.equals("SECTION_4") && index == 1) {
                if (!validarEdad(response)) {
                    sendText(chatId, "Por favor, ingresa una edad válida (número entre 1 y 120).");
                    return;
                }
            }

            Respuesta respuesta = new Respuesta();
            respuesta.setTelegramId(chatId);
            respuesta.setSeccion(section);
            respuesta.setPreguntaId(index);
            respuesta.setRespuestaTexto(response);
            respuestaService.saveRespuesta(respuesta);

            sendText(chatId, "Tu respuesta fue: " + response);
            indicePregunta.put(chatId, index + 1);

            enviarPregunta(chatId);
        }

        private boolean validarEdad(String edad) {
            try {
                int edadNum = Integer.parseInt(edad);
                return edadNum > 0 && edadNum <= 120;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private void sendText(Long chatId, String text) {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .build();
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private String formatUserInfo(String firstName, String lastName, String userName) {
            return firstName + " " + lastName + " (" + userName + ")";
        }

        // Métodos getToken y getBotUsername omitidos por brevedad
    }
