package me.tvhee.tvheeapi.api.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import sun.misc.Unsafe;

public final class Reflection
{
	private final Object object;

	public Reflection(Object object)
	{
		this.object = object;
	}

	public <T> T getObject()
	{
		return (T) object;
	}

	public Class<?> getObjectClass()
	{
		return object.getClass();
	}

	public String getObjectClassName()
	{
		return object.getClass().getTypeName();
	}

	public String getObjectFullClassName()
	{
		return object.getClass().getName();
	}

	public String getObjectPackageName()
	{
		return object.getClass().getPackage().getName();
	}

	public void setField(String field, Object object) throws IllegalArgumentException
	{
		List<Class<?>> superClasses = getSuperClasses(getObjectClass());

		for(Class<?> superClass : superClasses)
		{
			try
			{
				Field reflectedField = superClass.getDeclaredField(field);
				setAccessible(reflectedField);
				reflectedField.set(this.object, object);
				return;
			}
			catch(NoSuchFieldException ignored)
			{
				//Looking forward for the superclasses
			}
			catch(IllegalAccessException e)
			{
				//IllegalArgumentException is also good
				throw new TvheeAPIException(getClass(), "setField", e);
			}
		}

		throw new TvheeAPIException(getClass(), "setField", "Field '" + field + "' not found!");
	}

	public Reflection getField(Class<?> returnType)
	{
		List<Class<?>> superClasses = getSuperClasses(getObjectClass());

		for(Class<?> superClass : superClasses)
		{
			try
			{
				for(Field field : superClass.getDeclaredFields())
				{
					if(field.getType().equals(returnType))
					{
						setAccessible(field);
						return new Reflection(field.get(this.object));
					}
				}
			}
			catch(IllegalAccessException e)
			{
				throw new TvheeAPIException(getClass(), "getField", e);
			}
		}

		throw new TvheeAPIException(getClass(), "getField", "Field with returntype '" + returnType + "' not found!");
	}

	public Reflection getField(String field) throws IllegalArgumentException
	{
		List<Class<?>> superClasses = getSuperClasses(getObjectClass());

		for(Class<?> superClass : superClasses)
		{
			try
			{
				Field reflectedField = superClass.getDeclaredField(field);
				setAccessible(reflectedField);
				return new Reflection(reflectedField.get(this.object));
			}
			catch(NoSuchFieldException ignored)
			{
			}
			catch(IllegalAccessException e)
			{
				throw new TvheeAPIException(getClass(), "getField", e);
			}
		}

		throw new TvheeAPIException(getClass(), "getField", "Field '" + field + "' not found!");
	}

	public boolean hasField(String field)
	{
		try
		{
			getField(field);
			return true;
		}
		catch(IllegalArgumentException ignored)
		{
			return false;
		}
	}

	public boolean hasMethod(String method, Object... parameters)
	{
		try
		{
			invokeMethod(method, parameters);
			return true;
		}
		catch(IllegalArgumentException ignored)
		{
			return false;
		}
	}

	public Reflection invokeMethod(String method, Object... parameters) throws IllegalArgumentException
	{
		Class<?>[] parameterTypes = getClasses(parameters);
		List<Class<?>> superClasses = getSuperClasses(getObjectClass());

		for(Class<?> superClass : superClasses)
		{
			try
			{
				Method javaMethod = testMethod(superClass, method, parameterTypes);

				if(javaMethod == null)
					continue;

				setAccessible(javaMethod);
				return new Reflection(javaMethod.invoke(this.object, parameters));
			}
			catch(InvocationTargetException | IllegalAccessException e)
			{
				throw new TvheeAPIException(getClass(), "invokeMethod", e);
			}
		}

		throw new TvheeAPIException(getClass(), "invokeMethod", "Method '" + method + " (" + Arrays.toString(parameterTypes) + ")' not found!");
	}

	public static Class<?> getClass(String name)
	{
		try
		{
			return getClassWithException(name);
		}
		catch(IllegalArgumentException e)
		{
			throw new TvheeAPIException(Reflection.class, "getClass", e);
		}
	}

	public static Class<?> getClassWithException(String name) throws TvheeAPIException
	{
		try
		{
			return Class.forName(name.replaceAll("nms", getNms().replaceAll("obc", getObc())));
		}
		catch(ClassNotFoundException e)
		{
			throw new TvheeAPIException(Reflection.class, "getClassWithException", e);
		}
	}

	public static Reflection newInstance(String className, Object... parameters) throws IllegalArgumentException
	{
		return newInstance(getClass(className), parameters);
	}

