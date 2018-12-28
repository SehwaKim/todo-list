package me.sehwa.todolist.taskDependencies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    @Query("select t from TaskDependency t where t.parentTask.id = :parentTaskId")
    List<TaskDependency> findAllByParentTaskId(@Param("parentTaskId") Long parentTaskId);

    @Query("select t from TaskDependency t where t.childTask.id = :childTaskId")
    List<TaskDependency> findAllByChildTaskId(@Param("childTaskId") Long childTaskId);

    @Modifying
    @Query("delete from TaskDependency t where t.childTask.id = :childTaskId")
    void deleteAllByChildTaskId(@Param("childTaskId") Long childTaskId);

    @Modifying
    @Query("delete from TaskDependency t where t.childTask.id = :childTaskId and t.parentTask.id = :parentTaskId")
    void deleteByChildTaskIdAndParentTaskId(@Param("childTaskId") Long childTaskId, @Param("parentTaskId") Long parentTaskId);
}
