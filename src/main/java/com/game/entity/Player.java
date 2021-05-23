package com.game.entity;

import javax.persistence.*;
import java.util.Date;

/**
 Сущность:
 сущность представляет собой единственный экземпляр объекта домена, сохраненный в базе данных как запись.
 У него есть некоторые атрибуты, в виде столбцов в нашей таблицах.
 - хранит в себе данные
 - взаимодействует с БД для получения данных
 - отдает данные контроллеру
 */

@Entity
// указывает, что данный бин (класс) является сущностью
@Table(name = "player")
// указывает на имя таблицы, которая будет отображаться в этой сущности
public class Player {

    @Id
    @Column(name = "id")
    // указывает на имя колонки, которая отображается в свойство сущности
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // указывает, что данное свойство будет создаваться согласно указанной стратегии
    // GenerationType.IDENTITY - самый простой способ конфигурирования генератора.
    // Он опирается на auto-increment колонку в таблице.
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "race")
    @Enumerated(EnumType.STRING)
    // устанавливаем, что Enum будем хранить в видже строки
    private Race race;

    @Column(name = "profession")
    @Enumerated(EnumType.STRING)
    private Profession profession;

    // опыт персонажа
    @Column(name = "experience")
    private Integer experience;

    @Column(name = "level")
    private Integer level;

    // остаток опыта до следующего уровня
    @Column(name = "untilNextLevel")
    private Integer untilNextLevel;

    @Column(name = "birthday")
    @Temporal(TemporalType.DATE)
    // используем аннотацию @Temporal для вставки даты, времени или того и другого в таблицу базы данных.
    // Используя TemporalType, мы можем вставлять данные, время или и то, и другое в таблицу int.
    // @Temporal(TemporalType.DATE) // insert date
    //@Temporal(TemporalType.TIME) // insert time
    //@Temporal(TemporalType.TIMESTAMP) // insert  both time and date.
    private Date birthday;

    @Column(name = "banned")
    private Boolean banned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }
}