	public static Reflection newInstance(Class<?> clazz, Object... parameters) throws IllegalArgumentException
	{
		Class<?>[] parameterTypes = getClasses(parameters);
		List<Class<?>> superClasses = getSuperClasses(clazz);

		for(Class<?> superClass : superClasses)
		{
			try
			{
				Constructor<?> constructor = testConstructor(superClass, parameterTypes);

				if(constructor == null)
					continue;

				setAccessible(constructor);
				return new Reflection(constructor.newInstance(parameters));
			}
			catch(InvocationTargetException | IllegalAccessException | InstantiationException e)
			{
				throw new TvheeAPIException(Reflection.class, "newInstance", e);
			}
		}

		throw new TvheeAPIException(Reflection.class, "newInstance", "Constructor ('" + Arrays.toString(parameterTypes) + "') not found!");
	}

	public static Reflection invokeStaticMethod(String className, String method, Object... parameters) throws IllegalArgumentException
	{
		return invokeStaticMethod(getClass(className), method, parameters);
	}

	public static Reflection invokeStaticMethod(Class<?> clazz, String method, Object... parameters) throws IllegalArgumentException
	{
		Class<?>[] parameterTypes = getClasses(parameters);
		List<Class<?>> superClasses = getSuperClasses(clazz);

		for(Class<?> superClass : superClasses)
		{
			try
			{
				Method javaMethod = testMethod(superClass, method, parameterTypes);

				if(javaMethod == null)
					continue;

				setAccessible(javaMethod);
				return new Reflection(javaMethod.invoke(null, parameters));
			}
			catch(InvocationTargetException | IllegalAccessException e)
			{
				throw new TvheeAPIException(Reflection.class, "invokeStaticMethod", e);
			}
		}

		throw new TvheeAPIException(Reflection.class, "invokeStaticMethod", "Method '" + method + " (" + Arrays.toString(parameterTypes) + ")' not found!");
	}

	public static void setStaticField(String className, String field, Object object) throws IllegalArgumentException
	{
		setStaticField(getClass(className), field, object);
	}

	private static long staticFieldOffset(Field field)
	{
		return getUnsafe().staticFieldOffset(field);
	}

	private static Object staticFieldBase(Field field)
	{
		return getUnsafe().staticFieldBase(field);
	}

	private static void putObject(Object object1, long offset, Object object2)
	{
		getUnsafe().putObject(object1, offset, object2);
	}

	public static void setStaticField(Class<?> clazz, String field, Object object) throws IllegalArgumentException
	{
		if(clazz.equals(TvheeAPI.class))
			throw new TvheeAPIException(Reflection.class, "setStaticField", "You can't set the instance of " + TvheeAPI.class + "!");

		List<Class<?>> superClasses = getSuperClasses(clazz);

		for(Class<?> superClass : superClasses)
		{
			try
			{
				Field reflectedField = superClass.getDeclaredField(field);
				setAccessible(reflectedField);

				final Object staticFieldBase = staticFieldBase(reflectedField);
				final long staticFieldOffset = staticFieldOffset(reflectedField);
				putObject(staticFieldBase, staticFieldOffset, object);
				return;
			}
			catch(NoSuchFieldException ignored)
			{

			}
			catch(SecurityException | IllegalArgumentException e)
			{
				throw new TvheeAPIException(Reflection.class, "setStaticField", e);
			}
		}

		throw new TvheeAPIException(Reflection.class, "setStaticField", "Field '" + field + "' not found!");
	}

	public static Reflection getStaticField(Class<?> clazz, Class<?> returnType)
	{
		List<Class<?>> superClasses = getSuperClasses(clazz);

		for(Class<?> superClass : superClasses)
		{
			try
			{
				for(Field field : superClass.getDeclaredFields())
				{
					if(field.getType().equals(returnType))
					{
						setAccessible(field);
						return new Reflection(field.get(null));
					}
				}
			}
			catch(IllegalAccessException e)
			{
				throw new TvheeAPIException(Reflection.class, "getStaticField", e);
			}
		}

		throw new TvheeAPIException(Reflection.class, "getField", "Field with returntype '" + returnType + "' not found!");
	}

	public static Reflection getStaticField(String className, String field) throws IllegalArgumentException
	{
		return getStaticField(getClass(className), field);
	}

	public static Reflection getStaticField(Class<?> clazz, String field) throws IllegalArgumentException
	{
		List<Class<?>> superClasses = getSuperClasses(clazz);

		for(Class<?> superClass : superClasses)
		{
			try
			{
				Field reflectedField = superClass.getDeclaredField(field);
				setAccessible(reflectedField);
				return new Reflection(reflectedField.get(null));
			}
			catch(IllegalAccessException | IllegalArgumentException e)
			{
				throw new TvheeAPIException(Reflection.class, "getStaticField", e);
			}
			catch(NoSuchFieldException ignored)
			{
			}
		}

		throw new TvheeAPIException(Reflection.class, "getStaticField", "Field '" + field + "' not found!");
	}

