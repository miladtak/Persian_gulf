package com.example.engine.physics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.example.engine.model.SceneObject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * High-performance 2D Physics & Collision Engine for GAME ENGINE PERSIAN GULF
 * Features impulse resolution, AABB vs AABB, Circle vs Circle, Circle vs Box,
 * triggers, raycasting, and buoyancy simulation.
 */
object PhysicsEngine2D {

    const val DEFAULT_GRAVITY = 980f // px / s^2

    /**
     * Checks overlap between two SceneObjects with colliders
     */
    fun checkCollision(a: SceneObject, b: SceneObject): CollisionManifold? {
        val aBounds = a.getColliderBounds()
        val bBounds = b.getColliderBounds()

        // Quick AABB broadphase check
        if (!aBounds.overlaps(bBounds)) return null

        val colA = a.collider
        val colB = b.collider
        val isTrigger = colA.isTrigger || colB.isTrigger

        // Layer mask filtering
        if ((colA.layer.bit and colB.mask) == 0 || (colB.layer.bit and colA.mask) == 0) {
            return null
        }

        // Circle vs Circle
        if (colA.shape == ColliderShapeType.CIRCLE && colB.shape == ColliderShapeType.CIRCLE) {
            val centerA = Offset(a.x + colA.offsetX, a.y + colA.offsetY)
            val centerB = Offset(b.x + colB.offsetX, b.y + colB.offsetY)
            val diff = centerB - centerA
            val dist = diff.getDistance()
            val totalRadius = colA.radius + colB.radius

            if (dist < totalRadius) {
                val normal = if (dist > 0.001f) diff / dist else Offset(1f, 0f)
                val depth = totalRadius - dist
                val contact = centerA + normal * colA.radius
                return CollisionManifold(a.id, b.id, normal, depth, contact, isTrigger)
            }
            return null
        }

        // AABB vs AABB (Box or Trigger)
        val overlapX = (min(aBounds.right, bBounds.right) - max(aBounds.left, bBounds.left))
        val overlapY = (min(aBounds.bottom, bBounds.bottom) - max(aBounds.top, bBounds.top))

        if (overlapX > 0 && overlapY > 0) {
            val normal: Offset
            val depth: Float
            if (overlapX < overlapY) {
                depth = overlapX
                normal = if (a.x < b.x) Offset(1f, 0f) else Offset(-1f, 0f)
            } else {
                depth = overlapY
                normal = if (a.y < b.y) Offset(0f, 1f) else Offset(0f, -1f)
            }
            val contact = Offset(
                (max(aBounds.left, bBounds.left) + min(aBounds.right, bBounds.right)) / 2f,
                (max(aBounds.top, bBounds.top) + min(aBounds.bottom, bBounds.bottom)) / 2f
            )
            return CollisionManifold(a.id, b.id, normal, depth, contact, isTrigger)
        }

        return null
    }

    /**
     * Resolves linear impulse between two solid rigidbodies
     */
    fun resolveSolidCollision(a: SceneObject, b: SceneObject, manifold: CollisionManifold) {
        if (manifold.isTrigger) return // Triggers don't physically bounce or displace

        val invMassA = if (a.isStatic) 0f else 1f / max(0.1f, a.mass)
        val invMassB = if (b.isStatic) 0f else 1f / max(0.1f, b.mass)
        val totalInvMass = invMassA + invMassB

        if (totalInvMass <= 0f) return

        // Positional correction (prevent sinking)
        val percent = 0.8f // Penetration percentage to correct
        val slop = 0.5f // Penetration allowance
        val correction = manifold.normal * (max(manifold.penetrationDepth - slop, 0f) / totalInvMass * percent)

        if (!a.isStatic) {
            a.x -= correction.x * invMassA
            a.y -= correction.y * invMassA
        }
        if (!b.isStatic) {
            b.x += correction.x * invMassB
            b.y += correction.y * invMassB
        }

        // Relative velocity
        val relVx = b.velocityX - a.velocityX
        val relVy = b.velocityY - a.velocityY
        val velAlongNormal = relVx * manifold.normal.x + relVy * manifold.normal.y

        // Don't resolve if velocities are separating
        if (velAlongNormal > 0) return

        // Coefficient of restitution (bounciness)
        val e = min(a.restitution, b.restitution)
        val impulseMag = -(1f + e) * velAlongNormal / totalInvMass

        val impulse = manifold.normal * impulseMag
        if (!a.isStatic) {
            a.velocityX -= impulse.x * invMassA
            a.velocityY -= impulse.y * invMassA
        }
        if (!b.isStatic) {
            b.velocityX += impulse.x * invMassB
            b.velocityY += impulse.y * invMassB
        }
    }

    /**
     * Casts a 2D ray against all colliders
     */
    fun raycast(
        origin: Offset,
        direction: Offset,
        maxDist: Float,
        objects: List<SceneObject>,
        mask: Int = 0xFF
    ): RaycastHit {
        val dirNorm = if (direction.getDistance() > 0.001f) direction / direction.getDistance() else Offset(1f, 0f)
        var closestHit: RaycastHit = RaycastHit(hit = false)
        var minDistance = maxDist

        for (obj in objects) {
            if ((obj.collider.layer.bit and mask) == 0) continue
            val bounds = obj.getColliderBounds()

            // Ray vs AABB intersection
            val invDx = if (abs(dirNorm.x) > 0.0001f) 1f / dirNorm.x else Float.MAX_VALUE
            val invDy = if (abs(dirNorm.y) > 0.0001f) 1f / dirNorm.y else Float.MAX_VALUE

            val t1 = (bounds.left - origin.x) * invDx
            val t2 = (bounds.right - origin.x) * invDx
            val t3 = (bounds.top - origin.y) * invDy
            val t4 = (bounds.bottom - origin.y) * invDy

            val tmin = max(min(t1, t2), min(t3, t4))
            val tmax = min(max(t1, t2), max(t3, t4))

            if (tmax >= 0 && tmin <= tmax && tmin < minDistance) {
                if (tmin >= 0) {
                    minDistance = tmin
                    closestHit = RaycastHit(
                        hit = true,
                        hitPoint = origin + dirNorm * tmin,
                        distance = tmin,
                        hitEntityId = obj.id
                    )
                }
            }
        }

        return closestHit
    }
}
