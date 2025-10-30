-- 반환값:
--  -1 -> 락 획득 성공 (새로 획득 또는 재진입)
--   n -> 락 획득 실패, 남은 TTL (ms)

local lockKey = KEYS[1]
local leaseMillis = tonumber(ARGV[1])
local lockOwnerId = ARGV[2]

-- 락이 존재하지 않거나, 동일한 소유자가 재진입하는 경우 락 획득 성공
if ((redis.call('exists', lockKey) == 0) or (redis.call('hexists', lockKey, lockOwnerId) == 1)) then
    redis.call('hincrby', lockKey, lockOwnerId, 1);
    redis.call('pexpire', lockKey, leaseMillis);
    return -1;
end;

return redis.call('pttl', lockKey);
