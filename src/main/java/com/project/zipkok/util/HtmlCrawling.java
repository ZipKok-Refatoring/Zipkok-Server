package com.project.zipkok.util;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.dto.GetAddressResponse;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.service.AddressService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class HtmlCrawling {

    // WebDriver 객체 생성
    ChromeDriver driver = new ChromeDriver();
    AddressService addressService = new AddressService();

    public void parsingHTML(String url) throws IOException, InterruptedException {
        // 현재 package의 workspace 경로, Windows는 [ chromedriver.exe ]
        Path path = Paths.get("C:\\Users\\권민혁\\Downloads\\chromedriver-win64(121.0.6167.85)\\chromedriver-win64\\chromedriver.exe");  // 현재 package의

        // WebDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", path.toString());

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--start-maximized");            // 전체화면으로 실행
        options.addArguments("--disable-popup-blocking");    // 팝업 무시
        options.addArguments("--disable-default-apps");     // 기본앱 사용안함

        // 브라우저 드라이버 실행
        driver.get(url);
        Thread.sleep(1000);

        // 매물 요소들을 선택하여 리스트로 가져오기
        List<WebElement> elements = driver.findElements(By.cssSelector("li.styled__CardItem-sc-5dgg47-3.cVNfk"));


        // 각 매물 요소를 클릭하고 정보를 추출
        for (WebElement element : elements) {
            // 매물 id 값 뽑아서 정보 추출
            element.click();
            Thread.sleep(1000);
            
            //tab 정보 가져오기
            String currentTab = driver.getWindowHandle();
            Set<String> allTabs = driver.getWindowHandles();
            for (String tab : allTabs) {
                if (!tab.equals(currentTab)) {
                    driver.switchTo().window(tab);
                    Thread.sleep(1000);
                    // 정보 추출 코드 작성
                    //extractRealEstateInfo();
                    
                    driver.close();
                    driver.switchTo().window(currentTab);
                }
            }
            
            Thread.sleep(1000);
        }
    }

    private void extractRealEstateInfo() throws InterruptedException {

        // address parsing
        String address = driver.findElement(By.cssSelector("p.styled__Address-ze8x26-1.lKcXv")).getText();

        // 위도, 경도 parsing
        GetAddressResponse getAddressResponse = this.addressService.getAddresses(address, 1,1);
        Double latitude = Double.parseDouble(getAddressResponse.getDocuments().get(0).getLatitude());
        Double longitude = Double.parseDouble(getAddressResponse.getDocuments().get(0).getLongitude());

        // transactionType parsing
        String transactionType = driver.findElement(By.cssSelector("div.styled__ListHeader-mttebe-0.gfmTEV")).getText();
        if(transactionType.equals("월세")) transactionType = "MONTHLY";
        else if (transactionType.equals("전세")) transactionType = "YEARLY";
        else if(transactionType.equals("매매")) transactionType = "PURCHASE";

        // deposit, price parsing
        String config = driver.findElement(By.cssSelector("div.styled__ListContent-mttebe-1.bQSaMO")).getText();
        Long deposit = null;
        Long price = null;
        if(config.contains("/")){
            deposit = Long.parseLong(config.substring(0,config.indexOf("/")));
            price = Long.parseLong(config.substring(config.indexOf("/")+1));
        }
        else if(transactionType.equals("전세")){
            if(config.contains("억")){
                deposit = Long.parseLong(config.substring(0, config.indexOf("억")) + "0000");
            }else deposit = Long.parseLong(config);
            price = 0L;
        }
        else if(transactionType.equals("매매")){
            if(config.contains("억")){
                price = Long.parseLong(config.substring(0, config.indexOf("억")) + "0000");
            }else price = Long.parseLong(config);
            deposit = 0L;
        }

        // 관리비 parsing
        String fee_config = driver.findElement(By.cssSelector("div.styled__MaintenanceCostWrap-mttebe-2.SFyWA")).getText();
        Integer administrative_fee = Integer.parseInt(fee_config.substring(fee_config.indexOf(" ")+1, fee_config.indexOf("만")));

        // realEstateType parsing
        String realEstateType = driver.findElement(By.cssSelector("div.styled__ListContent-ialnoa-7.iMduqg")).getText();
        if(realEstateType.equals("원룸")) realEstateType = "ONEROOM";
        else if(realEstateType.equals("투룸"))

        RealEstate.builder()
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .transactionType(TransactionType.valueOf(transactionType))
                .deposit(deposit)
                .price(price)
                .administrativeFee(administrative_fee)
                .realEstateType(RealEstateType.valueOf(realEstateType))
                .build();

//        String imageURL;
//        String address;
//        String detail_address;
//        Double latitude;
//        Double longitude;
//        String Transaction_type;
//        String deposit;
//        String price;
//        String administrative_fee;
//        String detail;
//        Integer area_size;
//        Integer pyeongsu;
//        String realEstate_type;
//        Integer floor_num;
//        String agent;



        driver.navigate().back();
    }
}
