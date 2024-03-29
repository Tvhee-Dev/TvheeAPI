package me.tvhee.tvheeapi.core;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import me.tvhee.tvheeapi.api.annotations.Permission;
import me.tvhee.tvheeapi.api.annotations.PluginMain;
import me.tvhee.tvheeapi.api.annotations.Register;
import me.tvhee.tvheeapi.api.annotations.Require;
import me.tvhee.tvheeapi.api.annotations.RegistrationType;
import me.tvhee.tvheeapi.api.annotations.Support;
import me.tvhee.tvheeapi.api.command.CommandExecutor;
import me.tvhee.tvheeapi.api.description.ApiVersion;
import me.tvhee.tvheeapi.api.description.PluginLoadOrder;
import me.tvhee.tvheeapi.api.description.PluginLoader;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.bungee.api.event.BungeeListener;
import me.tvhee.tvheeapi.spigot.api.event.SpigotListener;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

@SupportedAnnotationTypes("me.tvhee.tvheeapi.*")
public final class TvheeAPIAnnotationProcessor extends AbstractProcessor
{
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment)
	{
		Set<? extends Element> mainElements = roundEnvironment.getElementsAnnotatedWith(PluginMain.class);

		if(mainElements.size() > 1)
		{
			throwError("You can only annotate your main once!");
			return false;
		}

		if(mainElements.isEmpty())
			return false;

		TypeElement mainPluginType = checkType(mainElements.iterator().next(), TvheeAPIPlugin.class);

		if(mainPluginType == null)
			return false;

		PluginMain pluginMain = mainPluginType.getAnnotation(PluginMain.class);
		Support support = pluginMain.support();

		List<String> bungeeListeners = new ArrayList<>();
		List<String> spigotListeners = new ArrayList<>();
		List<String> commands = new ArrayList<>();

		for(Element element : roundEnvironment.getElementsAnnotatedWith(Register.class))
		{
			Register registerAnnotation = element.getAnnotation(Register.class);
			RegistrationType registrationType = registerAnnotation.value();

			switch(registrationType)
			{
				case COMMAND :
				{
					TypeElement commandTypeElement = checkType(element, CommandExecutor.class);

					if(commandTypeElement != null)
						commands.add(commandTypeElement.getQualifiedName().toString());
				}
				case BUNGEE_LISTENER :
				{
					if(support == Support.BUKKIT_ONLY)
						continue;

					TypeElement bungeeListenerTypeElement = checkType(element, BungeeListener.class);

					if(bungeeListenerTypeElement != null)
						bungeeListeners.add(bungeeListenerTypeElement.getQualifiedName().toString());
				}
				case SPIGOT_LISTENER :
				{
					if(support == Support.BUNGEE_ONLY)
						continue;

					TypeElement spigotListenerTypeElement = checkType(element, SpigotListener.class);

					if(spigotListenerTypeElement != null)
						spigotListeners.add(spigotListenerTypeElement.getQualifiedName().toString());
				}
			}
		}

		for(Element element : roundEnvironment.getElementsAnnotatedWith(Require.class))
		{
			Require requireAnnotation = element.getAnnotation(Require.class);

			Class<? extends Annotation> requireAnnotationClass;

			try
			{
				String classString = requireAnnotation.toString().replaceAll(Require.class.getName() + "\\(value=", "").replaceAll("@", "").replaceAll("\\)", "");
				requireAnnotationClass = (Class<? extends Annotation>) Class.forName(classString);
			}
			catch(ClassNotFoundException e)
			{
				continue;
			}

			if(element.getAnnotation(requireAnnotationClass) == null)
			{
				throwError("You missed the " + requireAnnotationClass.getName() + " annotation in class " + element);
				return false;
			}
		}

		getAndSavePluginFile(mainPluginType.getQualifiedName().toString(), mainPluginType.getAnnotation(PluginMain.class), spigotListeners, bungeeListeners, commands);
		logMessage("[TvheeAPI] Finished processing annotations!");
		return true;
	}

	private void getAndSavePluginFile(String apiMainClass, PluginMain pluginMain, List<String> spigotListeners, List<String> bungeeListeners, List<String> commands)
	{
		Map<String, Object> yml = new LinkedHashMap<>();

		Support support = pluginMain.support();
		String pluginName = pluginMain.pluginName();
		String version = pluginMain.version();
		ApiVersion apiVersion = pluginMain.apiVersion();
		String description = pluginMain.description();
		PluginLoadOrder pluginLoadOrder = pluginMain.loadOrder();
		String[] authorsArray = pluginMain.authors();
		String website = pluginMain.website();
		String prefix = pluginMain.logPrefix();
		String[] hardDependenciesArray = pluginMain.dependencies();
		String[] softDependenciesArray = pluginMain.softDependencies();
		String[] loadBeforeArray = pluginMain.loadBefore();
		String[] libraries = pluginMain.mavenCentralLibraries();
		Permission[] permissions = pluginMain.permissions();

		yml.put("name", pluginName.equals("") ? "AnTvheeAPIPlugin" : pluginName);
		yml.put("api-main", apiMainClass);
		yml.put("version", version);
		yml.put("api-version", apiVersion.toString());

		if(!description.equals(""))
			yml.put("description", description);

		yml.put("load", pluginLoadOrder.toString());

		List<String> authors = Arrays.asList(authorsArray);

		if(!authors.isEmpty())
		{
			if(authors.size() == 1)
				yml.put("author", authors.get(0));
			else
				yml.put("authors", authors);
		}

		if(!website.equals(""))
			yml.put("website", website);

		if(!prefix.equals(""))
			yml.put("prefix", prefix);

		Map<String, Object> permissionsMap = new LinkedHashMap<>();

		for(Permission permission : permissions)
		{
			Map<String, Object> thisPermission = new LinkedHashMap<>();
			thisPermission.put("default", permission.permissionDefault().toString());

			String[] allowedChildren = permission.allowedChildren();
			String[] disallowedChildren = permission.disallowedChildren();

			Map<String, Object> children = new LinkedHashMap<>();

			for(String allowedChild : allowedChildren)
				children.put(allowedChild, true);

			for(String disallowedChild : disallowedChildren)
				children.put(disallowedChild, false);

			if(!children.isEmpty())
				thisPermission.put("children", children);

			permissionsMap.put(permission.name(), thisPermission);
		}

		if(!permissionsMap.isEmpty())
			yml.put("permissions", permissionsMap);

		List<String> hardDependencies = Arrays.asList(hardDependenciesArray);

		if(!hardDependencies.isEmpty())
			yml.put("depend", hardDependencies);

		List<String> softDependencies = Arrays.asList(softDependenciesArray);

		if(!softDependencies.isEmpty())
			yml.put("softdepend", softDependencies);

		List<String> loadBefore = Arrays.asList(loadBeforeArray);

		if(!loadBefore.isEmpty())
			yml.put("loadbefore", loadBefore);

		List<String> librariesList = Arrays.asList(libraries);

		if(!librariesList.isEmpty())
			yml.put("libraries", librariesList);

		if(!bungeeListeners.isEmpty())
			yml.put("listeners-bungee", bungeeListeners);

		if(!spigotListeners.isEmpty())
			yml.put("listeners-spigot", spigotListeners);

		if(!commands.isEmpty())
			yml.put("registered-commands", commands);

		try
		{
			for(PluginLoader pluginLoader : PluginLoader.values())
			{
				if((pluginLoader == PluginLoader.BUKKIT_PLUGIN && support == Support.BUNGEE_ONLY) || (pluginLoader == PluginLoader.BUNGEE_PLUGIN && support == Support.BUKKIT_ONLY))
					continue;

				yml.put("main", pluginLoader.toString());

				String pluginBungeeYml = pluginLoader == PluginLoader.BUKKIT_PLUGIN ? "plugin.yml" : "bungee.yml";
				Yaml yaml = new Yaml();
				FileObject file = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", pluginBungeeYml);

				try (Writer writer = file.openWriter())
				{
					writer.append("#Auto-generated ").append(pluginBungeeYml).append(", generated at ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append(" by ").append(this.getClass().getName()).append("\n");
					String raw = yaml.dumpAs(yml, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
					writer.write(raw);
					writer.flush();

					logMessage("[TvheeAPI] Generated " + pluginBungeeYml + "!");
				}
			}
		}
		catch(IOException e)
		{
			throwError(e.getMessage());
		}
	}

	private TypeElement checkType(Element element, Class<?> parent)
	{
		TypeElement typeElement;

		if(element instanceof TypeElement)
		{
			typeElement = (TypeElement) element;
		}
		else
		{
			throwError("Not a class!", element);
			return null;
		}

		if(!(typeElement.getEnclosingElement() instanceof PackageElement))
		{
			throwError("Not a top-level class", typeElement);
			return null;
		}

		if(typeElement.getModifiers().contains(Modifier.STATIC))
		{
			throwError("Cannot be static nested", typeElement);
			return null;
		}

		if(parent != null)
		{
			if(!processingEnv.getTypeUtils().isSubtype(typeElement.asType(), processingEnv.getElementUtils().getTypeElement(parent.getName()).asType()))
				throwError("Class is not an subclass of " + parent.getName() + "!");
		}

		return typeElement;
	}

	private void logMessage(String message)
	{
		this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message + "\r\n");
	}

	private void throwError(String message)
	{
		this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message + "\r\n");
	}

	private void throwError(String message, Element element)
	{
		this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message + "\r\n", element);
	}
}
