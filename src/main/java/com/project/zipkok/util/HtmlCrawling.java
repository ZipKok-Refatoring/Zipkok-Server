package com.project.zipkok.util;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.dto.GetAddressResponse;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.repository.RealEstateRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class HtmlCrawling {

    @Value("${crawling.workspace_path}")
    private String workspacePath;

    ChromeDriver driver = new ChromeDriver();
    private final AddressService addressService;
    private final RealEstateRepository realEstateRepository;

    public void parsingHTML(String url) throws IOException, InterruptedException {
        // 현재 package의 workspace 경로
        Path path = Paths.get(workspacePath);

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

        for(int i =0;i<10;i++) {
            System.out.println(i);
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
                        Thread.sleep(3000);
                        // 정보 추출 코드 작성
                        extractRealEstateInfo();

                        driver.close();
                        driver.switchTo().window(currentTab);
                    }
                }

                Thread.sleep(1000);
            }
            //해당 페이지의 모든 매물 추출이 끝나면 그 다음 페이지 클릭
            driver.findElements(By.cssSelector("button.styled__PageBtn-d24fjp-2.caslEP")).get(Math.min(i,2)).click();
            Thread.sleep(1000);
        }
    }

    private void extractRealEstateInfo() throws InterruptedException {

        try {
            // address parsing
            String address = driver.findElement(By.cssSelector("p.styled__Address-ze8x26-1.lKcXv")).getText();

//             위도, 경도 parsing
            GetAddressResponse getAddressResponse = this.addressService.getAddresses(address, 1, 1);
            Double latitude = Double.parseDouble(getAddressResponse.getDocuments().get(0).getLatitude());
            Double longitude = Double.parseDouble(getAddressResponse.getDocuments().get(0).getLongitude());

            // transactionType parsing
            String transactionType = driver.findElement(By.cssSelector("div.styled__ListHeader-mttebe-0.gfmTEV")).getText();
            if (transactionType.equals("월세")) transactionType = "MONTHLY";
            else if (transactionType.equals("전세")) transactionType = "YEARLY";
            else if (transactionType.equals("매매")) transactionType = "PURCHASE";

            // deposit, price parsing
            String config = driver.findElement(By.cssSelector("div.styled__ListContent-mttebe-1.bQSaMO")).getText();
            Long deposit = null;
            Long price = null;
            if (config.contains("/")) {
                deposit = Long.parseLong(config.substring(0, config.indexOf("/")));
                price = Long.parseLong(config.substring(config.indexOf("/") + 1));
            } else if (transactionType.equals("YEARLY")) {
                if (config.contains("억")) {
                    deposit = Long.parseLong(config.substring(0, config.indexOf("억")) + "0000");
                } else deposit = Long.parseLong(config);
                price = 0L;
            } else if (transactionType.equals("PURCHASE")) {
                if (config.contains("억")) {
                    price = Long.parseLong(config.substring(0, config.indexOf("억")) + "0000");
                } else price = Long.parseLong(config);
                deposit = 0L;
            }

            // 관리비 parsing
            String fee_config = driver.findElement(By.cssSelector("div.styled__MaintenanceCostWrap-mttebe-2.SFyWA")).getText();
            Integer administrative_fee;
            if(fee_config.contains("없음")) administrative_fee =0;
            else administrative_fee = Integer.parseInt(fee_config.substring(fee_config.indexOf(" ") + 1, fee_config.indexOf("만")));

            int index =0;

            // realEstateType parsing
            String realEstateType = driver.findElement(By.cssSelector("div.styled__ListContent-ialnoa-7.iMduqg")).getText();
            if (realEstateType.contains("원룸")) realEstateType = "ONEROOM";
            else if (realEstateType.contains("투룸")) realEstateType = "TWOROOM";
            else if (realEstateType.contains("오피스텔")) realEstateType = "OFFICETELL";
            else{
                index++;
                realEstateType = "APARTMENT";
            }

            // 면적 parsing
            String area_config = driver.findElements(By.cssSelector("div.styled__ListContent-ialnoa-7.iMduqg")).get(index+2).getText();
            Float area_size = Float.parseFloat(area_config.substring(0, area_config.indexOf("㎡")));
            Integer pyeongsu = (int)(area_size * 0.3025);

            // floor_num parsing
            String floor_num_config = driver.findElements(By.cssSelector("div.styled__ListContent-ialnoa-7.iMduqg")).get(index+1).getText();
            Integer floor_num;
            String detail = null;
            String subString = floor_num_config.substring(0,floor_num_config.indexOf("층"));

            if(subString.equals("반지")){
                floor_num = -1;
                detail = "반지하";
            }
            else if(subString.equals("고")){
                floor_num = 0;
                detail = "고층";
            }
            else if(subString.equals("중")){
                floor_num = 0;
                detail = "중층";
            }
            else if(subString.equals("저")){
                floor_num = 0;
                detail = "저층";
            }
            else {
                floor_num = Integer.parseInt(subString);
            }

            //agent parsing
            String agent = driver.findElement(By.cssSelector("div.styled__Name-sc-1h4thfr-16.iwgGHR")).getText();

            RealEstate realEstate = RealEstate.builder()
                    .address(address)
                    .latitude(latitude)
                    .longitude(longitude)
                    .transactionType(TransactionType.valueOf(transactionType))
                    .deposit(deposit)
                    .price(price)
                    .administrativeFee(administrative_fee)
                    .areaSize(area_size)
                    .pyeongsu(pyeongsu)
                    .realEstateType(RealEstateType.valueOf(realEstateType))
                    .floorNum(floor_num)
                    .agent(agent)
                    .detail(detail)
                    .status("active")
                    .build();

            System.out.println("address = " + realEstate.getAddress());
            System.out.println("latitude = " + realEstate.getLatitude());
            System.out.println("longitude = " + realEstate.getLongitude());
            System.out.println("transactionType = " + realEstate.getTransactionType().getDescription());
            System.out.println("deposit = " + realEstate.getDeposit());
            System.out.println("price = " + realEstate.getPrice());
            System.out.println("administrativeFee = " + realEstate.getAdministrativeFee());
            System.out.println("areaSize = " + realEstate.getAreaSize());
            System.out.println("pyeongsu = " + realEstate.getPyeongsu());
            System.out.println("realEstateType = " + realEstate.getRealEstateType().getDescription());
            System.out.println("floorNum = " + realEstate.getFloorNum());
            System.out.println("detail = " + realEstate.getDetail());
            System.out.println("agent = " + realEstate.getAgent());

            this.realEstateRepository.save(realEstate);

            System.out.println("=================저장완료===================\n\n");


        }catch (Exception e ){
            System.out.println(e.getMessage());
        }

        driver.navigate().back();
    }
}
