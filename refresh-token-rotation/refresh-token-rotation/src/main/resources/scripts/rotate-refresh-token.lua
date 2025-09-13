local currentJtiKey = KEYS[1]
local previousJtiKey = KEYS[2]
local accountTokenFamiliesKey = KEYS[3]

local oldJti = ARGV[1]
local newJti = ARGV[2]
local expirationSeconds = tonumber(ARGV[3])
local overlapSeconds = tonumber(ARGV[4])
local familyId = ARGV[5]

local now = redis.call('TIME')[1]
local expirationTimestamp = now + expirationSeconds

local currentJti = redis.call('GET', currentJtiKey)
local previousJti = redis.call('GET', previousJtiKey)

if currentJti == oldJti then
    -- current -> previous (with overlap), new -> current
    redis.call('SETEX', previousJtiKey, overlapSeconds, currentJti)
    redis.call('SETEX', currentJtiKey, expirationSeconds, newJti)

    redis.call('ZADD', accountTokenFamiliesKey, expirationTimestamp, familyId)
    redis.call('EXPIRE', accountTokenFamiliesKey, expirationSeconds)

    return 1
end

if previousJti == oldJti then
    -- new -> current, remove previous
    redis.call('SETEX', currentJtiKey, expirationSeconds, newJti)
    redis.call('DEL', previousJtiKey)

    redis.call('ZADD', accountTokenFamiliesKey, expirationTimestamp, familyId)
    redis.call('EXPIRE', accountTokenFamiliesKey, expirationSeconds)

    return 1
end

-- family Id에 대해서 모두 만료 처리
redis.call('DEL', currentJtiKey)
redis.call('DEL', previousJtiKey)
redis.call('ZREM', accountTokenFamiliesKey, familyId)

return 0