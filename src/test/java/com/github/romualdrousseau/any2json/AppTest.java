package com.github.romualdrousseau.any2json;

import java.nio.file.Paths;

import com.github.romualdrousseau.any2json.classifiers.NGramNNClassifier;
import com.github.romualdrousseau.shuju.json.JSON;

import java.nio.file.Path;
import java.net.URL;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
//    @Test
    public void testReadVariousDocuments() {
        Document document = null;
        Sheet sheet = null;
        Table table = null;
        Header header = null;
        int fileNo = 0;

        for (String[] expectedValues : scenarios1) {
            int state = 0;
            int headerIndex = 0;
            for (String expectedValue : expectedValues) {
                switch (state) {
                case 0:
                    document = loadDocument(expectedValue, "CP949");
                    sheet = document.getSheetAt(0);
//                    table = sheet.findTable(30, 30);
//                    assert !Table.IsEmpty(table);
                    state = 1;
                    break;
                case 1:
                    assertEquals(fileNo + ": Sheet name", expectedValue, sheet.getName());
                    state = 2;
                    break;
                case 2:
                    assertEquals(fileNo + ": Number of Rows", expectedValue, "" + table.getNumberOfRows());
                    state = 3;
                    break;
                case 3:
//                    header = table.getHeaderAt(headerIndex);
                    assertEquals(fileNo + ": Header name", expectedValue, header.getName());
                    state = 4;
                    break;
                case 4:
//                    assertEquals(fileNo + ": Value of <" + header.getName() + ">", expectedValue,
//                            table.getRowAt(0).getCellValue(header));
                    headerIndex++;
                    state = 3;
                    break;
                }
            }
            document.close();
            fileNo++;
        }
    }

    /**
     * Test to read various documents, tgas the headers and check the first line
     */
//    @Test
    public void testTagsVariousDocuments() {
        Document document = null;
        Sheet sheet = null;
        Table table = null;
        Header header = null;
        int fileNo = 0;

        NGramNNClassifier Brain = new NGramNNClassifier(
                JSON.loadJSONObject(getResourcePath("/data/all.json").toString()));

        for (String[] expectedValues : scenarios2) {
            int state = 0;
            for (String expectedValue : expectedValues) {
                switch (state) {
                case 0:
                    document = loadDocument(expectedValue, "CP949");
                    sheet = document.getSheetAt(0);
//                    table = sheet.findTableWithIntelliTag(Brain);
//                    assert !Table.IsEmpty(table);
                    state = 1;
                    break;
                case 1:
//                    header = table.getHeaderByTag("DATE");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <date>", expectedValue, null);
                    }
                    state = 2;
                    break;
                case 2:
//                    header = table.getHeaderByTag("PRODUCT_NAME");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <product-name>", expectedValue, null);
                    }
                    state = 3;
                    break;
                case 3:
//                    header = table.getHeaderByTag("PRODUCT_PACKAGE");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <product-package>", expectedValue, null);
                    }
                    state = 4;
                    break;
                case 4:
//                    header = table.getHeaderByTag("QUANTITY");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <quantity>", expectedValue, null);
                    }
                    state = 5;
                    break;
                case 5:
                    // header = table.getHeaderByTag("unit-price");
                    // if(header != null) {
                    // assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
                    // table.getRowAt(0).getCellValue(header, true));
                    // }
                    // else {
                    // assertEquals(fileNo + ": Value of <unit-price>", expectedValue, null);
                    // }
                    state = 6;
                    break;
                case 6:
//                    header = table.getHeaderByTag("AMOUNT");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <amount>", expectedValue, null);
                    }
                    state = 7;
                    break;
                case 7:
//                    header = table.getHeaderByTag("CUSTOMER_NAME");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <customer-name>", expectedValue, null);
                    }
                    state = 8;
                    break;
                case 8:
//                    header = table.getHeaderByTag("CUSTOMER_TYPE");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <customer-type>", expectedValue, null);
                    }
                    state = 9;
                    break;
                case 9:
//                    header = table.getHeaderByTag("POSTAL_CODE");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <postal-code>", expectedValue, null);
                    }
                    state = 10;
                    break;
                case 10:
