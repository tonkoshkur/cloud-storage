create schema if not exists cloudstorage;

create table user
(
    id       int8 auto_increment primary key,
    username varchar(256) unique,
    password varchar(256) not null
);