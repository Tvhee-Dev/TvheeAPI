package me.tvhee.tvheeapi.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Inherited
public @interface Register
{
	RegistrationType value();
}
