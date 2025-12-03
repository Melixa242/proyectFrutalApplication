package com.example.proyectfrutalapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PulpasDB.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Usuarios
    private static final String TABLE_USERS = "usuarios";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "nombre";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PHONE = "telefono";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_ACTIVE = "activo";

    // Tabla Roles
    private static final String TABLE_ROLES = "roles";
    private static final String COL_ROLE_ID = "id";
    private static final String COL_ROLE_NAME = "nombre";
    private static final String COL_ROLE_DESCRIPTION = "descripcion";

    // Tabla Usuario-Rol (Relación muchos a muchos)
    private static final String TABLE_USER_ROLES = "usuario_roles";
    private static final String COL_UR_ID = "id";
    private static final String COL_UR_USER_ID = "usuario_id";
    private static final String COL_UR_ROLE_ID = "rol_id";

    // Tabla Productos
    private static final String TABLE_PRODUCTS = "productos";
    private static final String COL_PROD_ID = "id";
    private static final String COL_PROD_NAME = "nombre";
    private static final String COL_PROD_QUANTITY = "cantidad";
    private static final String COL_PROD_UNIT = "unidad";
    private static final String COL_PROD_FRACTION = "fraccion";
    private static final String COL_PROD_PRODUCTION_DATE = "fecha_produccion";
    private static final String COL_PROD_EXPIRY_DATE = "fecha_vencimiento";
    private static final String COL_PROD_NOTES = "observaciones";
    private static final String COL_PROD_USER_ID = "usuario_id";
    private static final String TABLE_SALES = "ventas";
    private static final String COL_SALE_ID = "id";
    private static final String COL_SALE_USER_ID = "usuario_id";
    private static final String COL_SALE_CUSTOMER_NAME = "nombre_cliente";
    private static final String COL_SALE_CUSTOMER_PHONE = "telefono_cliente";
    private static final String COL_SALE_CUSTOMER_ADDRESS = "direccion_cliente";
    private static final String COL_SALE_TOTAL = "total";
    private static final String COL_SALE_DATE = "fecha_venta";
    private static final String COL_SALE_STATUS = "estado";
    private static final String COL_SALE_NOTES = "notas";

    // Tabla Detalle de Ventas
    private static final String TABLE_SALE_DETAILS = "detalles_venta";
    private static final String COL_SD_ID = "id";
    private static final String COL_SD_SALE_ID = "venta_id";
    private static final String COL_SD_PRODUCT_ID = "producto_id";
    private static final String COL_SD_PRODUCT_NAME = "nombre_producto";
    private static final String COL_SD_QUANTITY = "cantidad";
    private static final String COL_SD_UNIT = "unidad";
    private static final String COL_SD_UNIT_PRICE = "precio_unitario";
    private static final String COL_SD_SUBTOTAL = "subtotal";

    private static final String COL_PROD_WHOLESALE_PRICE = "precio_mayor";
    private static final String COL_PROD_RETAIL_PRICE = "precio_menor";
    private static final String TABLE_CLIENTES = "clientes";
    private static final String COL_CLIENTE_ID = "id";
    private static final String COL_CLIENTE_NOMBRE = "nombre";
    private static final String COL_CLIENTE_TELEFONO = "telefono";
    private static final String COL_CLIENTE_DIRECCION = "direccion";
    private static final String COL_CLIENTE_EMAIL = "email";
    private static final String COL_CLIENTE_NOTAS = "notas";
    private static final String COL_CLIENTE_ACTIVO = "activo";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla Usuarios
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_ACTIVE + " INTEGER DEFAULT 1)";
        db.execSQL(createUsersTable);

        // Crear tabla Roles
        String createRolesTable = "CREATE TABLE " + TABLE_ROLES + " (" +
                COL_ROLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ROLE_NAME + " TEXT UNIQUE NOT NULL, " +
                COL_ROLE_DESCRIPTION + " TEXT)";
        db.execSQL(createRolesTable);

        // Crear tabla Usuario-Roles
        String createUserRolesTable = "CREATE TABLE " + TABLE_USER_ROLES + " (" +
                COL_UR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_UR_USER_ID + " INTEGER NOT NULL, " +
                COL_UR_ROLE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_UR_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_UR_ROLE_ID + ") REFERENCES " + TABLE_ROLES + "(" + COL_ROLE_ID + "), " +
                "UNIQUE(" + COL_UR_USER_ID + ", " + COL_UR_ROLE_ID + "))";
        db.execSQL(createUserRolesTable);

        // Crear tabla Productos
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PROD_NAME + " TEXT NOT NULL, " +
                COL_PROD_QUANTITY + " REAL, " +
                COL_PROD_UNIT + " TEXT, " +
                COL_PROD_FRACTION + " TEXT, " +
                COL_PROD_WHOLESALE_PRICE + " REAL DEFAULT 0, " +
                COL_PROD_RETAIL_PRICE + " REAL DEFAULT 0, " +
                COL_PROD_PRODUCTION_DATE + " TEXT, " +
                COL_PROD_EXPIRY_DATE + " TEXT, " +
                COL_PROD_NOTES + " TEXT, " +
                COL_PROD_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_PROD_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createProductsTable);

        // Insertar roles por defecto
        insertDefaultRoles(db);

        // Insertar usuario admin por defecto
        insertDefaultAdmin(db);

        String createSalesTable = "CREATE TABLE " + TABLE_SALES + " (" +
                COL_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SALE_USER_ID + " INTEGER NOT NULL, " +
                COL_SALE_CUSTOMER_NAME + " TEXT NOT NULL, " +
                COL_SALE_CUSTOMER_PHONE + " TEXT, " +
                COL_SALE_CUSTOMER_ADDRESS + " TEXT, " +
                COL_SALE_TOTAL + " REAL NOT NULL, " +
                COL_SALE_DATE + " TEXT NOT NULL, " +
                COL_SALE_STATUS + " TEXT DEFAULT 'Pendiente', " +
                COL_SALE_NOTES + " TEXT, " +
                "FOREIGN KEY(" + COL_SALE_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createSalesTable);

        String createSaleDetailsTable = "CREATE TABLE " + TABLE_SALE_DETAILS + " (" +
                COL_SD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SD_SALE_ID + " INTEGER NOT NULL, " +
                COL_SD_PRODUCT_ID + " INTEGER NOT NULL, " +
                COL_SD_PRODUCT_NAME + " TEXT NOT NULL, " +
                COL_SD_QUANTITY + " REAL NOT NULL, " +
                COL_SD_UNIT + " TEXT, " +
                COL_SD_UNIT_PRICE + " REAL NOT NULL, " +
                COL_SD_SUBTOTAL + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COL_SD_SALE_ID + ") REFERENCES " +
                TABLE_SALES + "(" + COL_SALE_ID + "), " +
                "FOREIGN KEY(" + COL_SD_PRODUCT_ID + ") REFERENCES " +
                TABLE_PRODUCTS + "(" + COL_PROD_ID + "))";



        db.execSQL(createSaleDetailsTable);

        String createClientesTable = "CREATE TABLE " + TABLE_CLIENTES + " (" +
                COL_CLIENTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CLIENTE_NOMBRE + " TEXT NOT NULL, " +
                COL_CLIENTE_TELEFONO + " TEXT, " +
                COL_CLIENTE_DIRECCION + " TEXT, " +
                COL_CLIENTE_EMAIL + " TEXT, " +
                COL_CLIENTE_NOTAS + " TEXT, " +
                COL_CLIENTE_ACTIVO + " INTEGER DEFAULT 1)";
        db.execSQL(createClientesTable);



    }

    private void insertDefaultRoles(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COL_ROLE_NAME, "Administrador");
        values.put(COL_ROLE_DESCRIPTION, "Acceso total al sistema");
        db.insert(TABLE_ROLES, null, values);

        values.clear();
        values.put(COL_ROLE_NAME, "Vendedor");
        values.put(COL_ROLE_DESCRIPTION, "Puede gestionar productos y ventas");
        db.insert(TABLE_ROLES, null, values);

        values.clear();
        values.put(COL_ROLE_NAME, "Cliente");
        values.put(COL_ROLE_DESCRIPTION, "Puede ver productos y realizar pedidos");
        db.insert(TABLE_ROLES, null, values);
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, "Administrador");
        values.put(COL_USER_EMAIL, "admin@pulpas.com");
        values.put(COL_USER_PHONE, "00000000");
        values.put(COL_USER_PASSWORD, hashPassword("admin123"));
        values.put(COL_USER_ACTIVE, 1);

        long userId = db.insert(TABLE_USERS, null, values);

        // Asignar rol de administrador
        if (userId != -1) {
            ContentValues roleValues = new ContentValues();
            roleValues.put(COL_UR_USER_ID, userId);
            roleValues.put(COL_UR_ROLE_ID, 1); // ID del rol Administrador
            db.insert(TABLE_USER_ROLES, null, roleValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ROLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTES);
        onCreate(db);
    }

    // ========== MÉTODOS DE USUARIOS ==========

    public long addCliente(Cliente cliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CLIENTE_NOMBRE, cliente.getNombre());
        values.put(COL_CLIENTE_TELEFONO, cliente.getTelefono());
        values.put(COL_CLIENTE_DIRECCION, cliente.getDireccion());
        values.put(COL_CLIENTE_EMAIL, cliente.getEmail());
        values.put(COL_CLIENTE_NOTAS, cliente.getNotas());
        values.put(COL_CLIENTE_ACTIVO, cliente.isActivo() ? 1 : 0);

        long result = db.insert(TABLE_CLIENTES, null, values);
        return result;
    }

    public List<Cliente> getAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CLIENTES, null, null, null, null, null,
                COL_CLIENTE_NOMBRE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Cliente cliente = new Cliente(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLIENTE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_TELEFONO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_DIRECCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOTAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLIENTE_ACTIVO)) == 1
                );
                clientes.add(cliente);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return clientes;
    }

    public List<Cliente> getActiveClientes() {
        List<Cliente> clientes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CLIENTES, null,
                COL_CLIENTE_ACTIVO + "=1", null, null, null,
                COL_CLIENTE_NOMBRE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Cliente cliente = new Cliente(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLIENTE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_TELEFONO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_DIRECCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOTAS)),
                        true
                );
                clientes.add(cliente);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return clientes;
    }

    public List<Sale> getUniqueCustomers() {
        List<Sale> customersForSale = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Obtenemos todos los clientes activos desde la tabla correcta.
        Cursor cursor = db.query(TABLE_CLIENTES,
                new String[]{COL_CLIENTE_NOMBRE, COL_CLIENTE_TELEFONO, COL_CLIENTE_DIRECCION},
                COL_CLIENTE_ACTIVO + "=1", // Solo clientes activos
                null, null, null,
                COL_CLIENTE_NOMBRE + " ASC");

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndexOrThrow(COL_CLIENTE_NOMBRE);
            int phoneIndex = cursor.getColumnIndexOrThrow(COL_CLIENTE_TELEFONO);
            int addressIndex = cursor.getColumnIndexOrThrow(COL_CLIENTE_DIRECCION);

            do {
                // Creamos un objeto 'Sale' temporal para no tener que cambiar
                // la lógica en CartActivity.
                Sale customerInfo = new Sale();
                customerInfo.setCustomerName(cursor.getString(nameIndex));
                customerInfo.setCustomerPhone(cursor.getString(phoneIndex));
                customerInfo.setCustomerAddress(cursor.getString(addressIndex));
                customersForSale.add(customerInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return customersForSale;
    }


    public Cliente getClienteById(int clienteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cliente cliente = null;

        Cursor cursor = db.query(TABLE_CLIENTES, null,
                COL_CLIENTE_ID + "=?",
                new String[]{String.valueOf(clienteId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            cliente = new Cliente(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLIENTE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_TELEFONO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_DIRECCION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOTAS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLIENTE_ACTIVO)) == 1
            );
        }
        cursor.close();

        return cliente;
    }

    // Actualizar cliente
    public boolean updateCliente(Cliente cliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CLIENTE_NOMBRE, cliente.getNombre());
        values.put(COL_CLIENTE_TELEFONO, cliente.getTelefono());
        values.put(COL_CLIENTE_DIRECCION, cliente.getDireccion());
        values.put(COL_CLIENTE_EMAIL, cliente.getEmail());
        values.put(COL_CLIENTE_NOTAS, cliente.getNotas());
        values.put(COL_CLIENTE_ACTIVO, cliente.isActivo() ? 1 : 0);

        int rows = db.update(TABLE_CLIENTES, values,
                COL_CLIENTE_ID + "=?",
                new String[]{String.valueOf(cliente.getId())});

        return rows > 0;
    }

    public boolean deactivateCliente(int clienteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLIENTE_ACTIVO, 0);

        int rows = db.update(TABLE_CLIENTES, values,
                COL_CLIENTE_ID + "=?",
                new String[]{String.valueOf(clienteId)});

        return rows > 0;
    }

    // Eliminar cliente (borrado físico)
    public boolean deleteCliente(int clienteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_CLIENTES,
                COL_CLIENTE_ID + "=?",
                new String[]{String.valueOf(clienteId)});

        return rows > 0;
    }

    // Buscar clientes por nombre
    public List<Cliente> searchClientesByName(String searchQuery) {
        List<Cliente> clientes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CLIENTES, null,
                COL_CLIENTE_NOMBRE + " LIKE ?",
                new String[]{"%" + searchQuery + "%"},
                null, null, COL_CLIENTE_NOMBRE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Cliente cliente = new Cliente(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLIENTE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_TELEFONO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_DIRECCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENTE_NOTAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLIENTE_ACTIVO)) == 1
                );
                clientes.add(cliente);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return clientes;
    }

    // Verificar si existe un email
    public boolean clienteEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLIENTES,
                new String[]{COL_CLIENTE_ID},
                COL_CLIENTE_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }


    public long registerUser(String name, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_PASSWORD, hashPassword(password));
        values.put(COL_USER_ACTIVE, 1);

        long userId = db.insert(TABLE_USERS, null, values);

        // Asignar rol de Cliente por defecto
        if (userId != -1) {
            ContentValues roleValues = new ContentValues();
            roleValues.put(COL_UR_USER_ID, userId);
            roleValues.put(COL_UR_ROLE_ID, 3); // ID del rol Cliente
            db.insert(TABLE_USER_ROLES, null, roleValues);
        }

        return userId;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        Cursor cursor = db.query(TABLE_USERS,
                null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=? AND " + COL_USER_ACTIVE + "=1",
                new String[]{email, hashedPassword},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ACTIVE)) == 1
            );
            cursor.close();
        }

        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COL_USER_NAME);

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ACTIVE)) == 1
                );
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return users;
    }

    public boolean updateUser(int userId, String name, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PHONE, phone);

        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        return rows > 0;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar relaciones usuario-rol
        db.delete(TABLE_USER_ROLES, COL_UR_USER_ID + "=?", new String[]{String.valueOf(userId)});

        // Eliminar usuario
        int rows = db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});

        return rows > 0;
    }

    // ========== MÉTODOS DE ROLES ==========

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROLES, null, null, null, null, null, COL_ROLE_NAME);

        if (cursor.moveToFirst()) {
            do {
                Role role = new Role(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ROLE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE_DESCRIPTION))
                );
                roles.add(role);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return roles;
    }

    public long addRole(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_ROLE_NAME, name);
        values.put(COL_ROLE_DESCRIPTION, description);

        return db.insert(TABLE_ROLES, null, values);
    }

    public boolean updateRole(int roleId, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_ROLE_NAME, name);
        values.put(COL_ROLE_DESCRIPTION, description);

        int rows = db.update(TABLE_ROLES, values, COL_ROLE_ID + "=?",
                new String[]{String.valueOf(roleId)});

        return rows > 0;
    }

    public boolean deleteRole(int roleId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar relaciones usuario-rol
        db.delete(TABLE_USER_ROLES, COL_UR_ROLE_ID + "=?", new String[]{String.valueOf(roleId)});

        // Eliminar rol
        int rows = db.delete(TABLE_ROLES, COL_ROLE_ID + "=?", new String[]{String.valueOf(roleId)});

        return rows > 0;
    }

    // ========== MÉTODOS DE ASIGNACIÓN USUARIO-ROL ==========

    public boolean assignRoleToUser(int userId, int roleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_UR_USER_ID, userId);
        values.put(COL_UR_ROLE_ID, roleId);

        long result = db.insert(TABLE_USER_ROLES, null, values);
        return result != -1;
    }

    public boolean removeRoleFromUser(int userId, int roleId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(TABLE_USER_ROLES,
                COL_UR_USER_ID + "=? AND " + COL_UR_ROLE_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(roleId)});

        return rows > 0;
    }

    public List<Role> getUserRoles(int userId) {
        List<Role> roles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.* FROM " + TABLE_ROLES + " r " +
                "INNER JOIN " + TABLE_USER_ROLES + " ur ON r." + COL_ROLE_ID + " = ur." + COL_UR_ROLE_ID +
                " WHERE ur." + COL_UR_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Role role = new Role(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ROLE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE_DESCRIPTION))
                );
                roles.add(role);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return roles;
    }

    // ========== MÉTODOS DE PRODUCTOS ==========

    public long addProduct(Product product, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PROD_NAME, product.getName());
        values.put(COL_PROD_QUANTITY, product.getQuantity());
        values.put(COL_PROD_UNIT, product.getUnit());
        values.put(COL_PROD_FRACTION, product.getFraction());
        values.put(COL_PROD_WHOLESALE_PRICE, product.getWholesalePrice()); // NUEVO
        values.put(COL_PROD_RETAIL_PRICE, product.getRetailPrice());       // NUEVO
        values.put(COL_PROD_PRODUCTION_DATE, product.getProductionDate());
        values.put(COL_PROD_EXPIRY_DATE, product.getExpiryDate());
        values.put(COL_PROD_NOTES, product.getNotes());
        values.put(COL_PROD_USER_ID, userId);

        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null,
                COL_PROD_NAME);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_FRACTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_PRODUCTION_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_EXPIRY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NOTES)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_WHOLESALE_PRICE)), // NUEVO
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_RETAIL_PRICE))     // NUEVO
                );
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return products;
    }

    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PROD_NAME, product.getName());
        values.put(COL_PROD_QUANTITY, product.getQuantity());
        values.put(COL_PROD_UNIT, product.getUnit());
        values.put(COL_PROD_FRACTION, product.getFraction());
        values.put(COL_PROD_WHOLESALE_PRICE, product.getWholesalePrice()); // NUEVO
        values.put(COL_PROD_RETAIL_PRICE, product.getRetailPrice());       // NUEVO
        values.put(COL_PROD_PRODUCTION_DATE, product.getProductionDate());
        values.put(COL_PROD_EXPIRY_DATE, product.getExpiryDate());
        values.put(COL_PROD_NOTES, product.getNotes());

        int rows = db.update(TABLE_PRODUCTS, values, COL_PROD_ID + "=?",
                new String[]{String.valueOf(product.getId())});

        return rows > 0;
    }
    public List<Product> getAvailableProductsForSale() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS, null,
                COL_PROD_QUANTITY + " > 0", null, null, null, COL_PROD_NAME);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_FRACTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_PRODUCTION_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_EXPIRY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NOTES)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_WHOLESALE_PRICE)), // NUEVO
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_RETAIL_PRICE))     // NUEVO
                );
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return products;
    }

    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS, COL_PROD_ID + "=?",
                new String[]{String.valueOf(productId)});

        return rows > 0;
    }

    // ========== UTILIDADES ==========

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("DatabaseHelper", "Error hashing password", e);
            return password;
        }
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=?", new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    public long createSale(Sale sale, List<CartItem> cartItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        long saleId = -1;

        try {
            db.beginTransaction();

            // Insertar venta
            ContentValues saleValues = new ContentValues();
            saleValues.put(COL_SALE_USER_ID, sale.getUserId());
            saleValues.put(COL_SALE_CUSTOMER_NAME, sale.getCustomerName());
            saleValues.put(COL_SALE_CUSTOMER_PHONE, sale.getCustomerPhone());
            saleValues.put(COL_SALE_CUSTOMER_ADDRESS, sale.getCustomerAddress());
            saleValues.put(COL_SALE_TOTAL, sale.getTotalAmount());
            saleValues.put(COL_SALE_DATE, sale.getSaleDate());
            saleValues.put(COL_SALE_STATUS, sale.getStatus());
            saleValues.put(COL_SALE_NOTES, sale.getNotes());

            saleId = db.insert(TABLE_SALES, null, saleValues);

            if (saleId != -1) {
                // Insertar detalles de venta
                for (CartItem item : cartItems) {
                    ContentValues detailValues = new ContentValues();
                    detailValues.put(COL_SD_SALE_ID, saleId);
                    detailValues.put(COL_SD_PRODUCT_ID, item.getProduct().getId());
                    detailValues.put(COL_SD_PRODUCT_NAME, item.getProduct().getName());
                    detailValues.put(COL_SD_QUANTITY, item.getQuantity());
                    detailValues.put(COL_SD_UNIT, item.getProduct().getUnit());
                    detailValues.put(COL_SD_UNIT_PRICE, item.getUnitPrice());
                    detailValues.put(COL_SD_SUBTOTAL, item.getSubtotal());

                    db.insert(TABLE_SALE_DETAILS, null, detailValues);
                }

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating sale", e);
        } finally {
            db.endTransaction();
        }

        return saleId;
    }

    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SALES, null, null, null, null, null,
                COL_SALE_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Sale sale = new Sale(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SALE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SALE_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SALE_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SALE_CUSTOMER_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SALE_CUSTOMER_ADDRESS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SALE_TOTAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SALE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SALE_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SALE_NOTES))
                );
                sales.add(sale);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sales;
    }

    public List<SaleDetail> getSaleDetails(int saleId) {
        List<SaleDetail> details = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SALE_DETAILS, null,
                COL_SD_SALE_ID + "=?", new String[]{String.valueOf(saleId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                SaleDetail detail = new SaleDetail(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SD_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SD_SALE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SD_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SD_PRODUCT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SD_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SD_UNIT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SD_UNIT_PRICE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SD_SUBTOTAL))
                );
                details.add(detail);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return details;
    }

    public boolean updateSaleStatus(int saleId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SALE_STATUS, status);

        int rows = db.update(TABLE_SALES, values, COL_SALE_ID + "=?",
                new String[]{String.valueOf(saleId)});

        return rows > 0;
    }

    public boolean deleteSale(int saleId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar detalles
        db.delete(TABLE_SALE_DETAILS, COL_SD_SALE_ID + "=?",
                new String[]{String.valueOf(saleId)});

        // Eliminar venta
        int rows = db.delete(TABLE_SALES, COL_SALE_ID + "=?",
                new String[]{String.valueOf(saleId)});

        return rows > 0;
    }

}
