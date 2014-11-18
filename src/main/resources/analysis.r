PropertyDataRoot = "/Users/haines/";

header <- c("listingId","latitude","longitude","country","county","displayableAddress","streetName","PostTown","Outcode","type","numBathrooms","numBedrooms","numFloors","numReceptions","isNewBuild","agentAddress","agentLogo","agentName","agentPhone","detailsUrl","description","imageCaption","imageUrl","priceModifier","shortDescription","thumbnailUrl","status","searchPostcode","firstPublishedDate","lastPublishedDate","extractionTime","priceDate","price");

SEP <- ",";

propertyListings <- data.frame(read.table(paste(PropertyDataRoot, "zooplaStore.txt", sep=""), quote="\"", header=F, comment.char="", sep=SEP, stringsAsFactors=F))

propertyListings$Date <- as.Date(propertyListings$priceDate, "%Y%B%d %H:%M%S")

colnames(propertyListings) <- header;

propertyListings <- propertyListings[propertyListings$price != 0,];

sales <- propertyListings[propertyListings$status == 'FOR_SALES' | propertyListings$status == 'SOLD' | propertyListings$status == 'SALE_UNDER_OFFER', ];

localEnv <- environment();

sales <- sales[sales$type != 'PARKING_GARAGE' & sales$type != 'MOBILE_PARK_HOME' & sales$type != 'LAND' & sales$type != 'HOUSE_BOAT', ]

p <- ggplot(sales, aes(x=log(price), fill=searchPostcode), environment = localEnv) + labs(title=paste("test", "histogram"), dataset="Time", y="Frequency", x="Price") + geom_histogram(binwidth=0.2, alpha=.5, position="identity")
p

sales <- data.table(sales);

byProperty <- sales[, list(count=nrow(.SD), min=as.double(min(price)), max=as.double(max(price)), median=median(price)), by=c('searchPostcode')];
byProperty[type == 'TOWN_HOUSE' | type== 'FLAT', ]
