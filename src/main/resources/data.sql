-- 게시글 카테고리 초기 데이터
INSERT IGNORE INTO post_category(id, name, description)
VALUES (1, '공지', '공지 설명');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (2, '자유', '자유 설명');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (3, '서울',
        '서울은 한국의 수도이자 경제, 문화, 정치의 중심지입니다. 이 도시는 전통과 현대가 완벽하게 어우러진 곳으로, 경복궁과 같은 고풍스러운 궁궐에서부터, 광화문, 명동, 강남의 번화한 거리까지 다양한 매력을 지니고 있습니다. 또한, N서울타워와 같은 현대적인 건축물이 서울의 하늘을 수놓고, 한강을 따라 펼쳐진 아름다운 경관은 서울을 더욱 특별하게 만듭니다. 서울은 한국 전통 문화와 첨단 기술이 공존하는 글로벌 도시로, 매년 많은 관광객들이 찾는 명소입니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (4, '부산',
        '부산은 한국에서 두 번째로 큰 도시이자, 동남부에 위치한 대표적인 항구 도시입니다. 부산은 해양 문화와 전통이 깊은 도시로, 아름다운 해운대 해변과 광안대교의 야경이 유명합니다. 부산은 또한 국제 영화제인 부산국제영화제가 열리는 곳으로, 문화와 예술의 중심지로 자리매김하고 있습니다. 또한, 자갈치 시장과 같은 전통 시장에서 맛볼 수 있는 신선한 해산물은 부산의 자랑입니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (5, '대구',
        '대구는 매년 대구국제뮤지컬페스티벌과 같은 다양한 문화 행사와 공연이 열리며, 쇼핑과 음식이 풍부한 곳으로 관광객들에게 인기입니다. 또한, 팔공산과 같은 자연 명소가 있어 도심을 벗어나 자연을 즐기기에도 좋습니다. 대구는 ‘향토 음식의 수도’로 불리며, 다양한 지역 음식을 즐길 수 있는 기회도 많습니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (6, '인천',
        '인천은 서울의 서쪽에 위치한 중요한 항구 도시로, 한국의 대표적인 국제 공항인 인천국제공항이 자리하고 있습니다. 송도국제도시와 같은 현대적인 개발지구가 인천의 발전을 이끌고 있으며, 인천의 아름다운 해안선과 섬들은 관광객들에게 인기가 많습니다. 또한, 인천은 역사적 유적지인 인천 차이나타운과 제물포 구항 등도 있어 전통과 현대를 아우르는 독특한 매력을 지닌 도시입니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (7, '광주',
        '광주는 한국의 대표적인 예술과 문화의 중심지로, 광주비엔날레와 같은 세계적인 미술 행사들이 열리며, 예술의 도시로 명성을 떨칩니다. 광주는 또한 5·18 광주 민주화 운동의 역사적 배경을 지닌 도시로, 민주화와 인권을 상징하는 곳입니다. 광주의 풍경은 자연과 문화가 잘 결합되어 있으며, 무등산과 같은 자연 명소와 함께 전통적인 건축물들이 이 지역을 더욱 특별하게 만듭니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (8, '대전',
        '대전은 과학과 기술, 교육의 중심지로 알려져 있으며, 대한민국의 주요 연구소와 대학교들이 자리하고 있는 도시입니다. 대전엑스포공원과 같은 과학적인 전시와 문화공연이 있는 장소들은 대전의 주요 명소로, 과학과 기술을 배경으로 한 다양한 활동을 즐길 수 있습니다. 또한, 갑천변과 같은 자연 경관도 있어, 도심 속에서도 자연을 느낄 수 있는 공간이 많습니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (9, '울산',
        '울산은 한국의 산업과 경제 중심지로, 현대자동차와 울산항을 비롯한 중요한 산업 기반이 자리잡고 있습니다. 울산은 또한 아름다운 해변과 자연을 자랑하는 도시로, 간절곶에서의 일출은 매우 유명합니다. 울산의 고래문화와 같은 독특한 문화와 역사를 체험할 수 있으며, 다양한 관광명소와 문화행사도 많습니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (10, '세종',
        '세종시는 대한민국의 행정 중심지로, 모든 정부 기관이 위치한 계획적인 도시입니다. 정부세종청사와 같은 주요 행정시설과 함께, 이 도시는 정부의 중심지 역할을 하며, 효율적인 행정 시스템을 구축하는 곳입니다. 세종시는 또한 가족 친화적인 환경과 넓은 녹지 공간을 제공하여 주거지로도 매우 인기가 많습니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (11, '경기도',
        '경기도는 서울을 둘러싼 광범위한 지역으로, 서울과의 교통이 매우 편리하며, 다양한 관광지와 명소가 많은 지역입니다. 경기도는 자연과 도시가 조화를 이루는 곳으로, 수원 화성과 같은 역사적인 유적지부터, 에버랜드와 같은 현대적인 놀이공원까지 다양한 볼거리가 있습니다. 또한, 수많은 호수와 산들이 있어 자연을 즐기기에 좋은 곳입니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (12, '충청북도',
        '충청북도는 자연경관과 역사적인 유적이 많은 지역으로, 청주와 보은 등에서 전통적인 문화를 느낄 수 있습니다. 충청북도는 속리산, 제천, 단양과 같은 자연 관광지와 함께, 충주호와 같은 아름다운 호수도 있어 관광객들에게 많은 사랑을 받고 있습니다. 또한, 청주 상당산성 등 역사적인 명소도 많아 다양한 체험을 제공합니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (13, '충청남도',
        '충청남도는 아름다운 해안선과 역사적인 유적들이 있는 지역으로, 태안 해변과 공주가 대표적인 관광지입니다. 충청남도는 또한 온양 온천과 같은 유명한 휴양지와 함께, 공주 공산성 등 고대 유적도 많이 남아 있어, 자연과 역사를 동시에 즐길 수 있는 곳입니다. 충청남도는 맛있는 지역 음식과 함께, 관광과 힐링을 동시에 즐길 수 있는 지역입니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (14, '전라북도',
        '전라북도는 전통과 현대가 어우러진 지역으로, 특히 전주가 유명합니다. 전주는 한옥마을과 전통 한식으로 유명하며, 한국 전통문화의 중심지로 잘 알려져 있습니다. 또한, 군산의 근대문화유산과 정읍의 아름다운 자연경관은 관광객들에게 큰 인기를 끌고 있습니다. 전라북도는 또한 ‘고창의 갯벌’과 같은 자연유산과 함께, 맛있는 지역 음식들이 많은 곳으로도 유명합니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (15, '전라남도',
        '전라남도는 아름다운 섬들과 바다, 그리고 풍성한 자연경관이 특징인 지역입니다. 전라남도의 대표적인 명소로는 여수의 해상 케이블카와 순천만 자연생태공원이 있습니다. 이 지역은 한국 전통의 문화유산도 많이 보존하고 있으며, 풍성한 농산물과 해산물로 미식가들에게도 인기가 많습니다. 전라남도는 자연과 문화가 어우러져 여행객들에게 다양한 경험을 제공합니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (16, '경상북도',
        '경상북도는 유서 깊은 사찰과 고대 유적지들이 많아 역사와 문화가 풍부한 지역입니다. 경주는 불국사와 석굴암과 같은 세계문화유산이 있어 많은 관광객들이 찾는 명소로 알려져 있습니다. 또한, 안동 하회마을은 전통적인 한국 문화를 체험할 수 있는 곳으로, 전통 가옥과 고유의 민속 문화가 잘 보존되어 있습니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (17, '경상남도',
        '경상남도는 풍부한 농업과 바다, 그리고 역사적인 유적들이 공존하는 지역입니다. 통영의 한려수도는 국내외에서 유명한 관광지로, 아름다운 자연경관을 자랑합니다. 진주성은 경상남도의 역사적 상징으로, 진주지역의 중요한 유적지입니다. 경상남도는 또한 다양한 지역 음식을 즐길 수 있어 미식 여행으로도 좋습니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (18, '강원도',
        '강원도는 아름다운 자연경관과 스키 리조트로 유명한 지역으로, 사계절 내내 관광객들이 찾아옵니다. 겨울에는 평창과 강릉에서 즐길 수 있는 스키와 스노보드가 인기를 끌며, 여름에는 설악산과 같은 산악 지역과 함께 동해안의 해변들이 많은 사랑을 받습니다. 또한, 강원도는 전통적인 음식과 함께, 다양한 자연 체험을 할 수 있는 명소들이 많습니다. 평창 동계올림픽이 열린 곳으로도 유명하며, 자연과 스포츠가 결합된 매력적인 지역입니다.');

