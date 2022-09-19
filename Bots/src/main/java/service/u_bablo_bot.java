package service;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import config.API.API_Founds;
import config.DTO.FoundDTO;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import config.API.API_CoinGeko;
import config.SQL.SQLcoingekoapitable;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static Telegram.Bots.BotsApplication.isTest;
import static config.BotsConfig.*;

/**
 *  <a href="https/t.me/u_bablo"><strong>Канал автора</strong></a>
 *  <p>Бот - обзорщик криптовалют, режим <strong>"скана"</strong> работает оч криво, надо бы пофиксить</p>
 *  <p>А так же перенести бота на Jackson с GSON</p>
 *  @author @lllooEWREYoolll
 */
public class u_bablo_bot extends TelegramLongPollingBot {
	
	// Данные бота //
	@Override
	public String getBotUsername() {
		if (isTest){
			return u_bablo_bot_NAME_TEST;
		}
		return u_bablo_bot_NAME;
	}
	@Override
	public String getBotToken() {
		if (isTest){
			return u_bablo_bot_TOKEN_TEST;
		}
		return u_bablo_bot_TOKEN;
	}
	
	// Методы отправки сообщений //
	private void sendMessage // <- Метод для отправки сообщений update 
	(Update update, String textMessage, boolean disableWebPreview) { 
		try {
			 execute(	
					SendMessage.builder()
					.chatId(update.getMessage().getChatId())
					.parseMode("Markdown")
					.disableWebPagePreview(disableWebPreview)
					.text(textMessage)
					.build());
			}
				catch (TelegramApiException e) {
					System.out.println("Проблема с отправкой сообщения : ");
					e.printStackTrace();
			}
	}
	private void sendMessage // <- Метод для отправки сообщений update 
	(long chatId, String textMessage, boolean disableWebPreview) { 
		try {
			 execute(	
					SendMessage.builder()
					.chatId(chatId)
					.parseMode("Markdown")
					.disableWebPagePreview(disableWebPreview)
					.text(textMessage)
					.build());
			}
				catch (TelegramApiException e) {
					System.out.println("Проблема с отправкой сообщения : ");
					e.printStackTrace();
			}
	}
	private void sendInlineKeyBoardMessageWithUrl // <- Отправка сообщения с URL-кнопкой 
	(Update update, String textOnMessange, String textToKeyButton, String url) {
	     InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
	     InlineKeyboardButton url_button = new InlineKeyboardButton();
	     
	     url_button.setText(textToKeyButton);
	     url_button.setUrl(url);
	     
	     List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(); // Создания списка кнопок
	     keyboardButtonsRow.add(url_button);
	     
	     List<List<InlineKeyboardButton>> rowList = new ArrayList<>(); // Создание спикска кнопок??
	     rowList.add(keyboardButtonsRow);
	     
	     keyboard.setKeyboard(rowList);
	     try {
			execute(
	    	SendMessage.builder()
			.chatId(update.getMessage().getChatId())
			.parseMode("Markdown")
			.text(textOnMessange)
			.replyMarkup(keyboard)
			.build());
	     }
			catch (TelegramApiException e) {
				e.printStackTrace();
			}
	}
	private void sendMessageWithMarketsInlineButtoms // <- Отправка сообщений со ссылками на биржи 
	(Update update,ArrayList<String> markets,String coinId) throws IOException {
		String text = "Ошибка";
		 InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
	     List<List<InlineKeyboardButton>> rowList = new ArrayList<>(); // Создание спикска кнопок??
	     if (markets.size()>=5) 
		     for(int i = 0; i<5; i++) {
		    	 	StringBuilder market = new StringBuilder(markets.get(i));
	    			int volumeEnd = market.indexOf(",");
	    	 		int endName = market.indexOf("-");
	    	 		int startPair = market.indexOf("|");
	    	 		
	    	 		
	    	 		String name = market.substring(volumeEnd+1, endName);
	    	 		String url = market.substring(endName+1, startPair);
	    	 		String pair = market.substring(startPair, market.length());
	    	 		int volume = Integer.parseInt(market.substring(0, volumeEnd));
		    	 		if (volume>0) {
					 		InlineKeyboardButton url_button = new InlineKeyboardButton();
					 		List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(); // Создания списка кнопок
					 		
						    url_button.setText(name+" "+pair);
						    url_button.setUrl(url);
						    keyboardButtonsRow.add(url_button);
						    rowList.add(keyboardButtonsRow);
		    	 		}
			    text = "_Где купить?_ *"+update.getMessage().getText().toUpperCase()+"*"
			    		+ "\n\n*Топ-5 бирж по обьёму:"
			    		+ "\n\n| Спонсор @u_bablo |*";
	     }
	     else 
	    	 for(int i = 0; i<markets.size(); i++) {
	    		 	StringBuilder market = new StringBuilder(markets.get(i));
	    			int volumeEnd = market.indexOf(",");
	    	 		int endName = market.indexOf("-");
	    	 		int startPair = market.indexOf("|");
	    	 		
	    	 		
	    	 		String name = market.substring(volumeEnd+1, endName);
	    	 		String url = market.substring(endName+1, startPair);
	    	 		String pair = market.substring(startPair, market.length());
			 		
			 		InlineKeyboardButton url_button = new InlineKeyboardButton();
			 		List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(); // Создания списка кнопок
			 		
				    url_button.setText(name+" "+pair);
				    url_button.setUrl(url);
				    keyboardButtonsRow.add(url_button);
				    rowList.add(keyboardButtonsRow);
				    text = "_Где купить?_ *"+update.getMessage().getText().toUpperCase()+"*"
				    		+ "\n\n*Топ бирж по обьёму:"
				    		+ "\n\n| Спонсор @u_bablo |*";;
			     }
	     keyboard.setKeyboard(rowList);
	     try {
			execute(
	    	SendMessage.builder()
			.chatId(update.getMessage().getChatId())
			.parseMode("Markdown")
			.text(text)
			.replyMarkup(keyboard)
			.build());
	     }
			catch (TelegramApiException e) {
				e.printStackTrace();
			}
	}
	private void sendMessageWithMarketsInlineButtoms // <- Отправка сообщений со ссылками на биржи 
	(long chatId,ArrayList<String> markets, String coinId) throws IOException {
		String text = "Ошибка";
		 InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
	     List<List<InlineKeyboardButton>> rowList = new ArrayList<>(); // Создание спикска кнопок??
	     if (markets.size()>=5) 
		     for(int i = 0; i<5; i++) {
		    	 	StringBuilder market = new StringBuilder(markets.get(i));
	    			int volumeEnd = market.indexOf(",");
	    	 		int endName = market.indexOf("-");
	    	 		int startPair = market.indexOf("|");
	    	 		
	    	 		
	    	 		String name = market.substring(volumeEnd+1, endName);
	    	 		String url = market.substring(endName+1, startPair);
	    	 		String pair = market.substring(startPair, market.length());
	    	 		int volume = Integer.parseInt(market.substring(0, volumeEnd));
		    	 		if (volume>0) {
					 		InlineKeyboardButton url_button = new InlineKeyboardButton();
					 		List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(); // Создания списка кнопок
					 		
						    url_button.setText(name+" "+pair);
						    url_button.setUrl(url);
						    keyboardButtonsRow.add(url_button);
						    rowList.add(keyboardButtonsRow);
		    	 		}
			    text = "_Где купить?_"
			    		+ "\n\n*Топ-5 бирж по обьёму:"
			    		+ "\n\n| Спонсор @u_bablo |*";;
	     }
	     else 
	    	 for(int i = 0; i<markets.size(); i++) {
	    		 	StringBuilder market = new StringBuilder(markets.get(i));
	    			int volumeEnd = market.indexOf(",");
	    	 		int endName = market.indexOf("-");
	    	 		int startPair = market.indexOf("|");
	    	 		
	    	 		
	    	 		String name = market.substring(volumeEnd+1, endName);
	    	 		String url = market.substring(endName+1, startPair);
	    	 		String pair = market.substring(startPair, market.length());
			 		
			 		InlineKeyboardButton url_button = new InlineKeyboardButton();
			 		List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(); // Создания списка кнопок
			 		
				    url_button.setText(name+" "+pair);
				    url_button.setUrl(url);
				    keyboardButtonsRow.add(url_button);
				    rowList.add(keyboardButtonsRow);
				    text = "_Где купить?_"
				    		+ "\n\n*Топ бирж по обьёму:"
				    		+ "\n\n| Спонсор @u_bablo |*";
			     }
	     keyboard.setKeyboard(rowList);
	     try {
			execute(
	    	SendMessage.builder()
			.chatId(chatId)
			.parseMode("Markdown")
			.text(text)
			.replyMarkup(keyboard)
			.build());
	     }
			catch (TelegramApiException e) {
				e.printStackTrace();
			}
	}
	private void sendInlineKeyBoardMessageWithOutUrlForIdsList // <- Отправка сообщения с кнопкой и считка нажатия 
	(Update update, ArrayList<String> idsList, String textOnMessage) {
	 	     InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
	 	     List<List<InlineKeyboardButton>> rowList = new ArrayList<>(); // <- Создание клавиатуры из массива кнопок
	 	     for(int i = 0; i<idsList.size(); i++) {
	 	     InlineKeyboardButton button = new InlineKeyboardButton();
	 	     List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(); // <- Создание массива с кнопками
	 	     button.setText(idsList.get(i));
	 	     button.setCallbackData(idsList.get(i));
	 	     keyboardButtonsRow.add(button);
	 	     rowList.add(keyboardButtonsRow);
	 	     
	 	     }
	 	     keyboard.setKeyboard(rowList);
	 	     try {
	 			execute( 
	 	    	SendMessage.builder()
	 			.chatId(update.getMessage().getChatId())
	 			.parseMode("Markdown")
	 			.text(textOnMessage)
	 			.replyMarkup(keyboard)
	 			.build());
	             
	 	     }
	 			catch (TelegramApiException e) {
	 				e.printStackTrace();
	 	}
	 
	}
	
