package io.github.alstn113.rtr.adapter.out.redis

import io.github.alstn113.rtr.application.port.out.RefreshTokenRegistry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component

@Component
class RefreshTokenRedisRegistry(
    private val redisTemplate: StringRedisTemplate,
    private val refreshTokenProperties: RefreshTokenProperties,
    @Qualifier("registerRefreshTokenScript") private val registerRefreshTokenScript: RedisScript<Long>,
    @Qualifier("rotateRefreshTokenScript") private val rotateRefreshTokenScript: RedisScript<Long>,
    @Qualifier("revokeRefreshTokenFamilyScript") private val revokeRefreshTokenFamilyScript: RedisScript<Long>,
    @Qualifier("revokeRefreshTokenFamiliesScript") private val revokeRefreshTokenFamiliesScript: RedisScript<Long>,
) : RefreshTokenRegistry {

    /**
     * 1. ZSET의 크기가 MAX_CONNECTIONS 이상이면 가장 오래된 familyId 삭제 (current/previous 삭제)
     * 2. ZSET에 familyId 추가 (score: 토큰 만료 시간)
     * 3. current에 jti 저장 (만료 시간 설정)
     */
    override fun register(accountId: Long, familyId: String, jti: String) {
        val currentJtiKey = buildCurrentJtiKey(accountId, familyId)
        val accountTokenFamiliesKey = buildAccountTokenFamiliesKey(accountId)

        val expiration = refreshTokenProperties.expiration
        val expirationSeconds = expiration.toSeconds().toString()

        val keys = listOf(currentJtiKey, accountTokenFamiliesKey)
        val args = listOf(
            accountId.toString(),
            familyId,
            jti,
            expirationSeconds,
            MAX_CONNECTIONS.toString(),
        )

        redisTemplate.execute(
            registerRefreshTokenScript,
            keys,
            *args.toTypedArray()
        )
    }

    /**
     * 1. oldJti가 current와 일치하면 current를 previous로 이동(overlap period), newJti를 current로 설정
     * 2. oldJti가 previous와 일치하면 newJti를 current로 설정, previous는 삭제
     * 3. oldJti가 둘 다와 불일치 -> current/previous 삭제, ZSET에서 familyId 삭제, 실패 반환
     * 4. ZSET에 familyId score를 토큰 만료 시간으로 설정
     */
    override fun rotate(accountId: Long, familyId: String, oldJti: String, newJti: String): Boolean {
        val currentJtiKey = buildCurrentJtiKey(accountId, familyId)
        val previousJtiKey = buildPreviousJtiKey(accountId, familyId)
        val accountTokenFamiliesKey = buildAccountTokenFamiliesKey(accountId)

        val expiration = refreshTokenProperties.expiration
        val overlapPeriod = refreshTokenProperties.overlapPeriod
        val expirationSeconds = expiration.toSeconds()
        val overlapSeconds = overlapPeriod.toSeconds()

        val keys = listOf(currentJtiKey, previousJtiKey, accountTokenFamiliesKey)
        val args = listOf(
            oldJti,
            newJti,
            expirationSeconds.toString(),
            overlapSeconds.toString(),
            familyId,
        )

        val result = redisTemplate.execute(
            rotateRefreshTokenScript,
            keys,
            *args.toTypedArray()
        )

        return result == 1L
    }

    /**
     * 1. current/previous 삭제
     * 2. ZSET에서 familyId 삭제 및 만료일 갱신
     */
    override fun revokeFamily(accountId: Long, familyId: String) {
        val current = buildCurrentJtiKey(accountId, familyId)
        val previous = buildPreviousJtiKey(accountId, familyId)

        val accountTokenFamiliesKey = buildAccountTokenFamiliesKey(accountId)
        val expiration = refreshTokenProperties.expiration
        val expirationSeconds = expiration.toSeconds()

        val keys = listOf(current, previous, accountTokenFamiliesKey)
        val args = listOf(
            accountId.toString(),
            familyId,
            expirationSeconds.toString(),
        )

        redisTemplate.execute(
            revokeRefreshTokenFamilyScript,
            keys,
            *args.toTypedArray()
        )
    }

    /**
     * 1. ZSET에 있는 모든 familyId 조회
     * 2. 각각의 current/previous 삭제
     * 3. ZSET 삭제
     */
    override fun revokeAll(accountId: Long) {
        val accountTokenFamiliesKey = buildAccountTokenFamiliesKey(accountId)

        val keys = listOf(accountTokenFamiliesKey)
        val args = listOf(accountId.toString())

        redisTemplate.execute(
            revokeRefreshTokenFamiliesScript,
            keys,
            *args.toTypedArray()
        )
    }

    fun buildAccountTokenFamiliesKey(accountId: Long): String {
        return "refresh-token:$accountId:families"
    }

    fun buildCurrentJtiKey(accountId: Long, familyId: String): String {
        return "refresh-token:$accountId:$familyId:current"
    }

    fun buildPreviousJtiKey(accountId: Long, familyId: String): String {
        return "refresh-token:$accountId:$familyId:previous"
    }

    companion object {
        private const val MAX_CONNECTIONS = 5
    }
}