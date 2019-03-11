-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema cookplanner
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cookplanner
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cookplanner` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `cookplanner` ;

-- -----------------------------------------------------
-- Table `cookplanner`.`ingredient_name`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cookplanner`.`ingredient_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_gqwucwtphhplubuwiy5nwjo9n` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cookplanner`.`recipe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cookplanner`.`recipe` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `cook_time` INT(11) NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `directions` LONGTEXT NULL DEFAULT NULL,
  `image` VARCHAR(255) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `notes` LONGTEXT NULL DEFAULT NULL,
  `preparation_time` INT(11) NULL DEFAULT NULL,
  `preparations` LONGTEXT NULL DEFAULT NULL,
  `rating` INT(11) NULL DEFAULT NULL,
  `recipe_type` VARCHAR(20) NOT NULL,
  `servings` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cookplanner`.`measure_unit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cookplanner`.`measure_unit` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `plural_name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_p23q75p8cvu6ma61yina6o93i` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cookplanner`.`ingredient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cookplanner`.`ingredient` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `amount` FLOAT NULL DEFAULT NULL,
  `stock` BIT(1) NOT NULL,
  `measure_unit_id` BIGINT(20) NULL DEFAULT NULL,
  `name_id` BIGINT(20) NULL DEFAULT NULL,
  `recipe_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKkhyr0pdxjf8sugsg81vsijqky` (`measure_unit_id` ASC) VISIBLE,
  INDEX `FK8n4eiut3pgy2p5ri4dsca50p` (`name_id` ASC) VISIBLE,
  INDEX `FKj0s4ywmqqqw4h5iommigh5yja` (`recipe_id` ASC) VISIBLE,
  CONSTRAINT `FK8n4eiut3pgy2p5ri4dsca50p`
    FOREIGN KEY (`name_id`)
    REFERENCES `cookplanner`.`ingredient_name` (`id`),
  CONSTRAINT `FKj0s4ywmqqqw4h5iommigh5yja`
    FOREIGN KEY (`recipe_id`)
    REFERENCES `cookplanner`.`recipe` (`id`),
  CONSTRAINT `FKkhyr0pdxjf8sugsg81vsijqky`
    FOREIGN KEY (`measure_unit_id`)
    REFERENCES `cookplanner`.`measure_unit` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cookplanner`.`planning`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cookplanner`.`planning` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `date` DATE NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `on_shopping_list` BIT(1) NOT NULL,
  `servings` INT(11) NULL DEFAULT NULL,
  `recipe_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK3ur82p86jic0nunkmudnfef2a` (`recipe_id` ASC) VISIBLE,
  CONSTRAINT `FK3ur82p86jic0nunkmudnfef2a`
    FOREIGN KEY (`recipe_id`)
    REFERENCES `cookplanner`.`recipe` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cookplanner`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cookplanner`.`tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_1wdpsed5kna2y38hnbgrnhi5b` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cookplanner`.`recipe_tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cookplanner`.`recipe_tag` (
  `recipe_id` BIGINT(20) NOT NULL,
  `tag_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`recipe_id`, `tag_id`),
  INDEX `FKnk9vj1t1tc0rsdmlhm4rger7e` (`tag_id` ASC) VISIBLE,
  CONSTRAINT `FKnk9vj1t1tc0rsdmlhm4rger7e`
    FOREIGN KEY (`tag_id`)
    REFERENCES `cookplanner`.`tag` (`id`),
  CONSTRAINT `FKshildcupwo2vlv8sjyxjlpi8l`
    FOREIGN KEY (`recipe_id`)
    REFERENCES `cookplanner`.`recipe` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

