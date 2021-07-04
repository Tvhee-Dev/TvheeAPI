package me.tvhee.tvheeapi.api.annotations;

import me.tvhee.tvheeapi.api.description.ApiVersion;
import me.tvhee.tvheeapi.api.description.PluginLoadOrder;

public @interface PluginMain
{
	String pluginName();

	String version();

	ApiVersion apiVersion();

	boolean bungeeSupport();

	boolean bukkitSupport();

	String description() default "";

	PluginLoadOrder loadOrder() default PluginLoadOrder.POSTWORLD;

	String[] authors() default {};

	String website() default "";

	String logPrefix() default "";

	String[] dependencies() default {};

	String[] softDependencies() default {};

	String[] loadBefore() default {};

	String[] mavenCentralLibraries() default {};

	Permission[] permissions() default {};
}
