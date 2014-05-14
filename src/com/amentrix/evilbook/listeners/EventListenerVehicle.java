package com.amentrix.evilbook.listeners;

import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import com.amentrix.evilbook.main.EvilBook;

/**
 * Vehicle event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerVehicle implements Listener {
	/**
	 * Called when a vehicle is created
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onVehicleCreate(VehicleCreateEvent event) {
		if (event.getVehicle().getNearbyEntities(64, 64, 64).size() >= 400) {
			event.getVehicle().remove();
		} else if (!EvilBook.isInSurvival(event.getVehicle()) && event.getVehicle() instanceof Boat) {
			Boat boat = (Boat) event.getVehicle();
			boat.setWorkOnLand(true);
		}
	}
}
