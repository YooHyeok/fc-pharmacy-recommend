package com.recommend.pharmacy.util;

import com.opencsv.CSVReader;
import com.recommend.pharmacy.dto.PharmacyDto;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvUtils {
    public static List<PharmacyDto> convertToPharmacyDtoList() {
        /* C:\Users\yjou7\Downloads */
//        String file = "/Users/yjou7/DownLoads/pharmacy.csv";

        /* C:\Programming\workspace_intelliJ\fastcampus\fc-pharmacy */
        String file = "/Programming/workspace_intelliJ/fastcampus/fc-pharmacy/pharmacy.csv";

        List<List<String>> csvList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] values = null;
            /**
             * values가 null이 아닌 조건을 만족하는동안 실행
             * [실행순서]
             * 1. values= csvReader.readNext()
             * 2. values != null
             */
            while ((values = csvReader.readNext()) != null) {
                /* values : [약국명(0), 주소(1), 전화번호(2), 기준일자(3), 위도(4), 경도(5), 행정동(6)] */
                csvList.add(Arrays.asList(values)); // 배열을 List로 변환 후 csvList에 초기화
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IntStream.range(1, csvList.size()) /* 0번 데이터는 컬럼명 이므로 1번 row 인덱스부터 시작 */
                .mapToObj(index -> {
                    List<String> rowList = csvList.get(index);
                    return PharmacyDto.builder()
                            .pharmacyName(rowList.get(0))
                            .pharmacyAddress(rowList.get(1))
                            .latitude(Double.parseDouble(rowList.get(4)))
                            .latitude(Double.parseDouble(rowList.get(5)))
                            .build();
                }).collect(Collectors.toList());
    }
}
