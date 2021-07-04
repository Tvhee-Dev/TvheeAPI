package me.tvhee.tvheeapi.api.annotations;

import me.tvhee.tvheeapi.api.description.PermissionDefault;

public @interface Permission
{
	String name();

	PermissionDefault permissionDefault() default PermissionDefault.OP;

	String[] allowedChildren() default {};

	String[] disallowedChildren() default {};
}
