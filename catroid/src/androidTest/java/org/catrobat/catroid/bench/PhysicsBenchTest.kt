package org.catrobat.catroid.bench

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState
import org.catrobat.catroid.neo3d.Neo3DGameObject
import org.catrobat.catroid.neo3d.Neo3DPhysicsBody
import org.catrobat.catroid.neo3d.physics.JoltPhysicsBackend
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhysicsBenchTest {
    companion object {
        private const val TAG = "PhysicsBench"
        private const val STEPS = 600
        private const val DT = 1f / 60f
        private const val BODY_COUNT = 500
    }

    private fun report(name: String, nanos: LongArray) {
        val sorted = nanos.sorted()
        val avgMs = nanos.average() / 1_000_000.0
        val p95Ms = sorted[(sorted.size * 0.95).toInt()] / 1_000_000.0
        val maxMs = sorted.last() / 1_000_000.0
        Log.i(TAG, "$name n=$BODY_COUNT steps=$STEPS avgMs=$avgMs p95Ms=$p95Ms maxMs=$maxMs")
    }

    @Test
    fun benchJolt() {
        val backend = JoltPhysicsBackend()
        check(backend.initialize()) { "Jolt backend failed to initialize" }
        val sceneId = "bench"
        backend.registerScene(sceneId)
        repeat(BODY_COUNT) { index ->
            val obj = Neo3DGameObject("box$index")
            val col = index % 20
            val row = (index / 20) % 20
            val layer = index / 400
            obj.transform.setPosition(col * 2f - 19f, 2f + row * 1.2f + layer * 25f, 0f)
            obj.physicsBody = Neo3DPhysicsBody(
                Neo3DPhysicsBody.MotionType.DYNAMIC,
                Neo3DPhysicsBody.ShapeType.BOX,
                1f
            )
            backend.syncObject(sceneId, obj)
        }
        check(backend.getBodyCount(sceneId) == BODY_COUNT) { "body count mismatch" }
        repeat(60) { backend.update(sceneId, DT) }
        val nanos = LongArray(STEPS)
        repeat(STEPS) { step ->
            val start = System.nanoTime()
            backend.update(sceneId, DT)
            nanos[step] = System.nanoTime() - start
        }
        report("JOLT", nanos)
        backend.dispose()
    }

    @Test
    fun benchBullet() {
        Bullet.init()
        val config = btDefaultCollisionConfiguration()
        val dispatcher = btCollisionDispatcher(config)
        val broadphase = btDbvtBroadphase()
        val solver = btSequentialImpulseConstraintSolver()
        val world = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, config)
        world.gravity = Vector3(0f, -9.81f, 0f)
        val disposables = ArrayList<com.badlogic.gdx.utils.Disposable>()
        fun addBox(hx: Float, hy: Float, hz: Float, mass: Float, x: Float, y: Float, z: Float) {
            val shape = btBoxShape(Vector3(hx, hy, hz))
            val motionState = btDefaultMotionState()
            motionState.setWorldTransform(Matrix4().setToTranslation(x, y, z))
            val inertia = Vector3()
            if (mass > 0f) {
                shape.calculateLocalInertia(mass, inertia)
            }
            val info = btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia)
            val body = btRigidBody(info)
            world.addRigidBody(body)
            disposables.add(body)
            disposables.add(info)
            disposables.add(motionState)
            disposables.add(shape)
        }
        addBox(100f, 1f, 100f, 0f, 0f, -1f, 0f)
        repeat(BODY_COUNT) { index ->
            val col = index % 20
            val row = (index / 20) % 20
            val layer = index / 400
            addBox(0.5f, 0.5f, 0.5f, 1f, col * 2f - 19f, 2f + row * 1.2f + layer * 25f, 0f)
        }
        repeat(60) { world.stepSimulation(DT, 5, DT) }
        val nanos = LongArray(STEPS)
        repeat(STEPS) { step ->
            val start = System.nanoTime()
            world.stepSimulation(DT, 5, DT)
            nanos[step] = System.nanoTime() - start
        }
        report("BULLET", nanos)
        for (index in disposables.size - 1 downTo 0) {
            disposables[index].dispose()
        }
        world.dispose()
        solver.dispose()
        broadphase.dispose()
        dispatcher.dispose()
        config.dispose()
    }
}
