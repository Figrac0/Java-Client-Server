- **Java 17**
- **JavaFX** — GUI framework  
- **Socket API (TCP)** — for client-server communication  
- **SQLite** — local database for storing users and statistics  
- **Gson** — for serializing/deserializing JSON data  
- **Maven** — project build and dependency management  
- **FXML** — UI layout definition  
- **PlantUML** — diagrams (flow, activity, component)

  ***🧠 Socket Communication***
- Protocol: TCP
- Client sends commands (LOGIN, REGISTER, ADD_VICTORY, GET_STATS, etc.)
- Server responds with plain text or JSON (for statistics)
- Managed using DataInputStream / DataOutputStream
<br/>
🗂️ Database <br/>
-SQLite file: tictactoe.db
-Tables:<br/>
users(login TEXT, password TEXT, played INT, won INT, rating INT)

