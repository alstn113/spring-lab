local accountTokenFamiliesKey = KEYS[1]

local accountId = tonumber(ARGV[1])

local familyIds = redis.call('ZRANGE', accountTokenFamiliesKey, 0, -1)

local keysToDelete = {}

if #familyIds > 0 then
    for _, familyId in ipairs(familyIds) do
        table.insert(keysToDelete, 'refresh-token:' .. accountId .. ':' .. familyId .. ':current')
        table.insert(keysToDelete, 'refresh-token:' .. accountId .. ':' .. familyId .. ':previous')
    end
end

table.insert(keysToDelete, accountTokenFamiliesKey)

-- 모든 키를 원자적으로 비동기 삭제
if #keysToDelete > 0 then
    redis.call('UNLINK', unpack(keysToDelete))
end

return #familyIds -- 삭제된 familyId의 개수 반환