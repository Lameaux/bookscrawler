CREATE DATABASE books CHARACTER SET utf8 COLLATE utf8_general_ci;
use books;
-- DROP TABLE author;
-- DROP TABLE genre;
-- DROP TABLE book;


CREATE TABLE IF NOT EXISTS author (
	id VARCHAR(255) PRIMARY KEY, 
	first_name VARCHAR(100),
	last_name VARCHAR(100)	
) ENGINE=InnoDB;
CREATE INDEX author_last_name on author(last_name);

CREATE TABLE IF NOT EXISTS genre (
	id VARCHAR(100) primary key,
	title VARCHAR(255),
	active INT DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS book (
	id INT PRIMARY KEY,
	author_id VARCHAR(255),
	title VARCHAR(255),
	url VARCHAR(255),
	annotation TEXT,
	genre VARCHAR(100),
	publisher VARCHAR(255),
	year VARCHAR(255),
	isbn VARCHAR(255),
	lang VARCHAR(20) DEFAULT 'ru',
	views INT DEFAULT 0,
	likes INT DEFAULT 0,
	has_image INT DEFAULT 0
) ENGINE=InnoDB;
CREATE INDEX book_author_id on book(author_id);
CREATE INDEX book_genre on book(genre);

INSERT INTO `genre` (`id`, `title`, `active`) VALUES
('accounting', 'Бухучет и аудит', 0),
('adv-animal', 'Природа и животные', 0),
('adv-geo', 'Путешествия и география', 0),
('adv-history', 'Исторические приключения', 0),
('adv-indian', 'Приключения про индейцев', 0),
('adv-maritime', 'Морские приключения', 0),
('adv-western', 'Вестерн', 0),
('adventure', 'Приключения: прочее', 1),
('antique', 'Старинная литература: прочее', 0),
('antique-ant', 'Античная литература', 0),
('antique-east', 'Древневосточная литература', 0),
('antique-european', 'Древнеевропейская литература', 0),
('antique-myths', 'Мифы. Легенды. Эпос', 0),
('antique-russian', 'Древнерусская литература', 0),
('aphorisms', 'Афоризмы', 0),
('architecture-book', 'Архитектура', 0),
('astrology', 'Астрология', 0),
('auto-regulations', 'Автомобили и ПДД', 0),
('banking', 'Банковское дело', 0),
('child-adv', 'Детские приключения', 0),
('child-det', 'Детские остросюжетные', 0),
('child-education', 'Образовательная литература', 0),
('child-folklore', 'Детский фольклор', 0),
('child-prose', 'Детская проза', 0),
('child-sf', 'Детская фантастика', 0),
('child-tale', 'Сказка', 0),
('child-verse', 'Детские стихи', 0),
('children', 'Детская литература: прочее', 1),
('cine', 'Кино', 0),
('comedy', 'Комедия', 0),
('comp-db', 'Базы данных', 0),
('comp-dsp', 'Цифровая обработка сигналов', 0),
('comp-hard', 'Аппаратное обеспечение', 0),
('comp-osnet', 'ОС и Сети', 0),
('comp-programming', 'Программирование', 0),
('comp-soft', 'Программы', 0),
('comp-www', 'Интернет', 0),
('computers', 'Околокомпьютерная литература', 0),
('design', 'Искусство и Дизайн', 1),
('det-action', 'Боевик', 0),
('det-classic', 'Классический детектив', 0),
('det-cozy', 'Дамский детективный роман', 0),
('det-crime', 'Криминальный детектив', 0),
('det-espionage', 'Шпионский детектив', 0),
('det-hard', 'Крутой детектив', 0),
('det-history', 'Исторический детектив', 0),
('det-irony', 'Иронический детектив', 0),
('det-maniac', 'Маньяки', 0),
('det-police', 'Полицейский детектив', 0),
('det-political', 'Политический детектив', 0),
('detective', 'Детективы: прочее', 1),
('dissident', 'Антисоветская литература', 0),
('drama', 'Драма', 0),
('dramaturgy', 'Драматургия: прочее', 1),
('economics', 'Экономика', 0),
('epic', 'Былины', 0),
('epic-poetry', 'Эпическая поэзия', 0),
('epistolary-fiction', 'Эпистолярная проза', 0),
('essay', 'Эссе, очерк, этюд, набросок', 0),
('experimental-poetry', 'Экспериментальная поэзия', 0),
('extravaganza', 'Феерия', 0),
('fable', 'Басни', 0),
('fairy-fantasy', 'Сказочная фантастика', 0),
('fanfiction', 'Фанфик', 0),
('folk-songs', 'Народные песни', 0),
('folk-tale', 'Народные сказки', 0),
('folklore', 'Фольклор: прочее', 0),
('foreign-language', 'Иностранные языки', 0),
('geo-guides', 'Путеводители', 0),
('global-economy', 'Внешняя торговля', 0),
('gothic-novel', 'Готический роман', 0),
('great-story', 'Повесть', 0),
('historical-fantasy', 'Историческое фэнтези', 0),
('home', 'Домоводство', 1),
('home-collecting', 'Коллекционирование', 0),
('home-cooking', 'Кулинария', 0),
('home-crafts', 'Хобби и ремесла', 0),
('home-diy', 'Сделай сам', 0),
('home-entertain', 'Развлечения', 0),
('home-garden', 'Сад и огород', 0),
('home-health', 'Здоровье', 0),
('home-pets', 'Домашние животные', 0),
('home-sex', 'Эротика, Секс', 0),
('home-sport', 'Спорт', 0),
('humor', 'Юмор: прочее', 1),
('humor-anecdote', 'Анекдоты', 0),
('humor-fantasy', 'Юмористическое фэнтези', 0),
('humor-prose', 'Юмористическая проза', 0),
('humor-satire', 'Сатира', 0),
('humor-verse', 'Юмористические стихи', 0),
('in-verse', 'в стихах', 0),
('industries', 'Отраслевые издания', 0),
('job-hunting', 'Поиск работы, карьера', 0),
('limerick', 'Частушки, прибаутки, потешки', 0),
('love', 'О любви', 1),
('love-contemporary', 'Современные любовные романы', 0),
('love-detective', 'Любовные детективы', 0),
('love-erotica', 'Эротика', 0),
('love-hard', 'Порно', 0),
('love-history', 'Исторические любовные романы', 0),
('love-sf', 'Любовная фантастика', 0),
('love-short', 'Короткие любовные романы', 0),
('lyrics', 'Лирика', 0),
('management', 'Управление, подбор персонала', 0),
('marketing', 'Маркетинг, PR, реклама', 0),
('military', 'Военное дело: прочее', 0),
('military-arts', 'Боевые искусства', 0),
('military-history', 'Военная история', 0),
('military-special', 'Спецслужбы ', 0),
('military-weapon', 'Военная техника и вооружение', 0),
('music', 'Музыка', 0),
('mystery', 'Мистерия', 1),
('nonf-biography', 'Биографии и Мемуары', 0),
('nonf-criticism', 'Критика', 0),
('nonf-military', 'Военная документалистика', 0),
('nonf-publicism', 'Публицистика', 0),
('nonfiction', 'Документальная литература', 1),
('notes', 'Партитуры', 0),
('nsf', 'Ненаучная фантастика', 0),
('org-behavior', 'Корпоративная культура', 0),
('other', 'Неотсортированное', 1),
('palindromes', 'Палиндромы', 0),
('palmistry', 'Хиромантия', 0),
('paper-work', 'Делопроизводство', 0),
('periodic', 'Газеты и журналы', 0),
('personal-finance', 'Личные финансы', 0),
('poetry', 'Поэзия: прочее', 1),
('popadanec', 'Попаданцы', 0),
('popular-business', 'О бизнесе популярно', 0),
('prose', 'Проза', 1),
('prose-classic', 'Классическая проза', 0),
('prose-contemporary', 'Современная проза', 0),
('prose-counter', 'Контркультура', 0),
('prose-epic', 'Эпопея', 0),
('prose-game', 'Книга-игра', 0),
('prose-history', 'Историческая проза', 0),
('prose-magic', 'Магический реализм', 0),
('prose-military', 'О войне', 0),
('prose-rus-classic', 'Русская классическая проза', 0),
('prose-sentimental', 'Сентиментальная проза', 0),
('prose-su-classics', 'Советская классическая проза', 0),
('proverbs', 'Пословицы, поговорки', 0),
('psy-childs', 'Детская психология', 0),
('psy-sex-and-family', 'Секс и семейная психология', 0),
('psy-theraphy', 'Психотерапия и консультирование', 0),
('real-estate', 'Недвижимость', 0),
('ref-dict', 'Словари', 0),
('ref-encyc', 'Энциклопедии', 0),
('ref-guide', 'Руководства', 0),
('ref-ref', 'Справочники', 0),
('reference', 'Справочная литература', 1),
('religion', 'Религиозная литература: прочее', 1),
('religion-budda', 'Буддизм', 0),
('religion-catholicism', 'Католицизм', 0),
('religion-christianity', 'Христианство', 0),
('religion-esoterics', 'Эзотерика', 0),
('religion-hinduism', 'Индуизм', 0),
('religion-islam', 'Ислам', 0),
('religion-judaism', 'Иудаизм', 0),
('religion-orthodoxy', 'Православие', 0),
('religion-paganism', 'Язычество', 0),
('religion-protestantism', 'Протестантизм ', 0),
('religion-rel', 'Религия', 0),
('religion-self', 'Самосовершенствование', 0),
('riddles', 'Загадки', 0),
('roman', 'Роман', 0),
('sagas', 'Семейный роман/Семейная сага', 0),
('scenarios', 'Сценарии', 0),
('sci-abstract', 'Рефераты', 0),
('sci-anachem', 'Аналитическая химия', 0),
('sci-biochem', 'Биохимия', 0),
('sci-biology', 'Биология', 0),
('sci-biophys', 'Биофизика', 0),
('sci-botany', 'Ботаника', 0),
('sci-build', 'Строительство и сопромат', 0),
('sci-business', 'Деловая литература', 0),
('sci-chem', 'Химия', 0),
('sci-cosmos', 'Астрономия и Космос', 0),
('sci-crib', 'Шпаргалки', 0),
('sci-culture', 'Культурология', 0),
('sci-ecology', 'Экология', 0),
('sci-economy', 'Экономика', 0),
('sci-geo', 'Геология и география', 0),
('sci-history', 'История', 0),
('sci-juris', 'Юриспруденция', 0),
('sci-linguistic', 'Языкознание', 0),
('sci-math', 'Математика', 0),
('sci-medicine', 'Медицина', 0),
('sci-medicine-alternative', 'Альтернативная медицина', 0),
('sci-metal', 'Металлургия', 0),
('sci-orgchem', 'Органическая химия', 0),
('sci-pedagogy', 'Педагогика', 0),
('sci-philology', 'Литературоведение', 0),
('sci-philosophy', 'Философия', 0),
('sci-phys', 'Физика', 0),
('sci-physchem', 'Физическая химия', 0),
('sci-politics', 'Политика', 0),
('sci-popular', 'Научпоп', 0),
('sci-psychology', 'Психология', 0),
('sci-radio', 'Радиоэлектроника', 0),
('sci-religion', 'Религиоведение', 0),
('sci-social-studies', 'Обществознание', 0),
('sci-state', 'Государство и право', 0),
('sci-tech', 'Технические науки', 0),
('sci-textbook', 'Учебники', 0),
('sci-transport', 'Транспорт и авиация', 0),
('sci-veterinary', 'Ветеринария', 0),
('sci-zoo', 'Зоология', 0),
('science', 'Научная литература: прочее', 1),
('screenplays', 'Киносценарии', 0),
('sf', 'Научная фантастика', 1),
('sf-action', 'Боевая фантастика', 0),
('sf-cyberpunk', 'Киберпанк', 0),
('sf-detective', 'Детективная фантастика', 0),
('sf-epic', 'Эпическая фантастика', 0),
('sf-etc', 'Фантастика: прочее', 0),
('sf-fantasy', 'Фэнтези', 0),
('sf-fantasy-city', 'Городское фэнтези', 0),
('sf-fantasy-irony', 'Ироническое фэнтези', 0),
('sf-heroic', 'Героическая фантастика', 0),
('sf-history', 'Альтернативная история', 0),
('sf-horror', 'Ужасы', 0),
('sf-humor', 'Юмористическая фантастика', 0),
('sf-irony', 'Ироническая фантастика', 0),
('sf-mystic', 'Мистика', 0),
('sf-postapocalyptic', 'Постапокалипсис', 0),
('sf-social', 'Социальная фантастика', 0),
('sf-space', 'Космическая фантастика', 0),
('sf-space-opera', 'Космоопера', 0),
('sf-stimpank', 'Стимпанк', 0),
('sf-technofantasy', 'Технофэнтези', 0),
('short-story', 'Рассказ', 0),
('small-business', 'Малый бизнес', 0),
('song-poetry', 'Песенная поэзия', 0),
('stock', 'Ценные бумаги, инвестиции', 0),
('story', 'Новелла', 0),
('theatre', 'Театр', 0),
('thriller', 'Триллер', 1),
('thriller-legal', 'Юридический триллер', 0),
('thriller-medical', 'Медицинский триллер', 0),
('thriller-techno', 'Техно триллер', 0),
('trade', 'Торговля', 0),
('tragedy', 'Трагедия', 0),
('unfinished', 'Недописанное', 0),
('vaudeville', 'Водевиль', 0),
('vers-libre', 'Верлибры', 0),
('visual-arts', 'Изобразительное искусство, фотография', 0),
('visual-poetry', 'Визуальная поэзия', 0),
('ya', 'Подростковая литература', 0);

update genre set active = 1 where exists(select 1 from book where book.genre = genre.id);
