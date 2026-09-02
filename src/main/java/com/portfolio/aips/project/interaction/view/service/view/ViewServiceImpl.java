package com.portfolio.aips.project.interaction.view.service.view;

import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.ViewRedisRepository;
import com.portfolio.aips.project.interaction.view.repo.ViewRepository;
import com.portfolio.aips.project.interaction.view.repo.dto.request.ExistsViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.IncreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.request.SaveHeartBeatDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.result.FindByHbKeyResult;
import com.portfolio.aips.project.interaction.view.service.view.command.CreateViewCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewServiceImpl implements ViewService {
    private final ViewRedisRepository viewRedisRepository;
    private static final long HEARTBEAT_TIME_PERIOD = 20_000; //두번 하트비트 요청 시(40초) 간격 20초 이상
    private final ViewRepository viewRepository;

    @Override
    public void increaseViewCount(IncreaseViewCountDTO dto) {


            viewRedisRepository.increaseViewCount(dto);





    }

    @Override
    public boolean isHeartBeatValid(SaveHeartBeatDTO dto) {
        String hbKey = viewRedisRepository.saveHeartBeat(dto);

        FindByHbKeyResult result = viewRedisRepository.findByHbKey(hbKey);

        if(result.times() == null)
        {
            return false;
        }

        List<Object> times = result.times();
        long first = Long.parseLong(times.get(0).toString());
        long last  = Long.parseLong(times.get(1).toString());

        if (last - first >= HEARTBEAT_TIME_PERIOD) {

           viewRedisRepository.deleteKey(hbKey);
           log.info("하트비트 체크 완료 {}", hbKey);

           return true;
        }

        if(result.size() >= 2)
        {
            //사용자가 자바스크립트 조작 시 20초 이하 간격으로 레디스에 쌓이므로 삭제해줘야함
            log.info("간격이 20초 이하이므로 삭제 {}", hbKey);
            viewRedisRepository.deleteKey(hbKey);
        }







        return false;
    }

    @Override
    public void createView(CreateViewCommand command) {

        viewRepository.save(new ViewEntity(null, command.boardPk(),  command.boardType(), command.viewCount()));
    }


}