	// Работа с обновлениями //
	@Override
	public void onUpdateReceived(Update update) {
				
		// Методы отправки сообщений //
		if (update.hasMessage()&& update.getMessage().hasText()) {
			String command = update.getMessage().getText(); 
			ChatMember member = null;
			
			// Получение данных о пользователе, подписан ли он //
			try { member = execute(GetChatMember.builder()
						   .chatId("-1001751660372")
						   .userId(update.getMessage().getFrom().getId())
						   .build());
			} catch (TelegramApiException e1) {
				System.out.println("Проблема с получением данных по ChatMember : ");
				e1.printStackTrace();
			}
			boolean memberTrue = member.getStatus().toString().equals("member");
			boolean adminTrue = member.getStatus().toString().equals("administrator");
			boolean creatorTrue = member.getStatus().toString().equals("creator");
			
			// Если пользователь подписан на канал //
			if (memberTrue || adminTrue || creatorTrue) {
					 if (command.equals("/start")) startAnswer(update);
				else if (command.equals("/dream")) dreamAnswer(update);
				else if (command.equals("/found")) foundAnswer(update);
				else if (command.startsWith("$")) {
                    try {
                        coinAnswer(update);
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
				else if (command.startsWith("/info")) infoAnswer(update);
			} else if (!memberTrue) userNotMember(update);
			
			}
		
		// Если сообщение содержит CallbackQuery (нажатие на кнопку)
		else if (update.hasCallbackQuery()) {
			try {
				answerToListIds(update, update.getCallbackQuery().getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Методы ответов //
	private void startAnswer(Update update) /* <- Ответ на /start */ {
		String messageText = 
				"Салут дружище, я бот - помощник замечательного канала - *@u_bablo*\n\n"
				+ "И я крайне рад что ты являешься частью комьюнити и подписан на канал! ❤️\n\n"
				+ "_Давай я расскажу тебе что я умею?_\n\n"
				+ "Могу показывать тир Фонда, которого ты в меня вводишь, подробнее -> */found*\n"
				+ "У меня есть база из более чем *300+* фондов, так что, даже, на первый взгляд 👀 _<ноунейм>_ может оказаться чем-то интересным\n\n"
				+ "Команда */dream* познакомит тебя с миром фантазии и окунет туда с головой, там будут описаны все мои идеи для реализации в боте _(спойлер"
				+ "- их там много)_\n"
				+ "\nСмело вводи символ монеты для получения информации о ней например *$BTC*\n"
				+ "\n_(Ты увидишь текущую цену, настрой на рынке, изменение цены за определенный промежуток времени, описание монеты, и, самое интересное"
				+ ", список бирж где ее можно приобрести!)_\n\n"
				+ "Сейчас бот находится в версии _1.5.0_, хочешь накинуть идей для реализации?"
				+ "\nПиши мне *@oolllEwreyllloo*";
		sendMessage(update,messageText,true);
	}
	private void dreamAnswer(Update update) /* <- Ответ на /dream */  {
		String messageText = 
				"*О-хо-хо* на этого бота у меня грандиозные планы и на 60% они уже осуществимы \n"
				+ "\n"
				+ "- Показ книг для изучения торговли на рынке или криптовалют\n"
				+ "\n"
				+ "- Автоматическое рецензирование книг, сбор отзывов из вне и из подписчиков комьюнити\n"
				+ "\n"
				+ "- Суммирование тиров \n"
				+ "(_Эта функция позволяет вводить фонды проекта через запятую и может показывать престиж самого проекта, очень"
				+ " удобно, когда не понимаешь насколько проект ужасен или прекрасен_) 😱\n"
				+ "\n"
				+ "- Добавление в бота функционала чат-бота с банами, мутами и т.д\n"
				+ "\n"
				+ "- Поиск фондов у проекта по имени \n"
				+ "(_На самом деле_ *true-hard* _задачка, так как API оч дорогое и малопоточное, мне еще долго и долго думать над"
				+ " тем как грамотнее реализовать это_)\n"
				+ "\n"
				+ "- Поиск *\"знаменитых\"* подписчиков у Twitter'а проекта\n"
				+ "(_Часто получается, что проект на очень-очень ранней стадии имеет очень и очень значимых личностей в"
				+ " подписчиках, это конечно не *Кнопка БАБЛО*, но лишним точно не будет_ 😁)"
				+ "\n\nХочешь накинуть идей для реализации?\n"
				+ "\nПиши мне *@oolllEwreyllloo*";
		sendMessage(update, messageText, false);
	}
	private void foundAnswer(Update update) /* <- Ответ на /found */ {
		String textMessage = 
				"*Хочешь узнать Информацию о криптовалютном фонде?*\n"
				+ "_Ты по адресу, поздравляю!_\n\n"
				+ "Просто введи */info* и имя фонда о котором хочешь узнать информацию, и бот все сделает за тебя 😉\n\n"
				+ "*Пример :* `/info Paradigm`\n\n"
				+ "_Зачем мне тир?_\n"
				+ "Хороший вопрос, если ты дошел до момента поиска тира у криптовалютного фонда - поздравляю, ты не позер, а настоящий сыщик 🕵️\n\n"
				+ "*Тир* - показатель \"*престижа*\" определённого фонда, чем он ниже, тем фонд престижней, а значит, более"
				+ " избирательно подходит к выбору актива к себе в кошелек 👛"; 
		sendMessage(update, textMessage, false);
	}
	private void coinAnswer(Update update) throws IOException, SQLException /* <- Ответ на сообщение начинающееся с $ ($BTC и тому подобное) */{
		ArrayList<String> markets = new ArrayList<String>();
		String coins = update.getMessage().getText();
		StringBuilder coin = new StringBuilder(coins);
		coin.deleteCharAt(0);

		ArrayList<String> id = new SQLcoingekoapitable().idOfCrypto(coin.toString());
		if (id.size() == 1) {
		sendMessage(update, new API_CoinGeko().API_CoinGeko_coin(id.get(0)), false); // <- Информация по монете
		sendMessage(update, new API_CoinGeko().API_CoinGeko_coin_description(id.get(0)), false); // <- Описание монеты
		
		markets = new API_CoinGeko().API_CoinGeko_coin_markets(id.get(0)); // <- Лист бирж
		if (markets.size()>0)
			sendMessageWithMarketsInlineButtoms(update,markets,id.get(0));
			else sendMessage(update, "_По монете_ *"+update.getMessage().getText().toUpperCase()+"* _биржи не найдены_", false);
		}
		
		else if (id.size()>1) {
			String textMessage = "Я нашел несколько монет с одним символом\n_Выберите подходящую:_";
			sendInlineKeyBoardMessageWithOutUrlForIdsList(update,id,textMessage);
		}
		
		else {
			sendMessage(update,"_Токена с символом "+update.getMessage().getText().toUpperCase()+" не существует_",false);
		}
		
	}

	public void infoAnswer(Update update){
		String nameOfFound = update.getMessage().getText().replaceAll("/info ","");
		try {
			FoundDTO found =  new API_Founds().foundByName(nameOfFound);
			sendMessage(update, found.toString(), false);
		} catch (Exception e) {
			sendMessage(update, "Фонда с именем -> "+nameOfFound+" не существует(", false);
		}
	}
	private void answerToListIds(Update update, String id) throws IOException /* <- Ответ если найдено несколько монет по одному символу */{
			ArrayList<String> markets = new ArrayList<String>();
			sendMessage(update.getCallbackQuery().getMessage().getChatId(), new API_CoinGeko().API_CoinGeko_coin(id), false); // <- Информация по монете
			sendMessage(update.getCallbackQuery().getMessage().getChatId(), new API_CoinGeko().API_CoinGeko_coin_description(id), false); // <- Описание монеты
			markets = new API_CoinGeko().API_CoinGeko_coin_markets(id); // <- Лист бирж
			if (markets.size()>0)
				sendMessageWithMarketsInlineButtoms(update.getCallbackQuery().getMessage().getChatId(),markets,id);
				else sendMessage(update.getCallbackQuery().getMessage().getChatId(), "*Биржи не найдены* 😔", false);
		}
	
	// Пользователь не подписан //
	private void userNotMember(Update update) {
		sendInlineKeyBoardMessageWithUrl(update, 
				"*Салут*, хочешь испытать все прелести одного из крутейших _(в"
				+ " перспективе)_ криптоботов? Тогда подпишись на канал нажав кнопку ниже \n"
				+ "\n"
				+ "*Тебе не сложно, а мне приятно* ❤️"
				+ "\nПроверить - */start*", "Подписаться", "https://t.me/+r2qAD6vpQBgwM2Vi");
	}
}
