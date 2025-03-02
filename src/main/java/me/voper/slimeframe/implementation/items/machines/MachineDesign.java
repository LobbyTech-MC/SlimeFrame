package me.voper.slimeframe.implementation.items.machines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;

// Credits to Mooy1
// https://github.com/Mooy1/InfinityLib/blob/master/src/main/java/io/github/mooy1/infinitylib/machines/MachineLayout.java

@Setter
@Getter
@With
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public final class MachineDesign {

    private int[] inputBorder;
    private int[] inputSlots;
    private int[] outputBorder;
    private int[] outputSlots;
    private int[] background;
    private int statusSlot;
    private int selectorSlot;

    public static final MachineDesign CRAFTING_MACHINE = new MachineDesign()
            .background(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44})
            .inputBorder(new int[]{9, 10, 11, 12, 18, 21, 27, 28, 29, 30})
            .inputSlots(new int[]{19, 20})
            .outputBorder(new int[]{14, 15, 16, 17, 23, 26, 32, 33, 34, 35})
            .outputSlots(new int[]{24, 25})
            .statusSlot(22);

    public static final MachineDesign SELECTOR_MACHINE = CRAFTING_MACHINE
            .withSelectorSlot(4);


	private MachineDesign inputBorder(int[] inputBorder) {
		this.inputBorder = inputBorder;
		return this;
	}
	private MachineDesign inputSlots(int[] inputSlots) {
		this.inputSlots = inputSlots;
		return this;
	}
	private MachineDesign outputBorder(int[] outputBorder) {
		this.outputBorder = outputBorder;
		return this;
	}
	private MachineDesign outputSlots(int[] outputSlots) {
		this.outputSlots = outputSlots;
		return this;
	}
	private MachineDesign statusSlot(int statusSlot) {
		this.statusSlot = statusSlot;
		return this;
	}
	
	private MachineDesign background(int[] background) {
		this.background = background;
		return this;
	}

	private MachineDesign withSelectorSlot(int selectorSlot) {
		this.selectorSlot = selectorSlot;
		return this;
	}
	public int[] inputBorder() {
		return inputBorder;
	}
	public int[] outputBorder() {
		return outputBorder;
	}
	public int[] inputSlots() {
		return inputSlots;
	}
	public int[] outputSlots() {
		return outputSlots;
	}
	public int statusSlot() {
		return statusSlot;
	}
	public int selectorSlot() {
		return selectorSlot;
	}
	public int[] background() {
		return background;
	}

}
