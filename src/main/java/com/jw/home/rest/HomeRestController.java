package com.jw.home.rest;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jw.home.rest.dto.AddHomeDto;
import com.jw.home.domain.Home;
import com.jw.home.domain.Home.Room;
import com.jw.home.service.HomeService;

import reactor.core.publisher.Mono;

//@RestController
//@RequestMapping("/api/v1/homes")
public class HomeRestController {

//    @Autowired
    private HomeService homeService;

//    @PostMapping
    public Mono<Home> createHome(@RequestBody AddHomeDto homeDto) {
        Home home = new Home();
        home.setHomeName(homeDto.getHomeName());
        home.setTimezone(homeDto.getTimezone());
        home.setSecurityMode(homeDto.getSecurityMode());
        home.setRooms(homeDto.getRooms().stream()
                .map(s -> {
                    Room room = new Room();
                    room.setRoomName(s.getRoomName());
                    return room;
                }).collect(Collectors.toList()));

        Mono<Home> addHome = homeService.addHome("jwryu", home)
                .doOnNext(h -> System.out.println(h))
                .doOnError(e -> {
                    System.out.println("JWRYU >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    e.printStackTrace();
                });

        return addHome;


//		try {
//			homeService.addHome();
//		} catch (HomeLimitException e) {
//			Server
//		} catch (HomeDuplicatedException e) {
//		
//		}

//		.doOnError(e -> {
//			if (e instanceof HomeDuplicatedException) {
//
////				return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(null)
//			} else if (e instanceof HomeLimitException) {
//				
//			}
//		});
//		// TODO 공통 권한검사
//		return ServerResponse.
//				.bodyValue(null);
    }
}
