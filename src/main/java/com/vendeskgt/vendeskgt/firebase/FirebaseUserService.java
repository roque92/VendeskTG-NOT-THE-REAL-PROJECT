package com.vendeskgt.vendeskgt.firebase;

import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ListUsersPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseUserService {

    private final FirebaseConfig firebaseConfig;

    @Autowired
    public FirebaseUserService(FirebaseConfig firebaseConfig) {
        this.firebaseConfig = firebaseConfig;
    }

    // Asegura que FirebaseAuth esté inicializado
    private FirebaseAuth getFirebaseAuth() throws Exception {
        try {
            return FirebaseAuth.getInstance();
        } catch (IllegalStateException e) {
            firebaseConfig.firebaseApp();
            return FirebaseAuth.getInstance();
        }
    }

    // Lista todos los usuarios y los retorna
    public List<Map<String, String>> listAllUsers() throws Exception {
        List<Map<String, String>> allUsers = new ArrayList<>();
        ListUsersPage page = getFirebaseAuth().listUsers(null);
        while (page != null) {
            for (ExportedUserRecord user : page.getValues()) {
                allUsers.add(Map.of(
                        "uid", user.getUid(),
                        "email", user.getEmail(),
                        "disabled", String.valueOf(user.isDisabled())
                ));
            }
            page = page.getNextPage();
        }
        return allUsers;
    }

    // Borra todos los usuarios
    public void deleteAllUsers() throws Exception {
        ListUsersPage page = getFirebaseAuth().listUsers(null);
        while (page != null) {
            for (ExportedUserRecord user : page.getValues()) {
                try {
                    getFirebaseAuth().deleteUser(user.getUid());
                    System.out.println("Usuario eliminado: " + user.getEmail());
                } catch (Exception e) {
                    System.err.println("Error eliminando usuario " + user.getEmail() + ": " + e.getMessage());
                }
            }
            page = page.getNextPage();
        }
        System.out.println("✅ Todos los usuarios eliminados");
    }

    // Método combinado: lista primero y luego borra
    public List<Map<String, String>> listAndDeleteAllUsers() throws Exception {
        System.out.println("📋 Listando todos los usuarios:");
        List<Map<String, String>> users = listAllUsers();
        for (Map<String, String> user : users) {
            System.out.println(user);
        }
        System.out.println("🗑 Eliminando todos los usuarios...");
        deleteAllUsers();
        return users;
    }
}