INSERT IGNORE INTO post_category(id, name, description)
VALUES (19, '제주도',
        '제주도는 한국에서 가장 유명한 관광지로, 화산섬의 독특한 자연과 아름다운 해변, 그리고 풍부한 문화유산이 매력적인 곳입니다. 한라산과 같은 명산이 있고, 제주 올레길은 트레킹을 즐기기에 최적의 장소로 알려져 있습니다. 제주도는 또한 돌하르방과 같은 전통적인 상징물과 제주 흑돼지와 같은 특산물로 미식 여행지로도 유명합니다. 제주도는 풍부한 자연과 독특한 문화, 여유로운 분위기로 국내외 관광객들에게 큰 사랑을 받는 곳입니다.');

-- 지역 카테고리 초기 데이터
INSERT IGNORE INTO region(id, name)
VALUES (1, '서울');

INSERT IGNORE INTO region(id, name)
VALUES (2, '부산');

INSERT IGNORE INTO region(id, name)
VALUES (3, '대구');

INSERT IGNORE INTO region(id, name)
VALUES (4, '인천');

INSERT IGNORE INTO region(id, name)
VALUES (5, '광주');

INSERT IGNORE INTO region(id, name)
VALUES (6, '대전');

INSERT IGNORE INTO region(id, name)
VALUES (7, '울산');

INSERT IGNORE INTO region(id, name)
VALUES (8, '세종');

INSERT IGNORE INTO region(id, name)
VALUES (9, '경기도');

INSERT IGNORE INTO region(id, name)
VALUES (10, '충청북도');

INSERT IGNORE INTO region(id, name)
VALUES (11, '충청남도');

INSERT IGNORE INTO region(id, name)
VALUES (12, '전라북도');

INSERT IGNORE INTO region(id, name)
VALUES (13, '전라남도');

INSERT IGNORE INTO region(id, name)
VALUES (14, '경상북도');

INSERT IGNORE INTO region(id, name)
VALUES (15, '경상남도');

INSERT IGNORE INTO region(id, name)
VALUES (16, '강원도');

INSERT IGNORE INTO region(id, name)
VALUES (17, '제주도');