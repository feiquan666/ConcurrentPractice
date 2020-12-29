package example.flink;

import java.util.Arrays;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class FlinkTest {

	public static void main(String[] args) throws Exception {
		// 创建执行环境
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 获取源数据
		DataStream<String> lines = env.socketTextStream("localhost", 8888);
		// 数据处理
		DataStream<String> word = lines
				.flatMap((String line, Collector<String> out) -> Arrays.stream(line.split(" "))
						.forEach(out::collect)).returns(Types.STRING);
		DataStream<Tuple2<String, Integer>> wordAndOne = word.map(w -> Tuple2.of(w, 1)).returns(
				Types.TUPLE(Types.STRING, Types.INT));
		SingleOutputStreamOperator<Tuple2<String, Integer>> sumed = wordAndOne.keyBy(0).sum(1);
		// 结果输出
		sumed.print();
		// 启动执行
		env.execute();
	}
}
