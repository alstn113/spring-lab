-- 반환값:
--  -1 -> 락 획득 성공 (새로 획득 또는 재진입)
--   n -> 락 획득 실패, 남은 TTL (ms)

local lockKey = KEYS[1]
local leaseMillis = tonumber(ARGV[1])
local lockOwnerId = ARGV[2]

-- 동일 스레드(owner)가 이미 락을 가지고 있다면 재진입
if (redis.call('hexists', lockKey, lockOwnerId) == 1) then
    redis.call('hincrby', lockKey, lockOwnerId, 1)
    redis.call('pexpire', lockKey, leaseMillis)
    return -1  -- 재진입 성공
end

-- 락이 존재하지 않으면 새로 획득
if (redis.call('exists', lockKey) == 0) then
    redis.call('hset', lockKey, lockOwnerId, 1)
    redis.call('pexpire', lockKey, leaseMillis)
    return -1  -- 성공
end

-- 다른 스레드/프로세스가 락 보유 중인 경우 TTL 반환
return redis.call('pttl', lockKey)