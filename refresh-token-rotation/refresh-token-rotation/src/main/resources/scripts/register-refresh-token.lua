local currentJtiKey = KEYS[1]
local accountTokenFamiliesKey = KEYS[2]

local accountId = tonumber(ARGV[1])
local familyId = ARGV[2]
local jti = ARGV[3]
local expirationSeconds = tonumber(ARGV[4])
local maxConnections = tonumber(ARGV[5])

local now = redis.call('TIME')[1]
local expirationTimestamp = now + expirationSeconds

if redis.call('ZCARD', accountTokenFamiliesKey) >= maxConnections then
    local oldestFamilyId = redis.call('ZRANGE', accountTokenFamiliesKey, 0, 0)[1]
    redis.call('ZREM', accountTokenFamiliesKey, oldestFamilyId)
    redis.call('DEL', 'refresh-token:' .. accountId .. ':' .. oldestFamilyId .. ':current')
    redis.call('DEL', 'refresh-token:' .. accountId .. ':' .. oldestFamilyId .. ':previous')
end

redis.call('SETEX', currentJtiKey, expirationSeconds, jti)
redis.call('ZADD', accountTokenFamiliesKey, expirationTimestamp, familyId)
redis.call('EXPIRE', accountTokenFamiliesKey, expirationSeconds)

return 1
