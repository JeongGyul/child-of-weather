package com.childofweather.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GridAddressLoader {

    private static final Map<String, String> gridToAddress = new HashMap<>();

    public static Map<String, String> getMap() {
        return gridToAddress;
    }

    static {
        try {
            // ZIP Bomb 검사 완화
            ZipSecureFile.setMinInflateRatio(0);

            loadExcel();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("⚠️ 행정구역 엑셀 로딩 실패");
        }
    }

    private static void loadExcel() throws Exception {

        InputStream is = GridAddressLoader.class.getResourceAsStream("/grid_address.xlsx");

        if (is == null) {
            throw new IllegalStateException("grid_address.xlsx 리소스를 찾을 수 없습니다.");
        }

        Workbook workbook = new XSSFWorkbook(is);

        // 엑셀의 실질 데이터 시트 이름
        Sheet sheet = workbook.getSheet("최종 업데이트 파일_20251027");
        if (sheet == null) sheet = workbook.getSheetAt(0);

        // 1행 헤더, 2행부터 데이터
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            // 컬럼 인덱스 매핑 (확정됨!)
            String sido = readString(row.getCell(2));      // 1단계 (시/도)
            String sigungu = readString(row.getCell(3));   // 2단계 (시/구/군)
            String dong = readString(row.getCell(4));      // 3단계 (읍/면/동)

            String nxStr = readString(row.getCell(5));     // 격자 X
            String nyStr = readString(row.getCell(6));     // 격자 Y

            if (nxStr.isEmpty() || nyStr.isEmpty()) continue;

            int nx, ny;
            try {
                nx = Integer.parseInt(nxStr);
                ny = Integer.parseInt(nyStr);
            } catch (Exception e) {
                continue;
            }

            // 주소 문자열 구성
            String address = buildAddress(sido, sigungu, dong);

            String key = nx + "-" + ny;
            gridToAddress.put(key, address);

        }

        workbook.close();
        is.close();

        System.out.println("📌 행정구역 격자 데이터 로딩 완료: " + gridToAddress.size() + "개");
    }

    private static String readString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double v = cell.getNumericCellValue();
                if (v == Math.floor(v)) return String.valueOf((int)v);
                else return String.valueOf(v);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private static String buildAddress(String sido, String sigungu, String dong) {

        // 1. null 또는 빈 값 제거
        sido = sido == null ? "" : sido.trim();
        sigungu = sigungu == null ? "" : sigungu.trim();
        dong = dong == null ? "" : dong.trim();

        StringBuilder sb = new StringBuilder();

        // 2. 시/도
        if (!sido.isEmpty()) {
            sb.append(sido);

            // "경기도부천시"처럼 시/도 + 시가 붙어있는 경우 → 자동 분리
            if (sido.endsWith("도") || sido.endsWith("시")) {
                sb.append(" ");
            }
        }

        // 3. 시/군/구 (sigungu)
        if (!sigungu.isEmpty()) {

            // 만약 sido에 이미 시 정보가 포함된 경우 → 시/구 or 시/군만 출력
            // 예) sido="경기도", sigungu="부천시오정구"
            // → "경기도 부천시 오정구"
            String temp = sigungu;

            // case 1: "부천시" + "오정구" 구조
            if (temp.contains("시") && (temp.contains("구") || temp.contains("군"))) {

                int siIdx = temp.indexOf("시");
                sb.append(temp, 0, siIdx + 1).append(" ");    // 부천시

                String rest = temp.substring(siIdx + 1).trim(); // 오정구
                if (!rest.isEmpty()) {
                    sb.append(rest).append(" ");
                }
            } else {
                // 기본 케이스
                sb.append(temp).append(" ");
            }
        }

        // 4. 읍/면/동
        if (!dong.isEmpty()) {
            sb.append(dong);
        }

        return sb.toString().trim();
    }

}
