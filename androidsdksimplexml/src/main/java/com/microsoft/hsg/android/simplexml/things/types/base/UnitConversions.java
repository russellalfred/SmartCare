/*
 * Copyright 2011 Microsoft Corp.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.microsoft.hsg.android.simplexml.things.types.base;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name=UnitConversions.RootElement)
public class UnitConversions {
	
	public static final String RootElement = "unit-conversions";
	
	@Element(name = "base-unit-conversion")
	protected BaseUnitConversion baseUnitConversion;

	public BaseUnitConversion getBaseUnitConversion() {
		return baseUnitConversion;
	}

	public void setBaseUnitConversion(BaseUnitConversion baseUnitConversion) {
		this.baseUnitConversion = baseUnitConversion;
	}
}
