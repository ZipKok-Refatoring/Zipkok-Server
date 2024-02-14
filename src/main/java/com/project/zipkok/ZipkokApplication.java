package com.project.zipkok;

import com.project.zipkok.util.HtmlCrawling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ZipkokApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		HtmlCrawling htmlCrawling = new HtmlCrawling();
		htmlCrawling.parsingHTML("https://dabangapp.com/search/map?filters=%7B%22multi_room_type%22%3A%5B3%5D%2C%22selling_type%22%3A%5B2%5D%2C%22deposit_range%22%3A%5B0%2C999999%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C2%2C3%2C4%2C5%2C6%2C7%2C-1%2C0%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22is_contract%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%2C%22loan%22%3Afalse%7D&position=%7B%22location%22%3A%5B%5B127.0534593%2C37.5305658%5D%2C%5B127.0877916%2C37.5492476%5D%5D%2C%22center%22%3A%5B127.070625420237%2C37.5399072974101%5D%2C%22zoom%22%3A15%7D&search=%7B%22id%22%3A%22%22%2C%22type%22%3A%22%22%2C%22name%22%3A%22%22%7D&tab=all");
		//SpringApplication.run(ZipkokApplication.class, args);
	}

}
