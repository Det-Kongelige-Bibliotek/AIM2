# HOWTO

```bash
sudo -u postgres psql
```
```sql
create database aim;
create database "aim-test";
create user aimowner with encrypted password 'mypass';
grant all privileges on database aim to aimowner;
grant all privileges on database "aim-test" to aimowner;
\c aim
set role aimowner;
```

Now paste the content of `create_words.sql` and do the same with `\c "aim-test"`
