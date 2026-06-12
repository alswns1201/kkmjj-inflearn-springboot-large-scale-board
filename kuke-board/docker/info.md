
# kafka topic 내용  create ( 위치 : /opt/kafka/bin $ )
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic kuke-board-article --replication-factor 1 --partitions 3
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic kuke-board-comment --replication-factor 1 --partitions 3
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic kuke-board-like --replication-factor 1 --partitions 3
./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic kuke-board-view --replication-factor 1 --partitions 3