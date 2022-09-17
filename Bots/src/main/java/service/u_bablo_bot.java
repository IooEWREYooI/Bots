package service;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import config.API_CoinGeko;
import config.SQLcoingekoapitable;
import config.SQLfoundtier;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static config.BotsConfig.u_bablo_bot_NAME;
import static config.BotsConfig.u_bablo_bot_TOKEN;

public class u_bablo_bot extends TelegramLongPollingBot {
	public boolean tierScan = false; // <- Скан для /tier отключен
	public boolean tierTurn = false; // <- Сканирование запущено, когда оно запущено, сообщение о том что человек - не подписчик, не отправляется
	
	// Данные бота //
	@Override
	public String getBotUsername() // <- username бота без @ 
	{
		return u_bablo_bot_NAME;
	}
	@Override
	public String getBotToken() // <- Токен бота 
	{
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
	(long chatId,ArrayList<String> markets,String coinId) throws IOException {
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
	public void onUpdateReceived 
	(Update update) { 
				
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
			if (tierScan == false && memberTrue || adminTrue || creatorTrue) {
					 if (command.equals("/start")) startAnswer(update);
				else if (command.equals("/dream")) dreamAnswer(update);
				else if (command.equals("/tier")) tierAnswer(update); 
				else if (command.startsWith("$")) { try {coinAnswer(update);} catch (IOException | SQLException e) {e.printStackTrace();} }
			}
			// Получение сообщения и преобразование в String (скан ответа, для подписанных) //
			else if (tierScan == true && memberTrue || adminTrue || creatorTrue) {
				try { tierScaner(update); } catch (SQLException e) { e.printStackTrace(); }
			}
			
			// Если не подписан на канал //
			else if (tierTurn = false || memberTrue == false) userNotMember(update);
			
			}
		
		// Если сообщение содержит CallbackQuery (нажатие на кнопку)
		else if (update.hasCallbackQuery()) {
			try { answerToListIds(update, update.getCallbackQuery().getData()); } catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	// Методы ответов //
	private void startAnswer // <- Ответ на /start 
	(Update update) {
		String messageText = 
				"Салут дружище, я бот - помощник замечательного канала - *@u_bablo*\r\n"
				+ "\r\n"
				+ "И я крайне рад что ты являешься частью комьюнити и подписан на канал! ❤️\r\n"
				+ "\r\n"
				+ "_Давай я расскажу тебе что я умею?_\r\n"
				+ "\r\n"
				+ "Могу показывать тир Фонда, "
				+ "которого ты в меня вводишь по команде */tier*\r\n"
				+ "У меня есть база из более чем *240+* фондов, так что, даже, на первый взгляд 👀 _<ноунейм>_ может оказаться чем-то интересным\r\n"
				+ "\r\n"
				+ "Команда */dream* познакомит тебя с миром фантазии и окунет туда с головой, там будут описаны все мои идеи для реализации в боте _(спойлер"
				+ "- их там много)_\r\n"
				+ "\nСмело вводи символ монеты для получения информации о ней например *$BTC*\n"
				+ "\n_(Ты увидишь текущую цену, настрой на рынке, изменение цены за определенный промежуток времени, описание монеты, и, самое интересное"
				+ ", список бирж где ее можно приобрести!)_\n"
				+ "\r\n"
				+ "Сейчас бот находится в версии _1.2.0_, хочешь накинуть идей для реализации?"
				+ "\nПиши мне *@oolllEwreyllloo*";
		sendMessage(update,messageText,true);
	}
	private void dreamAnswer // <- Ответ на /dream 
	(Update update) {
		String messageText = 
				"*О-хо-хо* на этого бота у меня грандиозные планы и на 60% они уже осуществимы \r\n"
				+ "\r\n"
				+ "- Показ книг для изучения торговли на рынке или криптовалют\r\n"
				+ "\r\n"
				+ "- Автоматическое рецензирование книг, сбор отзывов из вне и из подписчиков комьюнити\r\n"
				+ "\r\n"
				+ "- Суммирование тиров \r\n"
				+ "(_Эта функция позволяет вводить фонды проекта через запятую и может показывать престиж самого проекта, очень"
				+ " удобно, когда не понимаешь насколько проект ужасен или прекрасен_) 😱\r\n"
				+ "\r\n"
				+ "- Добавление в бота функционала чат-бота с банами, мутами и т.д\r\n"
				+ "\r\n"
				+ "- Поиск фондов у проекта по имени \r\n"
				+ "(_На самом деле_ *true-hard* _задачка, так как API оч дорогое и малопоточное, мне еще долго и долго думать над"
				+ " тем как грамотнее реализовать это_)\r\n"
				+ "\r\n"
				+ "- Поиск *\"знаменитых\"* подписчиков у Twitter'а проекта\r\n"
				+ "(_Часто получается, что проект на очень-очень ранней стадии имеет очень и очень значимых личностей в"
				+ " подписчиках, это конечно не *Кнопка БАБЛО*, но лишним точно не будет_ 😁)"
				+ "\n\nХочешь накинуть идей для реализации?\r\n"
				+ "\nПиши мне *@oolllEwreyllloo*";
		sendMessage(update, messageText, false);
	}
	private void tierAnswer // <- Ответ на /tier 
	(Update update) {
		String textMessage = 
				"*Хочешь узнать тир фонда?*\r\n"
				+ "_Ты по адресу, поздравляю!_\r\n"
				+ "\r\n"
				+ "Просто введи имя фонда о котором хочешь узнать информацию, и бот все сделает за тебя 😉\r\n"
				+ "\r\n"
				+ "*Пример :* `Paradigm`\r\n"
				+ "\r\n"
				+ "_Зачем мне тир?_\r\n"
				+ "Хороший вопрос, если ты дошел до момента поиска тира у криптовалютного фонда - поздравляю, ты не позер, а"
				+ " настоящий сыщик 🕵️\r\n"
				+ "\r\n"
				+ "*Тир* - показатель \"*престижа*\" определённого фонда, чем он ниже, тем фонд престижней, а значит, более"
				+ " избирательно подходит к выбору актива к себе в кошелек 👛"; 
		sendMessage(update, textMessage, false);
		tierScan = true;
		tierTurn = true;
	}
	private void coinAnswer // <- Ответ на сообщение начинающееся с $ ($BTC и тому подобное) 
	(Update update) throws IOException, SQLException {
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
			String textMessage = "Я нашел несколько монет с одним символом\n_Выбирите подходящую:_";
			sendInlineKeyBoardMessageWithOutUrlForIdsList(update,id,textMessage);
		}
		
		else {
			sendMessage(update,"_Токена с символом "+update.getMessage().getText().toUpperCase()+" несуществует_",false);
		}
		
	}
	private void answerToListIds // <- Ответ если найдено несколько монет по одному символу 
	(Update update, String id) throws ConnectException, IOException {
			ArrayList<String> markets = new ArrayList<String>();
			sendMessage(update.getCallbackQuery().getMessage().getChatId(), new API_CoinGeko().API_CoinGeko_coin(id), false); // <- Информация по монете
			sendMessage(update.getCallbackQuery().getMessage().getChatId(), new API_CoinGeko().API_CoinGeko_coin_description(id), false); // <- Описание монеты
			markets = new API_CoinGeko().API_CoinGeko_coin_markets(id); // <- Лист бирж
			if (markets.size()>0)
				sendMessageWithMarketsInlineButtoms(update.getCallbackQuery().getMessage().getChatId(),markets,id);
				else sendMessage(update.getCallbackQuery().getMessage().getChatId(), "*Биржи не найдены* 😔", false);
		}


	// Методы "скана" //
	private void tierScaner 
	(Update update) throws SQLException {
		if(tierScan == true && update.getMessage().getText().toLowerCase().equals("exit") == false) {
			String foundTier = new SQLfoundtier().getTierOfFound(update.getMessage().getText().toLowerCase());
			
			if (foundTier.equals("0") == false && foundTier.equals("1")) {
			sendMessage (update,"Фонд *"+update.getMessage().getText()+"* является *"+foundTier+"* тиром"
					+ "\n(Престиж *наивысший* ⭐️⭐️⭐️)"
					+ "\n\n_Для выхода из режима поиска введи_ \n*/stop*"
					+ "\n\n*| Спонсор @u_bablo |*"
					,false);
			}
			
			else if (foundTier.equals("0") == false && foundTier.equals("2")) {
				sendMessage (update,"Фонд *"+update.getMessage().getText()+"* является *"+foundTier+"* тиром"
						+ "\n(Престиж *средний* ⭐️⭐️)"
						+ "\n\n_Для выхода из режима поиска введи_ \n*/stop*"
						+ "\n\n*| Спонсор @u_bablo |*"
						,false);
				}
			
			else if (foundTier.equals("0") == false && foundTier.equals("3")) {
				sendMessage (update,"Фонд *"+update.getMessage().getText()+"* является *"+foundTier+"* тиром"
						+ "\n(Престиж *низкий* ⭐️)"
						+ "\n\n_Для выхода из режима поиска введи_ \n*/stop*"
						+ "\n\n*| Спонсор @u_bablo |*"
						,false);
				}
			
			else if (foundTier.equals("0") == false && foundTier.equals("4")) {
				sendMessage (update,"Фонд *"+update.getMessage().getText()+"* является *"+foundTier+"* тиром"
						+ "\n(Престиж *очень низкий* 🤢)"
						+ "\n\n_Для выхода из режима поиска введи_ \n*/stop*"
						+ "\n\n*| Спонсор @u_bablo |*"
						,false);
				}
			
			else if (update.getMessage().getText().toLowerCase().equals("/stop") == false){
				sendMessage (update,
						"*"+update.getMessage().getText()+"* - такого фонда не существует, повторите попытку\n\nИли введите */stop* чтобы выйти"
						,false);
			}
			
			else if (foundTier.equals("0") && update.getMessage().getText().toLowerCase().equals("/stop")) {
				sendMessage (update,"*Вы успешно вышли из режима поиска тира, введите любую команду*",false);
				tierScan = false;
				tierTurn = false;
			}
		}
	}
	
	// Пользователь не подписан //
	private void userNotMember 
	(Update update) {
		tierScan = false;
		sendInlineKeyBoardMessageWithUrl(update, 
				"*Салут*, хочешь испытать все прелести одного из крутейших _(в"
				+ " перспективе)_ криптоботов? Тогда подпишись на канал нажав кнопку ниже \r\n"
				+ "\r\n"
				+ "*Тебе не сложно, а мне приятно* ❤️"
				+ "\nПроверить - */start*", "Подписаться", "https://t.me/+r2qAD6vpQBgwM2Vi");
	}
}
