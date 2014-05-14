package com.amentrix.evilbook.eviledit.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockState;

import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.Emitter;

/**
 * EvilEdit clipboard instance
 * @author Reece Aaron Lecrivain
 */
public class Clipboard {
	private List<BlockState> undoList = new ArrayList<>(), copyList = new ArrayList<>(); 
	public List<Emitter> undoEmitterList = new ArrayList<>(), copyEmitterList = new ArrayList<>();
	public List<DynamicSign> undoDynamicSignList = new ArrayList<>(), copyDynamicSignList = new ArrayList<>();

	public List<BlockState> getUndo() {
		return this.undoList;
	}
	
	public void appendUndo(BlockState blockState) {
		for (BlockState state : undoList) if (state.getLocation().equals(blockState.getLocation())) return;
		this.undoList.add(blockState);
	}
	
	public void clearUndo() {
		this.undoList = new ArrayList<>();
	}
	
	public List<BlockState> getCopy() {
		return this.copyList;
	}
	
	public void appendCopy(BlockState blockState) {
		this.copyList.add(blockState);
	}
	
	public void clearCopy() {
		this.copyList = new ArrayList<>();
	}
}
