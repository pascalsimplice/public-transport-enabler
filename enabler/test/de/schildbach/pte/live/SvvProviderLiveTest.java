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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.schildbach.pte.NetworkProvider.Accessibility;
import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SvvProvider;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.LocationType;
import de.schildbach.pte.dto.NearbyStationsResult;
import de.schildbach.pte.dto.Product;
import de.schildbach.pte.dto.QueryDeparturesResult;
import de.schildbach.pte.dto.QueryTripsResult;

/**
 * @author Andreas Schildbach
 */
public class SvvProviderLiveTest extends AbstractProviderLiveTest
{
	public SvvProviderLiveTest()
	{
		super(new SvvProvider());
	}

	@Test
	public void nearbyStations() throws Exception
	{
		final NearbyStationsResult result = provider.queryNearbyStations(new Location(LocationType.STATION, 60650002), 0, 0);

		print(result);
	}

	@Test
	public void nearbyStationsByCoordinate() throws Exception
	{
		final NearbyStationsResult result = provider.queryNearbyStations(new Location(LocationType.ADDRESS, 47809195, 13054919), 0, 0);

		print(result);
	}

	@Test
	public void queryDepartures() throws Exception
	{
		final QueryDeparturesResult result = provider.queryDepartures(60650002, 0, false);

		print(result);
	}

	@Test
	public void autocompleteIncomplete() throws Exception
	{
		final List<Location> autocompletes = provider.autocompleteStations("Kur");

		print(autocompletes);
	}

	@Test
	public void autocompleteCoverage() throws Exception
	{
		final List<Location> salzburgAutocompletes = provider.autocompleteStations("Salzburg Süd");
		print(salzburgAutocompletes);
		assertThat(salzburgAutocompletes, hasItem(new Location(LocationType.STATION, 60650458)));

		final List<Location> strasswalchenAutocompletes = provider.autocompleteStations("Straßwalchen West");
		print(strasswalchenAutocompletes);
		assertThat(strasswalchenAutocompletes, hasItem(new Location(LocationType.STATION, 60656483)));

		final List<Location> schwarzachAutocompletes = provider.autocompleteStations("Schwarzach Abtsdorf");
		print(schwarzachAutocompletes);
		assertThat(schwarzachAutocompletes, hasItem(new Location(LocationType.STATION, 60656614)));

		final List<Location> trimmelkamAutocompletes = provider.autocompleteStations("Trimmelkam");
		print(trimmelkamAutocompletes);
		assertThat(trimmelkamAutocompletes, hasItem(new Location(LocationType.STATION, 60640776)));
	}

	@Test
	public void shortTrip() throws Exception
	{
		final QueryTripsResult result = queryTrips(new Location(LocationType.STATION, 60650021, 47797036, 13053608, "Salzburg", "Justizgebäude"),
				null, new Location(LocationType.STATION, 60650022, 47793760, 13059338, "Salzburg", "Akademiestraße"), new Date(), true, Product.ALL,
				WalkSpeed.NORMAL, Accessibility.NEUTRAL);
		System.out.println(result);
		assertEquals(QueryTripsResult.Status.OK, result.status);
		assertTrue(result.trips.size() > 0);

		if (!result.context.canQueryLater())
			return;

		final QueryTripsResult laterResult = queryMoreTrips(result.context, true);
		System.out.println(laterResult);

		if (!laterResult.context.canQueryLater())
			return;

		final QueryTripsResult later2Result = queryMoreTrips(laterResult.context, true);
		System.out.println(later2Result);

		if (!later2Result.context.canQueryEarlier())
			return;

		final QueryTripsResult earlierResult = queryMoreTrips(later2Result.context, false);
		System.out.println(earlierResult);
	}
}
