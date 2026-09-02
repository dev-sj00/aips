package com.portfolio.aips.project.interaction.view.controller;

import com.portfolio.aips.project.interaction.view.controller.dto.request.IncreaseViewCountRequest;
import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.SaveHeartBeatDTO;
import com.portfolio.aips.project.interaction.view.service.view.ViewService;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/view")
@RequiredArgsConstructor
public class ViewRestController {

    private final ViewService viewService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeatIncreaseViewCountForAnonymous(@RequestBody IncreaseViewCountRequest request, HttpServletRequest httpRequest) {

        boolean isValid = viewService.isHeartBeatValid(new SaveHeartBeatDTO(httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")));

        if(isValid)
        {

            viewService.increaseViewCount(
                    new IncreaseViewCountDTO
                    (request.boardPk(), request.boardType(), null, httpRequest.getRemoteAddr()
                    ));
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Void> increaseViewCount(@RequestBody IncreaseViewCountRequest request, HttpServletRequest httpRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        viewService.increaseViewCount(
                new IncreaseViewCountDTO(request.boardPk(), request.boardType(), customUserDetails.getPk().toString(), httpRequest.getRemoteAddr()
                ));



        return new ResponseEntity<>(HttpStatus.OK);
    }


}
