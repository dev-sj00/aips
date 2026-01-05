local size = redis.call('ZCARD', KEYS[1])

local limit = tonumber(ARGV[1])

if not size then
  return redis.error_reply("ZCARD failed")
end

if not limit then
  return redis.error_reply("Invalid limit value")
end

size = tonumber(size)

if size > limit then
  local members = redis.call('ZRANGE', KEYS[1], 0, 0)

  if members and #members > 0 then
    local lowestHashKey = members[1]
    redis.call('DEL', lowestHashKey)
    redis.call('ZREM', KEYS[1], lowestHashKey)
  end
end

