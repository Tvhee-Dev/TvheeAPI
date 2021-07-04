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
import me.tvhee.tvheeapi.api.annotations.RegistrationType;
import me.tvhee.tvheeapi.api.annotations.Require;
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
		List<String> bungeeListeners = new ArrayList<>();
		List<String> spigotListeners = new ArrayList<>();
		List<String> commands = new ArrayList<>();

		for(Element element : roundEnvironment.getElementsAnnotatedWith(Register.class))
		{
			Register registerAnnotation = element.getAnnotation(Register.class);
			RegistrationType registrationType = registerAnnotation.value();

			switch(registrationType)
			{
				case COMMAND ->
				{
					TypeElement commandTypeElement = checkType(element, CommandExecutor.class);
					commands.add(commandTypeElement.getQualifiedName().toString());
				}
				case BUNGEE_LISTENER ->
				{
					TypeElement bungeeListenerTypeElement = checkType(element, BungeeListener.class);
					bungeeListeners.add(bungeeListenerTypeElement.getQualifiedName().toString());
				}
				case SPIGOT_LISTENER ->
				{
					TypeElement spigotListenerTypeElement = checkType(element, SpigotListener.class);
					spigotListeners.add(spigotListenerTypeElement.getQualifiedName().toString());
				}
				default -> throw new IllegalArgumentException("Unexpected value: " + registrationType);
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

		final Set<? extends Element> mainElements = roundEnvironment.getElementsAnnotatedWith(PluginMain.class);

		if(mainElements.size() > 1)
		{
			throwError("You can only annotate your main once!");
			return false;
		}

		if(mainElements.isEmpty())
			return false;

		final TypeElement mainPluginType = checkType(mainElements.iterator().next(), TvheeAPIPlugin.class);

		if(mainPluginType == null)
			return false;

		getAndSavePluginFile(PluginLoader.BUKKIT_PLUGIN, mainPluginType.getQualifiedName().toString(), mainPluginType.getAnnotation(PluginMain.class), spigotListeners, bungeeListeners, commands);
		getAndSavePluginFile(PluginLoader.BUNGEE_PLUGIN, mainPluginType.getQualifiedName().toString(), mainPluginType.getAnnotation(PluginMain.class), spigotListeners, bungeeListeners, commands);

		return true;
	}

	private void getAndSavePluginFile(final PluginLoader main, final String apiMainClass, final PluginMain pluginMain, final List<String> spigotListeners, final List<String> bungeeListeners, final List<String> commands)
	{
		if(main == PluginLoader.BUKKIT_PLUGIN && !pluginMain.bukkitSupport())
			return;
		else if(main == PluginLoader.BUNGEE_PLUGIN && !pluginMain.bungeeSupport())
			return;

		final Map<String, Object> yml = new LinkedHashMap<>();

		final String pluginName = pluginMain.pluginName();
		final String version = pluginMain.version();
		final ApiVersion apiVersion = pluginMain.apiVersion();
		final String description = pluginMain.description();
		final PluginLoadOrder pluginLoadOrder = pluginMain.loadOrder();
		final String[] authorsArray = pluginMain.authors();
		final String website = pluginMain.website();
		final String prefix = pluginMain.logPrefix();
		final String[] hardDependenciesArray = pluginMain.dependencies();
		final String[] softDependenciesArray = pluginMain.softDependencies();
		final String[] loadBeforeArray = pluginMain.loadBefore();
		final String[] libraries = pluginMain.mavenCentralLibraries();
		final Permission[] permissions = pluginMain.permissions();

		yml.put("name", pluginName.equals("") ? "AnTvheeAPIPlugin" : pluginName);
		yml.put("main", main.toString());
		yml.put("api-main", apiMainClass);
		yml.put("version", version);
		yml.put("api-version", apiVersion.toString());

		if(!description.equals(""))
			yml.put("description", description);

		yml.put("load", pluginLoadOrder.toString());

		final List<String> authors = Arrays.asList(authorsArray);

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

			if(!thisPermission.isEmpty())
				permissionsMap.put(permission.name(), thisPermission);
		}

		if(!permissionsMap.isEmpty())
			yml.put("permissions", permissionsMap);

		final List<String> hardDependencies = Arrays.asList(hardDependenciesArray);
		if(!hardDependencies.isEmpty())
			yml.put("depend", hardDependencies);

		final List<String> softDependencies = Arrays.asList(softDependenciesArray);
		if(!softDependencies.isEmpty())
			yml.put("softdepend", softDependencies);

		final List<String> loadBefore = Arrays.asList(loadBeforeArray);
		if(!loadBefore.isEmpty())
			yml.put("loadbefore", loadBefore);

		final List<String> librariesList = Arrays.asList(libraries);
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
			final String pluginBungeeYml = main == PluginLoader.BUKKIT_PLUGIN ? "plugin.yml" : "bungee.yml";
			final Yaml yaml = new Yaml();
			final FileObject file = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", pluginBungeeYml);
			try(final Writer writer = file.openWriter())
			{
				writer.append("#Auto-generated ").append(pluginBungeeYml).append(", generated at ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append(" by ").append(this.getClass().getName()).append("\n");
				final String raw = yaml.dumpAs(yml, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
				writer.write(raw);
				writer.flush();
			}
		}
		catch(IOException e)
		{
			throwError(e.getMessage());
		}
	}

	private TypeElement checkType(Element element, Class<?> parent)
	{
		final TypeElement typeElement;

		if(element instanceof TypeElement)
			typeElement = (TypeElement) element;
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

	private void throwError(String message)
	{
		this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
	}

	private void throwError(String message, Element element)
	{
		this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
	}
}
