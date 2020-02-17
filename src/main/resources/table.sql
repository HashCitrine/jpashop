drop table if exists cart;
drop table if exists category;
drop table if exists category_item;
drop table if exists comment;
drop table if exists delivery;
drop table if exists hibernate_sequence;
drop table if exists item;
drop table if exists member;
drop table if exists order_item;
drop table if exists orders;
drop table if exists review;

create table cart (id bigint not null, buy bit, count integer not null, item_item_id bigint, member_member_id bigint, order_id bigint, primary key (id)) engine=InnoDB
create table category (item_id bigint not null, name varchar(255), parent_id bigint, primary key (item_id)) engine=InnoDB
create table category_item (category_id bigint not null, item_id bigint not null) engine=InnoDB
create table comment (comment_id bigint not null, date datetime, memo varchar(255), num bigint, parent bigint, sequence datetime, member_id bigint, review_id bigint, primary key (comment_id)) engine=InnoDB
create table delivery (delivery_id bigint not null, status varchar(255), address varchar(255), postcode varchar(255), primary key (delivery_id)) engine=InnoDB
create table hibernate_sequence (next_val bigint) engine=InnoDB
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
create table item (dtype varchar(31) not null, item_id bigint not null, date datetime, item_image varchar(255), memo varchar(255), name varchar(255), price integer not null, stock_quantity integer not null, artist varchar(255), etc varchar(255), author varchar(255), isbn varchar(255), actor varchar(255), director varchar(255), primary key (item_id)) engine=InnoDB
create table member (member_id bigint not null, date datetime, email varchar(255), name varchar(255), password varchar(255), role varchar(255), verify bit not null, verify_code varchar(255), primary key (member_id)) engine=InnoDB
create table orders (order_id bigint not null, order_date datetime, status varchar(255), delivery_id bigint, member_id bigint, primary key (order_id)) engine=InnoDB
create table review (review_id bigint not null, date datetime, memo varchar(255), title varchar(255), item_id bigint, member_id bigint, primary key (review_id)) engine=InnoDB
alter table member add constraint UK_mbmcqelty0fbrvxp1q58dn57t unique (email)
alter table cart add constraint FK25d7w2wbn1uafco1bv5b4g42w foreign key (item_item_id) references item (item_id)
alter table cart add constraint FKjwey6912o3dr3t2muoda4xqqe foreign key (member_member_id) references member (member_id)
alter table category add constraint FK2y94svpmqttx80mshyny85wqr foreign key (parent_id) references category (item_id)
alter table category_item add constraint FKu8b4lwqutcdq3363gf6mlujq foreign key (item_id) references item (item_id)
alter table category_item add constraint FKcq2n0opf5shyh84ex1fhukcbh foreign key (category_id) references category (item_id)
alter table comment add constraint FKmrrrpi513ssu63i2783jyiv9m foreign key (member_id) references member (member_id)
alter table comment add constraint FKnf4ni761w29tmtgdxymmgvg8r foreign key (review_id) references review (review_id)
alter table cart add constraint FKt4dc2r9nbvbujrljv3e23iibt foreign key (order_id) references orders (order_id)
alter table orders add constraint FKtkrur7wg4d8ax0pwgo0vmy20c foreign key (delivery_id) references delivery (delivery_id)
alter table orders add constraint FKpktxwhj3x9m4gth5ff6bkqgeb foreign key (member_id) references member (member_id)
alter table review add constraint FK6hb6qqehnsm7mvfgv37m66hd7 foreign key (item_id) references item (item_id)
alter table review add constraint FKk0ccx5i4ci2wd70vegug074w1 foreign key (member_id) references member (member_id)