.DEFAULT_GOAL := help

COMPOSE := docker compose

ifeq ($(OS),Windows_NT)
SHELL := cmd.exe
.SHELLFLAGS := /C
endif

-include .env

POSTGRES_USER ?= postgres
POSTGRES_DB ?= postgres

.PHONY: help config pull up down restart ps logs health verify clear

help:
	@echo "Available targets:"
	@echo "  make config              Validate Docker Compose"
	@echo "  make up                  Pull PostgreSQL 16 and start it"
	@echo "  make down                Stop containers and keep data"
	@echo "  make restart             Recreate containers with the newest PostgreSQL 16 image"
	@echo "  make ps                  Show container status"
	@echo "  make logs                Follow PostgreSQL logs"
	@echo "  make health              Run pg_isready"
	@echo "  make verify              Run a SQL smoke check"
	@echo "  make clear CONFIRM=YES   Delete containers, image and database volume"

config:
	$(COMPOSE) config --quiet

pull:
	$(COMPOSE) pull postgres

up: config pull
	$(COMPOSE) up -d --wait postgres

down:
	$(COMPOSE) down --remove-orphans

restart: down pull
	$(COMPOSE) up -d --wait postgres

ps:
	$(COMPOSE) ps

logs:
	$(COMPOSE) logs --follow postgres

health:
	$(COMPOSE) exec -T postgres pg_isready -U $(POSTGRES_USER) -d $(POSTGRES_DB)

verify:
	$(COMPOSE) exec -T postgres psql -U $(POSTGRES_USER) -d $(POSTGRES_DB) -v ON_ERROR_STOP=1 -c "SELECT current_user, current_database(), version();"

clear:
ifeq ($(CONFIRM),YES)
	$(COMPOSE) down --volumes --rmi all --remove-orphans
else
	@echo "Refusing to delete PostgreSQL data."
	@echo "Run 'make clear CONFIRM=YES' only when you intentionally want a full reset."
endif
