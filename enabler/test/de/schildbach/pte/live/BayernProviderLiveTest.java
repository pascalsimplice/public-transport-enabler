/*
 * Copyright 2010-2014 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.schildbach.pte.live;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.schildbach.pte.BayernProvider;
import de.schildbach.pte.NetworkProvider.Accessibility;
import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.LocationType;
import de.schildbach.pte.dto.NearbyStationsResult;
import de.schildbach.pte.dto.Product;
import de.schildbach.pte.dto.QueryDeparturesResult;
import de.schildbach.pte.dto.QueryTripsResult;

/**
 * @author Andreas Schildbach
 */
public class BayernProviderLiveTest extends AbstractProviderLiveTest
{
	public BayernProviderLiveTest()
	{
		super(new BayernProvider());
	}

	@Test
	public void nearbyStations() throws Exception
	{
		final NearbyStationsResult result = provider.queryNearbyStations(new Location(LocationType.STATION, 3001459), 0, 0);

		print(result);
	}

	@Test
	public void nearbyStationsByCoordinate() throws Exception
	{
		final NearbyStationsResult result = provider.queryNearbyStations(new Location(LocationType.ADDRESS, 48135232, 11560650), 0, 0);

		print(result);
	}

	@Test
	public void queryDepartures() throws Exception
	{
		final QueryDeparturesResult munichOstbahnhof = provider.queryDepartures(80000793, 0, false);
		print(munichOstbahnhof);

		final QueryDeparturesResult munichHauptbahnhof = provider.queryDepartures(80000689, 0, false);
		print(munichHauptbahnhof);

		final QueryDeparturesResult nurembergHauptbahnhof = provider.queryDepartures(80001020, 0, false);
		print(nurembergHauptbahnhof);
	}

	@Test
	public void autocompleteIncomplete() throws Exception
	{
		final List<Location> autocompletes = provider.autocompleteStations("Marien");

		print(autocompletes);
	}

	@Test
	public void autocompleteWithUmlaut() throws Exception
	{
		final List<Location> autocompletes = provider.autocompleteStations("grün");

		print(autocompletes);
	}

	@Test
	public void autocompleteAddress() throws Exception
	{
		final List<Location> autocompletes = provider.autocompleteStations("München, Friedenstraße 2");

		print(autocompletes);
	}

	@Test
	public void autocompleteLocal() throws Exception
	{
		final List<Location> autocompleteRegensburg = provider.autocompleteStations("Regensburg");
		assertEquals(80001083, autocompleteRegensburg.iterator().next().id);

		final List<Location> autocompleteMunich = provider.autocompleteStations("München");
		assertEquals(80000689, autocompleteMunich.iterator().next().id);

		final List<Location> autocompleteNuremberg = provider.autocompleteStations("Nürnberg");
		assertEquals(80001020, autocompleteNuremberg.iterator().next().id);
	}

	@Test
	public void shortTrip() throws Exception
	{
		final QueryTripsResult result = queryTrips(new Location(LocationType.STATION, 80000793, "München", "Ostbahnhof"), null, new Location(
				LocationType.STATION, 80000799, "München", "Pasing"), new Date(), true, Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL);
		System.out.println(result);
		final QueryTripsResult laterResult = queryMoreTrips(result.context, true);
		System.out.println(laterResult);
	}

	@Test
	public void longTrip() throws Exception
	{
		final QueryTripsResult result = queryTrips(new Location(LocationType.STATION, 1005530, "Starnberg", "Arbeitsamt"), null, new Location(
				LocationType.STATION, 3001459, "Nürnberg", "Fallrohrstraße"), new Date(), true, Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL);
		System.out.println(result);
		// seems like there are no more trips all the time
	}

	@Test
	public void tripBetweenCoordinates() throws Exception
	{
		final QueryTripsResult result = queryTrips(new Location(LocationType.ADDRESS, 0, 48165238, 11577473), null, new Location(
				LocationType.ADDRESS, 0, 47987199, 11326532), new Date(), true, Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL);
		System.out.println(result);
		final QueryTripsResult laterResult = queryMoreTrips(result.context, true);
		System.out.println(laterResult);
	}

	@Test
	public void tripBetweenCoordinateAndStation() throws Exception
	{
		final QueryTripsResult result = queryTrips(new Location(LocationType.ADDRESS, 0, 48238341, 11478230), null, new Location(
				LocationType.STATION, 80000793, "München", "Ostbahnhof"), new Date(), true, Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL);
		System.out.println(result);
		final QueryTripsResult laterResult = queryMoreTrips(result.context, true);
		System.out.println(laterResult);
	}

	@Test
	public void tripBetweenAddresses() throws Exception
	{
		final QueryTripsResult result = queryTrips(new Location(LocationType.ADDRESS, 0, null, "München, Maximilianstr. 1"), null, new Location(
				LocationType.ADDRESS, 0, null, "Starnberg, Jahnstraße 50"), new Date(), true, Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL);
		System.out.println(result);
		final QueryTripsResult laterResult = queryMoreTrips(result.context, true);
		System.out.println(laterResult);
	}

	@Test
	public void tripBetweenStationAndAddress() throws Exception
	{
		final QueryTripsResult result = queryTrips(new Location(LocationType.STATION, 1001220, null, "Josephsburg"), null, new Location(
				LocationType.ADDRESS, 0, 48188018, 11574239, null, "München Frankfurter Ring 35"), new Date(), true, Product.ALL, WalkSpeed.NORMAL,
				Accessibility.NEUTRAL);
		System.out.println(result);
		final QueryTripsResult laterResult = queryMoreTrips(result.context, true);
		System.out.println(laterResult);
	}
}
