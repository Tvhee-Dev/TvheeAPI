package me.tvhee.tvheeapi.spigot.api.crafting;

public enum CraftingPlace
{
	/**
	 * The place in the crafting table
	 * <p>
	 * FIRST   | SECOND | THIRD
	 * FOURTH  | FIFTH  | SIXTH | -->> RESULT
	 * SEVENTH | EIGHT  | NINTH
	 * </p>
	 */

	FIRST('A'), SECOND('B'), THIRD('C'), FOURTH('D'), FIFTH('E'), SIXTH('F'), SEVENTH('G'), EIGHT('H'), NINTH('I');

	private final char key;

	CraftingPlace(char key)
	{
		this.key = key;
	}

	char getKey()
	{
		return key;
	}
}
