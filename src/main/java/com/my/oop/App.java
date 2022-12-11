package com.my.oop;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

public class App {
	static class Job {
		String id;
		String name;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	static class HH {
		List<Job> items;

		public List<Job> getItems() {
			return items;
		}

		public void setItems(List<Job> items) {
			this.items = items;
		}
		
		HH(){};
	}
	public static void main(String[] args) {
		TelegramBot bot = new TelegramBot("5944155581:AAGO61cVCfAdEA6Va_9a6Wjy0ZAj93to2Rw");
		bot.setUpdatesListener(element -> {
			System.out.println(element);
			element.forEach(it -> {
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.hh.ru/vacancies?text=" + it.message().text() + "&area=2019")).build();

				try {
					HttpResponse<String> responce = client.send(request, HttpResponse.BodyHandlers.ofString());
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					String body = responce.body();
					System.out.println(body);
					HH hh = mapper.readValue(body, HH.class);
					hh.items.subList(0, 5).forEach(job -> {
						bot.execute(new SendMessage(it.message().chat().id(), "Вакансия: " + job.name + "\nСсылка: https://hh.ru/vacancy/" + job.id));
						System.out.println(job.id + " " + job.name);
					});
					responce.body();
				} catch (IOException | InterruptedException e) {
					System.out.println(e.getMessage());
				}

			});
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}
}