	private static String getObc()
	{
		try
		{
			Field server = Class.forName("org.bukkit.Bukkit").getDeclaredField("server");
			setAccessible(server);
			String name = server.get(null).getClass().getPackage().getName();
			return "org.bukkit.craftbukkit." + name.substring(name.lastIndexOf('.') + 1);
		}
		catch(ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
		{
			return "";
		}
	}

	private static String getNms()
	{
		String obc = getObc();

		if(obc.contains("1_17"))
			return "net.minecraft";
		else
			return getObc().replaceAll("org\\.bukkit\\.craftbukkit", "net\\.minecraft\\.server");
	}

	private static void setAccessible(AccessibleObject accessibleObject)
	{
		try
		{
			accessibleObject.setAccessible(true);
		}
		catch(InaccessibleObjectException ignored)
		{
			AccessController.doPrivileged(new PrivilegedAction()
			{
				public Object run()
				{
					accessibleObject.setAccessible(true);
					return null;
				}
			});
		}
	}

	private static Method testMethod(Class<?> clazz, String name, Class<?>[] params)
	{
		for(Method method : clazz.getDeclaredMethods())
		{
			if(testParameters(method.getParameterTypes(), params) && method.getName().equals(name))
				return method;
		}

		return null;
	}

	private static Constructor<?> testConstructor(Class<?> clazz, Class<?>[] params)
	{
		for(Constructor<?> constructor : clazz.getDeclaredConstructors())
		{
			if(testParameters(constructor.getParameterTypes(), params))
				return constructor;
		}

		return null;
	}

	private static boolean testParameters(Class<?>[] parameters, Class<?>[] requestedParameters)
	{
		if(parameters.length != requestedParameters.length)
			return false;

		for(int i = 0; i < requestedParameters.length; i++)
		{
			Class<?> parameter = parameters[i];
			Class<?> requestedParameter = requestedParameters[i];

			if(!parameter.equals(requestedParameter) & !testPrimitive(parameter, requestedParameter))
			{
				for(Class<?> superClass : getSuperClasses(parameter))
				{
					if(parameter.equals(superClass))
						return true;
				}

				return false;
			}
		}

		return true;
	}

	private static boolean testPrimitive(Class<?> param, Class<?> toTest)
	{
		if((param.equals(Byte.class) || param.equals(byte.class)) && (toTest.equals(byte.class) || toTest.equals(Byte.class)))
			return true;
		else if((param.equals(Short.class) || param.equals(short.class)) && (toTest.equals(short.class) || toTest.equals(Short.class)))
			return true;
		else if((param.equals(Integer.class) || param.equals(int.class)) && (toTest.equals(int.class) || toTest.equals(Integer.class)))
			return true;
		else if((param.equals(Long.class) || param.equals(long.class)) && (toTest.equals(long.class) || toTest.equals(Long.class)))
			return true;
		else if((param.equals(Float.class) || param.equals(float.class)) && (toTest.equals(float.class) || toTest.equals(Float.class)))
			return true;
		else if((param.equals(Double.class) || param.equals(double.class)) && (toTest.equals(double.class) || toTest.equals(Double.class)))
			return true;
		else if((param.equals(Character.class) || param.equals(char.class)) && (toTest.equals(char.class) || toTest.equals(Character.class)))
			return true;
		else
			return (param.equals(Boolean.class) || param.equals(boolean.class)) && (toTest.equals(boolean.class) || toTest.equals(Boolean.class));
	}

	private static List<Class<?>> getSuperClasses(Class<?> clazz)
	{
		List<Class<?>> superClasses = new ArrayList<>(Arrays.asList(clazz.getInterfaces()));
		Class<?> superClass = clazz;

		while(superClass != null)
		{
			superClasses.add(superClass);
			superClass = superClass.getSuperclass();
		}

		return superClasses;
	}

	private static Class<?>[] getClasses(Object[] parameters)
	{
		Class<?>[] parameterTypes = new Class[parameters.length];

		for(int i = 0; i < parameters.length; i++)
			parameterTypes[i] = parameters[i].getClass();

		return parameterTypes;
	}

	private static Unsafe getUnsafe()
	{
		try
		{
			final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			setAccessible(unsafeField);
			return (Unsafe) unsafeField.get(null);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			throw new TvheeAPIInternalException(Reflection.class, "getUnsafe", e);
		}
	}
}
