package com.recommend.pharmacy.domain.direction.service;

import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Base62Service {
    private static final Base62 base62Instance = Base62.createInstance();

    /**
     * [인코딩]
     * DB Sequence값을 받아 Base62 인코딩
     * 즉, 최대 3개로 조회된 약국에 대한 PK로 URI를 대체한다... (위도경도 대신?)
     * @param directionId
     * @return
     */
    public String encodeDirectionId(Long directionId) {
        return new String(base62Instance.encode(String.valueOf(directionId).getBytes()));
    }

    /**
     * [디코딩]
     * @param encodedDirectionId
     * @return
     */
    public Long decodeDirectionId(String encodedDirectionId) {
        String resultDirectionId = new String(base62Instance.decode(String.valueOf(encodedDirectionId).getBytes()));
        return Long.valueOf(resultDirectionId);
    }
}
