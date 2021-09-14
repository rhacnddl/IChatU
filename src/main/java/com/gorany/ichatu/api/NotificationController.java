package com.gorany.ichatu.api;

import com.gorany.ichatu.dto.NotificationDTO;
import com.gorany.ichatu.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/v1/notifications")
@CrossOrigin({"https://ichatu.ga", "http://localhost:3000"})
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{memberId}/member/{page}")
    @ApiOperation(value = "알림 목록 조회", notes = "알림 목록을 페이징하여 조회한다.")
    public ResponseEntity<List<NotificationDTO>> getNotificationList(@PathVariable("memberId") Long memberId, @PathVariable("page") Integer page){

        log.info("# NotificationController -> getNotificationList(Long, Integer)");
        List<NotificationDTO> dtoList = notificationService.getNotificationList(memberId, page);

        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    /* deprecated */
    @PostMapping("/{id}/confirm")
    @ApiOperation(value = "단건 알림 확인", notes = "특정 알림 하나를 확인 처리한다.")
    public ResponseEntity<String> checkNotification(@PathVariable("id") Long notificationId){

        log.info("# NotificationController -> checkNotification(Long)");
        Integer updateCount = notificationService.checkNotification(notificationId);

        return updateCount == 1? new ResponseEntity<>("success", HttpStatus.OK) :
                new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{memberId}")
    @ApiOperation(value = "미확인 알림 개수 확인", notes = "확인 처리하지 않은 알림의 개수를 반환한다.")
    public ResponseEntity<Long> getNotConfirmedCount(@PathVariable("memberId") Long memberId){

        Long count = notificationService.getNotConfirmedCount(memberId);

        log.info("# NotificationController -> getNotConfirmedCount : {}", count);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PutMapping("/{memberId}")
    @ApiOperation(value = "수신한 알림 모두 확인", notes = "받은 알림을 모두 확인 처리한다.")
    public ResponseEntity<Integer> checkNotifications(@PathVariable("memberId") Long memberId){

        Integer updateCount = notificationService.checkNotifications(memberId);
        log.info("# NotificationController -> checkNotifications(Long) : {}", updateCount);

        return new ResponseEntity<>(updateCount, HttpStatus.OK);
    }

    @DeleteMapping("/{memberId}")
    @ApiOperation(value = "확인한 알림을 모두 제거", notes = "확인 처리된 알림을 모두 제거한다.")
    public ResponseEntity<Integer> removeNotifications(@PathVariable("memberId") Long memberId){

        Integer removeCount = notificationService.removeNotifications(memberId);
        log.info("# NotificationController -> removeNotifications(Long) : {}", removeCount);

        return new ResponseEntity<>(removeCount, HttpStatus.OK);
    }

    @PutMapping("/room/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "특정 채팅방의 확인하지 않은 알림 모두 확인 처리", notes = "사용자가 채팅방 입장 시 쌓인 알림을 모두 확인 처리한다.")
    public ResponseEntity<Integer> updateNotificationsOnRoom(@PathVariable("chatRoomId") Long chatRoomId, @PathVariable("memberId") Long memberId){

        log.info("# NotificationController -> updateNotificationsOnRoom(Long, Long) : {}, {}", chatRoomId, memberId);
        Integer count = notificationService.updateNotificationsByChatRoomAndMember(chatRoomId, memberId);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
