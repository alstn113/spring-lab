-- 반환값:
--   1 -> 정상 해제
--   0 -> 재진입 카운트 감소 (아직 락 유지)
--  -1 -> unlock 불가 (다른 스레드가 락 소유 중)

local lockKey = KEYS[1]
local lockOwnerId = ARGV[1]

local ownerExists = redis.call('hexists', lockKey, lockOwnerId)
if ownerExists == 0 then
  return -1
end

local counter = redis.call('hincrby', lockKey, lockOwnerId, -1)
if counter > 0 then
  return 0
end

redis.call('del', lockKey)

return 1
