package me.sehwa.todolist.taskDependencies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    @Query("select t from TaskDependency t where t.previous.id = :id")
    List<TaskDependency> findAllByPreviousId(@Param("id") Long id);

    @Query("select t from TaskDependency t where t.next.id = :id")
    List<TaskDependency> findAllByNextId(@Param("id") Long id);
}
