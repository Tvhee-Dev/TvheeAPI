package me.tvhee.tvheeapi.api.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

@Inherited
public @interface Require
{
	Class<? extends Annotation> value();
}
