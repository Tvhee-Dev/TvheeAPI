package me.tvhee.tvheeapi.api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Table<T>
{
	protected final Database database;
	private final String name;
	private final List<Column> columns;

	protected Table(String name, Database database)
	{
		this.name = name;
		this.database = database;

		List<Column> columns = new ArrayList<>();

		try
		{
			columns = getColumns();
		}
		catch(Exception ignored)
		{
		}

		this.columns = columns;
	}

	public abstract List<Column> getColumns();

	public abstract T deserialize(ResultSet data) throws SQLException;

	public abstract Map<String, Object> serialize(T data);

	public abstract boolean remove(T data);

	public String getName()
	{
		return name;
	}

	public void createIfNotExists()
	{
		this.database.update(createQuery(null, QueryType.CREATE));
	}

	public void removeTable()
	{
		this.database.update(createQuery(null, QueryType.REMOVE_TABLE));
	}

	public void recreateTable()
	{
		removeTable();
		createIfNotExists();
	}

	public T getFirstValue(String query, Object... values)
	{
		List<T> data = get(query, values);

		if(data.isEmpty())
			return null;
		else
			return data.get(0);
	}

	public int getId(T value)
	{
		try
		{
			Map<String, Object> objects = serialize(value);
			StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + getName() + " WHERE ");

			for(Column column : columns)
				queryBuilder.append(column.getName()).append("=? AND ");

			queryBuilder.setLength(queryBuilder.length() - 5);

			PreparedStatement preparedStatement = this.database.prepareStatement(queryBuilder.toString());
			List<Object> values = new ArrayList<>(objects.values());

			for(int index = 0; index < values.size(); index++)
				preparedStatement.setObject(index + 1, values.get(index));

			ResultSet resultSet = this.database.query(preparedStatement);
			resultSet.next();
			return resultSet.getInt("id");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public List<T> get(String query, Object... values)
	{
		PreparedStatement preparedStatement = this.database.prepareStatement(query.replace("{table}", getName()));

		try
		{
			for(int i = 1; i <= values.length; i++)
				preparedStatement.setObject(i, values[i - 1]);

			ResultSet resultSet = this.database.query(preparedStatement);
			List<T> deserializedData = new ArrayList<>();

			while(resultSet.next())
			{
				T data = deserialize(resultSet);

				if(data != null)
					deserializedData.add(data);
			}

			return deserializedData;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	//Set new data, and apply the patch to the database
	//The method will automatically decide if it should INSERT or UPDATE
	public void addOrUpdate(T data)
	{
		PreparedStatement query = this.createQuery(data, QueryType.INSERT_OR_UPDATE);

		if(query == null)
			return;

		this.database.update(query);
	}

	protected boolean executeUpdate(String query, Object... values)
	{
		PreparedStatement preparedStatement = this.database.prepareStatement(query.replace("{table}", getName()));

		try
		{
			for(int i = 1; i <= values.length; i++)
				preparedStatement.setObject(i, values[i - 1]);

			database.update(preparedStatement);
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * No need to provide a key / data for QueryType.CREATE
	 *
	 * @param data Necessary for QueryType.INSERT and QueryType.UPDATE
	 */

	private PreparedStatement createQuery(T data, QueryType type)
	{
		PreparedStatement preparedStatement = null;

		try
		{
			if(type == QueryType.INSERT_OR_UPDATE)
			{
				StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + this.name + "(");

				for(Column column : columns)
					queryBuilder.append(column.getName()).append(",");

				queryBuilder = new StringBuilder(queryBuilder.substring(0, queryBuilder.length() - 1));
				queryBuilder.append(") VALUES(");

				for(int i = 0; i < columns.size(); i++)
					queryBuilder.append("?,");

				queryBuilder = new StringBuilder(queryBuilder.substring(0, queryBuilder.length() - 1));
				queryBuilder.append(") ON DUPLICATE KEY UPDATE ");

				int id = getId(data);

				if(id != -1)
					queryBuilder.append("id=").append(id).append(",");

				for(Column column : columns)
				{
					if(!column.isPrimary())
						queryBuilder.append(column.getName()).append("=?,");
				}

				String query = queryBuilder.substring(0, queryBuilder.length() - 1) + ";";

				preparedStatement = database.prepareStatement(query);
				Map<String, Object> deserializedData = serialize(data);

				if(deserializedData.size() != columns.size())
					throw new IllegalArgumentException("You haven't provided all data!");

				int primaryColumns = 0;

				for(int i = 1; i <= columns.size(); i++)
				{
					Column column = columns.get(i - 1);
					preparedStatement.setObject(i, deserializedData.get(column.getName()));

					if(!column.isPrimary())
						preparedStatement.setObject(i + columns.size() - primaryColumns, deserializedData.get(column.getName()));
					else
						primaryColumns++;
				}
			}
			else if(type == QueryType.CREATE)
			{
				StringBuilder queryBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + this.name + "` (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");

				for(Column column : columns)
				{
					queryBuilder.append(column.getName()).append(" ").append(column.getDataType());

					if(column.isPrimary())
						queryBuilder.append(" UNIQUE");

					queryBuilder.append(",");
				}

				queryBuilder.setLength(queryBuilder.length() - 1);
				queryBuilder.append(");");
				preparedStatement = this.database.prepareStatement(queryBuilder.toString());
			}
			else if(type == QueryType.SELECT_ALL)
			{
				preparedStatement = database.prepareStatement("SELECT * FROM " + name + ";");
			}
			else if(type == QueryType.REMOVE_TABLE)
			{
				preparedStatement = database.prepareStatement("DROP TABLE " + name + ";");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return preparedStatement;
	}

	private enum QueryType
	{
		SELECT_ALL, //Get all the data from the database
		INSERT_OR_UPDATE, //Save new data if the key does not exist or updateToCache the data if the key already exists
		CREATE, //Create the table if not exists
		REMOVE, //Remove a value in the table
		REMOVE_TABLE //Remove the entire table
	}
}
