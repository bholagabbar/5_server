package io.wolfbeacon.server.controller.api.v1;

import io.wolfbeacon.server.controller.BaseController;
import io.wolfbeacon.server.model.Hackathon;
import io.wolfbeacon.server.service.HackathonService;
import io.wolfbeacon.server.util.CoordinateComparator;
import io.wolfbeacon.server.util.HackalistAPI;
import io.wolfbeacon.server.web.exception.UnAuthorizedException;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
public class HackathonController extends BaseController {

    @Autowired
    HackathonService hackathonService;
    @Autowired
    HackalistAPI hackalistAPI;

    @RequestMapping("/api/v1/hackathon/list")
    public
    @ResponseBody
    List<Hackathon> getHackathons(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(value = "start-date", required = false) String startDate,
                                  @RequestParam(value = "end-date", required = false) String endDate,
                                  @RequestParam(value = "sort-by", required = false) String sortBy,
                                  @RequestParam(value = "latitude", required = false) Double latitude,
                                  @RequestParam(value = "longitude", required = false) Double longitude,
                                  @RequestParam(value = "count", required = false) Integer count) throws Exception {
        CommonProfile profile = getProfile(request, response);
        if (profile == null) {
            throw new UnAuthorizedException();
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedStartDate = null, parsedEndDate = null;
        if (startDate != null) {
            parsedStartDate = format.parse(startDate);
        }
        if (endDate != null) {
            parsedEndDate = format.parse(endDate);
        }
        List<Hackathon> hackathonList = hackathonService.getHackathonsBetweenDate(parsedStartDate, parsedEndDate, sortBy, count);
        if (sortBy != null && sortBy.equals("distance") && latitude != null && longitude != null) {
            Collections.sort(hackathonList, new CoordinateComparator(latitude, longitude));
        }
        return hackathonList;
    }
}