package me.tvhee.tvheeapi.api.annotations;

import me.tvhee.tvheeapi.api.description.PermissionDefault;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface Permission
{
	String name();

	PermissionDefault permissionDefault() default PermissionDefault.OP;

	String[] allowedChildren() default {};

	String[] disallowedChildren() default {};
}
