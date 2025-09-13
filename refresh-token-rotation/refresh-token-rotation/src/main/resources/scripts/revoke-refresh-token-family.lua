local currentJtiKey = KEYS[1]
local previousJtiKey = KEYS[2]
local accountTokenFamiliesKey = KEYS[3]

local accountId = tonumber(ARGV[1])
local familyId = ARGV[2]
local expirationSeconds = tonumber(ARGV[3])

local now = redis.call('TIME')[1]
local expirationTimestamp = now + expirationSeconds

redis.call('DEL', currentJtiKey)
redis.call('DEL', previousJtiKey)
redis.call('ZREM', accountTokenFamiliesKey, familyId)

if redis.call('ZCARD', accountTokenFamiliesKey) ~= 0 then
    -- score가 제일 높은 것
    local expiration = redis.call('ZREVRANGE', accountTokenFamiliesKey, 0, 0, 'WITHSCORES')[2]
    redis.call('EXPIRE', accountTokenFamiliesKey, expiration - now)
end

