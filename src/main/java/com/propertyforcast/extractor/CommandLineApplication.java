package com.propertyforcast.extractor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyforcast.dao.CsvListingStoreDao;
import com.propertyforcast.dao.RestZooplaDao;
import com.propertyforcast.extractor.zoopla.ExistingListingPredicateProvider;
import com.propertyforcast.extractor.zoopla.ZooplaExtractor;
import com.propertyforcast.time.Clock;

public class CommandLineApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(CommandLineApplication.class);

	private static final String DEFAULT_ZOOPLA_API_KEY = "vr35z3ruhft4fwr5dz3h8kx6";
	private static final String STORE_FILE_LOCATION_CMD_OPTION = "storeLocation";
	private static final String ZOOPLA_SEARCH_RADIUS_CMD_OPTION = "searchRadius";
	private static final String POSTCODES_CMD_OPTION = "postcodes";
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, ParseException{
		
		Options options = new Options();
		options.addOption(OptionBuilder.hasArg(true).isRequired(true).create(STORE_FILE_LOCATION_CMD_OPTION));
		options.addOption(OptionBuilder.hasArg(true).isRequired(true).create(ZOOPLA_SEARCH_RADIUS_CMD_OPTION));
		options.addOption(OptionBuilder.hasArg(true).isRequired(true).create(POSTCODES_CMD_OPTION));
		
		try{
			CommandLineParser parser = new BasicParser();
			
			
			
			CommandLine cmd = parser.parse(options, args);
			
			Clock clock = Clock.SYSTEM_CLOCK;
			
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
			connManager.setMaxTotal(10);
			
			Path storeLocation = Paths.get(cmd.getOptionValue(STORE_FILE_LOCATION_CMD_OPTION));
			
			CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
			
			RestZooplaDao dao = new RestZooplaDao(client, DEFAULT_ZOOPLA_API_KEY, clock);
			
			CsvListingStoreDao store = new CsvListingStoreDao(storeLocation, new ExistingListingPredicateProvider(storeLocation));
			
			ZooplaExtractor extractor = new ZooplaExtractor(dao, store, clock, Double.parseDouble(cmd.getOptionValue(ZOOPLA_SEARCH_RADIUS_CMD_OPTION)));
			
			Iterable<String> postcodes = getPostcodes(cmd.getOptionValue(POSTCODES_CMD_OPTION));
			
			while(true){
				LOG.debug("Extracting listings");
				
				extractor.extract(postcodes);
			}
		} catch (ParseException e){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java zoopla-extractor.jar", options );
		}
	}

	private static Iterable<String> getPostcodes(String postcodeString) {
		
		return Arrays.asList(postcodeString.split(","));
	}
}
