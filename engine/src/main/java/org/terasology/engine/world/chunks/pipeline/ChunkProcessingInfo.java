// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.engine.world.chunks.pipeline;

import com.google.common.util.concurrent.SettableFuture;
import org.joml.Vector3ic;
import org.terasology.engine.world.chunks.pipeline.stages.ChunkTask;
import org.terasology.engine.world.chunks.pipeline.stages.ChunkTaskProvider;
import org.terasology.engine.world.chunks.Chunk;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public final class ChunkProcessingInfo {
    public final ReentrantLock lock = new ReentrantLock();
    private final Vector3ic position;

    private Chunk chunk;
    private ChunkTaskProvider chunkTaskProvider;

    private org.terasology.engine.world.chunks.pipeline.stages.ChunkTask chunkTask;

    public ChunkProcessingInfo(Vector3ic position) {
        this.position = position;
    }

    public Vector3ic getPosition() {
        return position;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public ChunkTaskProvider getChunkTaskProvider() {
        return chunkTaskProvider;
    }

    public void setChunkTaskProvider(ChunkTaskProvider chunkTaskProvider) {
        this.chunkTaskProvider = chunkTaskProvider;
    }

    public org.terasology.engine.world.chunks.pipeline.stages.ChunkTask getChunkTask() {
        return chunkTask;
    }

    public void setChunkTask(org.terasology.engine.world.chunks.pipeline.stages.ChunkTask chunkTask) {
        this.chunkTask = chunkTask;
    }

    boolean hasNextStage(List<ChunkTaskProvider> stages) {
        if (chunkTaskProvider == null) {
            return true;
        } else {
            return stages.indexOf(chunkTaskProvider) != stages.size() - 1;
        }
    }

    void nextStage(List<ChunkTaskProvider> stages) {
        int nextStageIndex =
                chunkTaskProvider == null
                        ? 0
                        : stages.indexOf(chunkTaskProvider) + 1;
        chunkTaskProvider = stages.get(nextStageIndex);
    }

    ChunkTask makeChunkTask() {
        if (chunkTask == null) {
            chunkTask = chunkTaskProvider.createChunkTask(position);
        }
        return chunkTask;
    }

    void resetTaskState() {
        chunkTask = null;
    }
}
