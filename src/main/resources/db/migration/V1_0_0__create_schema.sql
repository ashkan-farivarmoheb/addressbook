create schema if not exists reece;

create table if not exists reece.usr (
	id bigserial not null PRIMARY KEY,
	name text not null, 
	constraint user_name_uk unique(name),
	created_by TEXT NOT NULL,
    created_date bigint NOT NULL,
    last_modified_date bigint NOT NULL,
    last_modified_by TEXT NOT NULL
);
	
Create index idx_user_name
	on reece.usr(name);

create table if not exists reece.address_book (
	id bigserial not null PRIMARY KEY,
	name text not null,
	user_id bigint not null,
	created_by TEXT NOT NULL,
    created_date bigint NOT NULL,
    last_modified_date bigint NOT NULL,
    last_modified_by TEXT NOT NULL,
	constraint address_book_uk unique(name, user_id),
	constraint address_book_user_fk
            foreign key(user_id)
            references reece.usr(id) match simple
            on update no action
            on delete cascade
);

Create index idx_address_book_user_id
	on reece.address_book(name, user_id);

create table if not exists reece.contact (
	id bigserial not null PRIMARY KEY,
	firstname varchar(50) not null,
    lastname varchar(50),
    company varchar(50),
    number varchar(50) not null,
    type varchar(10) default 'HOME',
    address_book_id bigint not null,
    created_by TEXT NOT NULL,
    created_date bigint NOT NULL,
    last_modified_date bigint NOT NULL,
    last_modified_by TEXT NOT NULL,
    constraint contact_uk unique(firstname, lastname, address_book_id),
    constraint contact_address_book_fk
        foreign key(address_book_id)
        references reece.address_book(id) match simple
        on update no action
        on delete cascade
);

Create index idx_contact_address_book_id
	on reece.contact(firstname, address_book_id);

INSERT INTO reece.usr(
    name, created_by, created_date, last_modified_date, last_modified_by)
    VALUES ('ashkan', 'AUDITOR', 1687813586, 1687813586, 'AUDITOR');


INSERT INTO reece.usr(
    name, created_by, created_date, last_modified_date, last_modified_by)
    VALUES ('ava', 'AUDITOR', 1687813586, 1687813586, 'AUDITOR');