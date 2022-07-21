package me.tvhee.tvheeapi.api.mysql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class Database
{
	private final Map<String, Table<?>> tables = new HashMap<>();
	private String databaseName;
	private Connection connection;

	public void initializeConnection(String host, int port, String database, String username, String password, boolean ssl) throws SQLException
	{
		try
		{
			DriverManager.registerDriver((Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		Properties properties = new Properties();
		properties.setProperty("user", username);
		properties.setProperty("password", password);
		properties.setProperty("useSSL", String.valueOf(ssl));
		properties.setProperty("autoReconnect", "true");
		properties.setProperty("useUnicode", "yes");
		properties.setProperty("characterEncoding", "UTF-8");

		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
		databaseName = database;
	}

	public void registerTable(Table<?> table)
	{
		registerTable(table, true);
	}

	public void registerTable(Table<?> table, boolean create)
	{
		if(create)
			table.createIfNotExists();

		tables.put(table.getName(), table);
	}

	public boolean hasTable(String name)
	{
		return tables.containsKey(name);
	}

	public boolean hasTable(Class<? extends Table<?>> tableClass)
	{
		try
		{
			return getTable(tableClass) != null;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public Table<?> getTable(String name)
	{
		return tables.get(name);
	}

	public <T extends Table<?>> T getTable(String name, Class<T> clazz)
	{
		Table<?> table = tables.get(name);

		if(clazz.isInstance(table))
			return (T) table;

		throw new IllegalArgumentException("Table for class " + clazz + " and name " + name + " not found!");
	}

	public <T extends Table<?>> T getTable(Class<T> clazz)
	{
		for(Table<?> table : tables.values())
		{
			if(clazz.isInstance(table))
				return (T) table;
		}

		throw new IllegalArgumentException("Table for class " + clazz + " not found!");
	}

	public List<String> getAllTableNames()
	{
		if(connection == null)
			throw new IllegalArgumentException("Connection has not been initialized!");

		List<String> tables = new ArrayList<>();

		try
		{
			PreparedStatement preparedStatement = prepareStatement("SELECT table_name FROM information_schema.tables WHERE table_schema = ?");
			preparedStatement.setString(1, databaseName);

			ResultSet resultSet = query(preparedStatement);

			while(resultSet.next())
				tables.add(resultSet.getString("table_name"));
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return tables;
	}

	public PreparedStatement prepareStatement(String query)
	{
		try
		{
			return this.connection.prepareStatement(query);
		}
		catch(SQLException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	public void update(PreparedStatement statement)
	{
		try
		{
			statement.executeUpdate();
			statement.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public ResultSet query(PreparedStatement statement)
	{
		try
		{
			return statement.executeQuery();
		}
		catch(SQLException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
}