package com.dangdang.cymbal.service.constant;

/**
 * Constants in service module.
 *
 * @auther GeZhen
 */
public class Constant {

    public class Shell {

        public static final int EXIT_VALUE_OK = 0;
    }

    public class Redis {

        public static final String EXECUTE_RESULT_SUCCESS = "OK";

        public static final String EXECUTE_RESULT_NOKEY = "NOKEY";

        public static final int CLUSTER_ID_LENGTH = 8;
    }

    public class Strings {

        public static final String EMPTY_PASSWORD = "''";

        public static final String EMPTY = "";

        public static final String BLANK_SPACE = " ";
    }

    public class RedisConfig {

        public static final String SENTINEL_CONFIG_VERSION = "redis-sentinel";

        public static final String APPENDONLY_NO = "no";

        public static final String SAVE_OFF = "\"\"";
    }

    public class RedisInfo {

        public static final String TOTAL_NET_INPUT_BYTES = "total_net_input_bytes";

        public static final String TOTAL_NET_OUTPUT_BYTES = "total_net_output_bytes";

        public static final String KEYSPACE_HITS = "keyspace_hits";

        public static final String KEYSPACE_MISSES = "keyspace_misses";

        public static final String TOTAL_CONNECTIONS_RECEIVED = "total_connections_received";

        public static final String TOTAL_COMMANDS_PROCESSED = "total_commands_processed";

        public static final String USED_MEMORY = "used_memory";

        public static final String CONNECTED_CLIENTS = "connected_clients";

        public static final String INSTANTANEOUS_OUTPUT_KBPS = "instantaneous_output_kbps";
    }
}
