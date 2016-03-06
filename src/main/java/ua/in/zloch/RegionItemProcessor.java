package ua.in.zloch;

import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import ua.in.zloch.entity.Region;

public class RegionItemProcessor implements ItemProcessor<Region, Region> {

    private static final Logger log = LoggerFactory.getLogger(RegionItemProcessor.class);

    @Override
    public Region process(final Region region) throws Exception {
        region.setBoundaries("");

        log.info("Processing the region: " + region.getName() + "...");

        return region;
    }

}