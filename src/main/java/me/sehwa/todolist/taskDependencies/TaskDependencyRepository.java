package me.sehwa.todolist.taskDependencies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    @Query("select t from TaskDependency t where t.previousTask.id = :previousTaskId")
    List<TaskDependency> findAllByPreviousTaskId(@Param("previousTaskId") Long previousTaskId);

    @Query("select t from TaskDependency t where t.nextTask.id = :nextTaskId")
    List<TaskDependency> findAllByNextTaskId(@Param("nextTaskId") Long nextTaskId);

    @Modifying
    @Query("delete from TaskDependency t where t.nextTask.id = :nextTaskId")
    void deleteAllByNextTaskId(@Param("nextTaskId") Long nextTaskId);

    @Query("delete from TaskDependency t where t.nextTask.id = :childTaskId and t.previousTask.id = :parentTaskId")
    void deleteByChildIdAndParentId(@Param("childTaskId") Long childTaskId, @Param("parentTaskId") Long parentTaskId);
}
