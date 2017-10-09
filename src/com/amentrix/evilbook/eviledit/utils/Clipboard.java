package com.amentrix.evilbook.eviledit.utils;

import java.util.ArrayList;
import java.util.Collections;
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
		Collections.reverse(this.undoList); //TODO: This is a dirty temporary fix to prevent undo issues caused by multiple entries for the same block
		return this.undoList;
	}
	
	public void appendUndo(BlockState blockState) {
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
	
	public int getCopySize() {
		return this.copyList.size();
	}
}