//                    header = table.getHeaderByTag("ADDRESS");
                    if (header != null) {
//                        assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
//                                table.getRowAt(0).getCellValue(header, true));
                    } else {
                        assertEquals(fileNo + ": Value of <address>", expectedValue, null);
                    }
                    state = 11;
                    break;
                case 11:
                    // header = table.getHeaderByTag("CUSTOMER_NAME");
                    // if(header != null) {
                    // assertEquals(fileNo + ": Value of <" + header.getTag() + ">", expectedValue,
                    // table.getRowAt(0).getCellValue(header, true));
                    // }
                    // else {
                    // assertEquals(fileNo + ": Value of <customer>", expectedValue, null);
                    // }
                    state = 12;
                    break;
                }
            }
            document.close();
            fileNo++;
        }
    }

    private Document loadDocument(String resourceName, String encoding) {
        return DocumentFactory.createInstance(getResourcePath(resourceName).toString(), encoding);
    }

    private Path getResourcePath(String resourceName) {
        try {
            URL resourceUrl = getClass().getResource(resourceName);
            assert resourceUrl != null;
            return Paths.get(resourceUrl.toURI());
        } catch (URISyntaxException x) {
            assert false : x.getMessage();
            return null;
        }
    }

    private String[][] scenarios1 = {
            { "/data/강원동양.xls", "한국세르비에", "22", "매출처", "강릉아산병원", "매출일", "2016/06/01", "품 명", "디아미크롱서방정", "규 격",
                    "30mg/500T", "수 량", "1", "대표자", "하현권", "주 소", "강원도 강릉시 방동길 38 (사천면)", "비 고", "" },
            { "/data/강원백제.xls", "강원백제", "352", "메이커명", "한국세르비에", "약국명", "약국", "사업자번호", "2240346108", "제품명",
                    "디아미크롱서방정30mg 500T", "출하수량", "1", "단가", "59000", "출하금액", "59000", "주소",
                    "강원도 원주시 중앙로 86, 1동 102호 (중앙동)", "우편번호", "220-010", "날짜", "2016-06-02", "표준코드", "8806763000422" },
            { "/data/강원서호.xls", "work", "56", "매출처", "강릉약국(신)", "제품명", "헤파멜즈산", "재고", "74", "규격", "5g/100포", "현보험가",
                    "57,000.00", "수량", "8", "매출단가", "57,000.00", "매출금액", "456,000", "주소", "강원도 강릉시 방동길", "우편번호",
                    "25440" },
            { "/data/강원순천당.xls", "work", "101", "매출처", "강릉시민약국", "제품명", "디아미크롱정 100T(PTP)", "재고", "13", "규격", "100T",
                    "현보험가", "11,900.00", "수량", "1", "매출단가", "11,900.00", "매출금액", "11,900", "사업자등록번호", "226-01-84424",
                    "주소", "강원도 강릉시 경강로 2109 (임당동)", "대표자", "최종정외 2명", "거래처종류", "약국", "우편번호", "25532", "전화번호",
                    "642-8255", "판매일자", "2016/06/07", "표준코드", "8806447031711" },
            { "/data/광주순천호남.XLS", "광주순천호남", "218", "거래처코드", "21216", "상호", "고흥)고흥병원", "장부일자", "2016/06/01", "품명",
                    "아서틸정4mg", "포장", "90T", "수량", "2", "매출단가", "43,920", "매출금액", "87,840", "유형", "일반매출", "사업자번호",
                    "413-82-01929", "대표자", "류형식", "우편번호", "59535", "신우편번호", "59535", "주소(번지)",
                    "전라남도 고흥군 고흥읍고흥로, 1935 (남계리,숙소) 남계 186번지", "주소(동)", "전라남도 고흥군 고흥읍고흥로, 1935", "약가코드", "676300060",
                    "담당자", "김정호", "부서명", "90.영업3팀", "제조원", "한국세르비에", "대표코드", "8806763000606", "표준코드", "8806763000613",
                    "판매분류", "01.정상품", "기타분류", "" },
            { "/data/대구경일.xls", "매출현황", "24", "년월", "201611", "매입처", "세르비에", "제약사", "세르비에", "상품명", "프로코라란정5mg", "규격/단위",
                    "56T(P)", "수량", "-1", "상대처", "안동성소", "주소", "경상북도안동시서동문로" },
            { "/data/대구지오팜.xls", "한화제약_201612_보험", "2076", "제품명", "그로리정97.875mg(병)", "규격", "30T", "보험코드", "651601730",
                    "수량", "1", "매출일자", "12/2/16", "거래처분류", "약국", "우편번호", "719-801", "주소", "경북 성주군 성주읍" },
            { "/data/대전경동팜.xlsx", "Sheet1", "161", "일자", "2016/12/05", "제 품 명", "디아미크롱서방", "규격", "30/500T", "매출처",
                    "수원123약국", "수량", "2", "현보험약가", "59,000.00", "보험금액", "118,000", "우편번호", "16501", "주소",
                    "경기도 수원시 영통구 월드컵로", "표준코드", "8806763000422" },
            { "/data/대전엘에스팜.xls", "Sheet1", "36", "순번", "1", "도매등록번호", "3038130270", "거래일자", "20170623", "제품코드",
                    "8806763000422", "코드유형", "표준코드", "제조원", "한국세르비에(주)", "품명 ▲", "디아미크롱서방정30mg", "규격", "500T", "평균판매가",
                    "59,000.00", "수량", "3", "매출액", "177,000", "우편번호", "31151", "주소", "충청남도 천안시 동남구 순천향4길, 50", "주소1",
                    "충청남도 천안시 동남구", "출고처등록번호", "7811500011", "출고처", "천안)순천당약국(신)", "거래처구분", "약국", "입력일시",
                    "20170623134726", "보험구분", "전문/보험", "요양기관기호", "34832122", "담당자", "이동준", "보험코드", "676300040", "업무구분",
                    "1.통합" },
            { "/data/대전오성팜.xls", "매입처별 제조사별 매출현황", "56", "코드", "10399", "제조사명", "한국세르비에(주)", "발주처명", "한국세르비에(주)", "일자",
                    "2016-12-01", "영업담당", "최원철", "코드", "20269", "매출처명", "천안-다사랑약국(봉명동)", "표준코드", "8806763001016",
                    "보험코드", "676300100", "코드", "12038", "제품명", "프로코라란정5mg", "규격", "56정(PTP)", "현재보험가", "6,720.00", "수량",
                    "20", "매출단가", "6,720.00", "매출금액", "134,400", "보험단가", "6,720.00", "보험금액", "134,400", "현재고", "0",
                    "실납처명", "천안-다사랑약국(봉명동)", "원재고구분", "기타매출처", "재고적용구분", "기타매출처", "우편번호", "330-930", "주소",
                    "충청남도 천안시 동남구 순천향4길 50", "사업자등록번호", "312-32-97054", "대표자", "조성희", "전화번호", "041-579-5900", "요양기관코드",
                    "34836268" },
            { "/data/대전지오팜.xls", "대전지오팜", "1616", "제품명", "그로리정97.875mg(병)", "규격", "100T", "보험코드", "651601730", "수량",
                    "2", "매출일자", "2016/12/05", "거래처분류", "약국", "우편번호", "331-947", "표준코드", "8806516017325", "주소",
                    "충남 천안시 서북구 쌍용2동" },
            { "/data/전주전주.xls", "work", "108", "입 고 처", "에스케이케미칼", "발주처", "한국세르비에", "매출처", "김제건강종합약국", "원재고거래처",
                    "재고적용거래처", "적용재고거래처", "재고적용거래처", "제품코드", "01760", "제품명", "디아미크롱정80mg(P)", "재고", "17", "규격", "100T",
                    "현보험가", "11,900.00", "수량", "-1", "매출단가", "11,900.00", "매출금액", "-11,900", "사업자등록번호", "405-02-91694",
                    "주소", "전라북도 김제시 남북로 222 (요촌동)", "대표자", "박동현", "거래처종류", "약국", "우편번호", "54384", "전화번호", "547-2073",
                    "제조사", "에스케이케미칼", "판매일자", "2016/10/07", "거래처코드", "50338", "KD코드", "644703170", "담당자명", "우창걸",
                    "요양기관코드", "35805366", "단가적용처", "단가적용거래처", "거래처그룹", "일반", "시점별보험가", "11,900.00", "표준코드",
                    "8806447031711", "매출금액(시점별보험가)", "-11,900" },
            { "/data/Sales_analysis(01_Aug_18).xls", "Principal Product by Customer(W", "775", "Txn Dt", "08/01/18",
                    "Depot", "NP", "Item Name", "ARCALION 200 COATED (30'S) TAB", "Invoice Qty", "5", "Bonus Qty", "0",
                    "Sale Amount", "34130", "Return Qty", "0", "Return Bonus Qty", "0", "Return Value", "", "Net Qty",
                    "5", "Net Bonus Qty", "0", "Gross Value", "34130", "Discount", "0", "Net Amount", "34130",
                    "Item Code", "SRV-A00033", "Cust Code", "NP-M10702", "Cname", "MYA GANDAMAR", "Cust Type",
                    "GP Clinic", "Township", "PYINMANA" },
            { "/data/R9089a_101378_2018-04-18-03-51-23.xlsx", "Sales Summary Data", "592", "SALESPERSON CODE", "PSR",
                    "SALESPERSON NAME", "#", "CUSTOMER CODE", "010001", "CUSTOMER NAME", "國立台灣大學醫學院附設醫院",
                    "SAP CUSTOMER CODE", "30078334", "MATERIAL CODE", "27D02", "MATERIAL DESCRIPTION",
                    "Diamicron MR Tab 30mg 60'S/Bx", "SALES QUANTITY", "960", "BONUS QUANTITY", "0", "VALUE", "74058",
                    "IMS CUSTOMER GROUP 1", "HP1", "CUSTOMER PRICE GROUP 1", "HP" },
            { "/data/sales.mega.daily-2018-12-03.xlsx", "Sheet1", "395", "Depot", "ML", "Txn Dt", "12/03/18",
                    "Cust Code", "ML-W054965", "Cname", "WIN ZAW", "Cust Type", "Stockist", "Township", "DAWEI",
                    "Item Code", "SRV-A00033", "Item Name", "ARCALION 200 COATED (30'S) TAB", "Invoice Qty", "10",
                    "Bonus Qty", "0", "Sale Amount", "76500", "Return Qty", "0", "Return Bonus Qty", "0",
                    "Return Value", "", "Net Qty", "10", "Net Bonus Qty", "0", "Gross Value", "76500", "Net Amount",
                    "74970" } };

    private String[][] scenarios2 = {
            { "/data/강원동양.xls", "2016/06/01", "디아미크롱서방정", "30mg/500T", "1", null, null, "강릉아산병원", null, null,
                    "강원도 강릉시 방동길 38 (사천면)", null },
            { "/data/강원백제.xls", "2016-06-02", "디아미크롱서방정30mg 500T", null, "1", "59000", "59000", "약국", null, "220-010",
                    "강원도 원주시 중앙로 86, 1동 102호 (중앙동)", "한국세르비에" },
            { "/data/강원서호.xls", null, "헤파멜즈산", "5g/100포", "8", "57,000.00", "456,000", "강릉약국(신)", null, "25440",
                    "강원도 강릉시 방동길", null },
            { "/data/강원순천당.xls", "2016/06/07", "디아미크롱정 100T(PTP)", "100T", "1", "11,900.00", "11,900", "강릉시민약국", "약국",
                    "25532", "강원도 강릉시 경강로 2109 (임당동)", null },
            { "/data/광주순천호남.XLS", "2016/06/01", "아서틸정4mg", "90T", "2", "43,920", "87,840", "고흥)고흥병원", null, "59535",
                    "전라남도 고흥군 고흥읍고흥로, 1935 (남계리,숙소) 남계 186번지", null },
            { "/data/대구지오팜.xls", "12/2/16", "그로리정97.875mg(병)", "30T", "1", null, null, null, "약국", "719-801",
                    "경북 성주군 성주읍", null },
            { "/data/대전경동팜.xlsx", "2016/12/05", "디아미크롱서방", "30/500T", "2", "59,000.00", "118,000", "수원123약국", null,
                    "16501", "경기도 수원시 영통구 월드컵로", null },
            { "/data/대전오성팜.xls", "2016-12-01", "프로코라란정5mg", "56정(PTP)", "20", "6,720.00", "134,400", "천안-다사랑약국(봉명동)",
                    null, "330-930", "충청남도 천안시 동남구 순천향4길 50", "한국세르비에(주)" },
            { "/data/대전지오팜.xls", "2016/12/05", "그로리정97.875mg(병)", "100T", "2", null, null, null, "약국", "331-947",
                    "충남 천안시 서북구 쌍용2동", null },
            { "/data/전주전주.xls", "2016/10/07", "디아미크롱정80mg(P)", "100T", "-1", "11,900.00", "-11,900", "김제건강종합약국", "약국",
                    "54384", "전라북도 김제시 남북로 222 (요촌동)", "한국세르비에" },
            { "/data/대구경일.xls", "201611", "프로코라란정5mg", "56T(P)", "-1", null, null, "안동성소", null, null, "경상북도안동시서동문로",
                    "세르비에" },
            { "/data/대전엘에스팜.xls", "20170623", "디아미크롱서방정30mg", "500T", "3", null, "177,000", null, "약국", "31151",
                    "충청남도 천안시 동남구 순천향4길, 50", null },
            // {"/data/Sales_analysis(01_Aug_18).xls", "08/01/18", "ARCALION 200 COATED
            // (30'S) TAB", null, "5", null, "34130", "NP", "GP Clinic", null, "PYINMANA",
            // "MYA GANDAMAR"},
            // {"/data/R9089a_101378_2018-04-18-03-51-23.xlsx", null, "Diamicron MR Tab 30mg
            // 60'S/Bx", null, "960", null, "74058", null, "HP", null, null,
            // "國立台灣大學醫學院附設醫院"},
            // {"/data/sales.mega.daily-2018-12-03.xlsx", "12/03/18", "ARCALION 200 COATED
            // (30'S) TAB", null, "10", null, "74970", "ML", "Stockist", null, "DAWEI", "WIN
            // ZAW"}
    };
}
